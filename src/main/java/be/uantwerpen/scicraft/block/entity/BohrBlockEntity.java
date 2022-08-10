package be.uantwerpen.scicraft.block.entity;

import be.uantwerpen.scicraft.inventory.ImplementedInventory;
import be.uantwerpen.scicraft.item.Items;
import be.uantwerpen.scicraft.network.NetworkingConstants;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
//import net.minecraft.client.MinecraftClient;
//import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import be.uantwerpen.scicraft.util.NuclidesTable;
import be.uantwerpen.scicraft.util.NucleusState;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiling.jfr.sample.NetworkIoStatistics;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Stack;

//import java.util.ArrayList;

public class BohrBlockEntity extends BlockEntity implements ImplementedInventory {

    public long time = 0;
//    public Vec3i movement_direction = Vec3i.ZERO;
    public final static int time_move_ticks = 8;
    public BlockState render_state = net.minecraft.block.Blocks.AIR.getDefaultState();
    public static final int maxTimerAmount = 30;
    public static final IntProperty TIMER = IntProperty.of("timer",0, maxTimerAmount);

    private final DefaultedList<ItemStack> protonInventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
    private final DefaultedList<ItemStack> neutronInventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
    private final DefaultedList<ItemStack> electronInventory = DefaultedList.ofSize(3, ItemStack.EMPTY);

    public BohrBlockEntity( BlockPos pos, BlockState state) {
        super(BlockEntities.BOHR_BLOCK_ENTITY, pos, state);
    }

    public int getProtonCount(){
        return countList(this.getProtonInventory());
    }
    public int getNeutronCount(){
        return countList(this.getNeutronInventory());
    }
    public int getElectronCount(){
        return countList(this.getElectronInventory());
    }

    private int countList(DefaultedList<ItemStack> list) {
        return list.get(0).getCount() + list.get(1).getCount() + list.get(2).getCount();
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return null;
    }
    public void renderText(){
        int nrOfProtons = getProtonCount();
        int nrOfNeutrons = getNeutronCount();
        int nrOfElectrons = getElectronCount();

        MatrixStack matrixStack = new MatrixStack();
        NucleusState nuclideStateInfo = NuclidesTable.getNuclide(nrOfProtons, nrOfNeutrons);
        String protonString = "#Protons: " + nrOfProtons;
        String electronString = "#Electrons: " + nrOfElectrons;
        String neutronString = "#Neutrons: " + nrOfNeutrons;

        if ( nuclideStateInfo != null ) {
            String atomName = nuclideStateInfo.getAtomName();
            String symbol = nuclideStateInfo.getSymbol();
            String mainDecayMode = nuclideStateInfo.getMainDecayMode();

            String ionicCharge = NuclidesTable.calculateIonicCharge(nrOfProtons, nrOfElectrons);
            String ion = "ion: " + ionicCharge;

            String atomInfo = mainDecayMode + "    " + atomName + "    " + symbol + "    " + ion + "    Timer: " + this.getCachedState().get(TIMER);
            MinecraftClient.getInstance().textRenderer.draw(matrixStack, atomInfo, 10, 10, 111455);
        }

        MinecraftClient.getInstance().textRenderer.draw(matrixStack, protonString, 10, 20, 111455);
        MinecraftClient.getInstance().textRenderer.draw(matrixStack, neutronString, 10, 30, 111455);
        MinecraftClient.getInstance().textRenderer.draw(matrixStack, electronString, 10, 40, 111455);
    }
    public DefaultedList<ItemStack> getProtonInventory() {
        return protonInventory;
    }

    @Override
    public void markDirty() {
        super.markDirty();

    }

//    public void inventoryDirty(ServerWorld world){
//        if ( world.isClient()) return;
//        PacketByteBuf buf = PacketByteBufs.create();
//        NbtCompound nbtCompound = this.createNbt();
////        this.writeNbt(nbtCompound);
//        buf.writeBlockPos(pos).writeNbt(nbtCompound);
//        for (ServerPlayerEntity serverPlayerEntity: world.getPlayers()){
//            ServerPlayNetworking.send(serverPlayerEntity, toUpdatePacket, buf);
//        }
//    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
//        Packet<ClientPlayPacketListener> packet = BlockEntityUpdateS2CPacket.create(this);
        return BlockEntityUpdateS2CPacket.create(this);
    }
//    public void markDirty() {
//        if (this.world != null) {
//            BlockEntity.markDirty(this.world, this.pos, this.getCachedState());
//        }
//    }
    public DefaultedList<ItemStack> getNeutronInventory() {
        return neutronInventory;
    }

    public DefaultedList<ItemStack> getElectronInventory() {
        return electronInventory;
    }



    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, this.protonInventory);
        Inventories.readNbt(nbt, this.neutronInventory);
        Inventories.readNbt(nbt, this.electronInventory);
    }


    public void empty(){
        for ( int index = 0; index < 3; index++ ){
            protonInventory.get(index).setCount(0);
            neutronInventory.get(index).setCount(0);
            electronInventory.get(index).setCount(0);
        }
    }

    public ActionResult insertParticle(Item item) {
        DefaultedList<ItemStack> inventory;

        if (item == Items.PROTON) {
            inventory = getProtonInventory();
        }
        else if (item == Items.NEUTRON) {
            inventory = getNeutronInventory();

            }
        else if (item == Items.ELECTRON) {
            inventory = getElectronInventory();

        }
        else {
            return ActionResult.FAIL;
                }


        int i = 0;
        while (inventory.get(i).getCount() == 64 && inventory.get(i).getItem() == item) {
            i += 1;
        }
        if (i == 4) return ActionResult.FAIL;
        assert inventory.get(i).getCount() < 64;
//            if the inventory is empty initialize the inventory with 0 items
        if (inventory.get(i).isEmpty()) {
//                the item that the player was holding
            inventory.set(i, new ItemStack(item));
//                set the counter for the item on 0
            inventory.get(i).setCount(0);
        }
//            if the stack isn't full
        if (inventory.get(i).getCount() < 64) {
//                add 1 to the inventory
            inventory.get(i).setCount(inventory.get(i).getCount() + 1);
        }
        return ActionResult.SUCCESS;
    }


    public void scatterParticles(){
        DefaultedList<ItemStack> allstacks = DefaultedList.ofSize(9);

        allstacks.addAll(getNeutronInventory());
        allstacks.addAll(getElectronInventory());
        allstacks.addAll(getProtonInventory());

        ItemScatterer.spawn(Objects.requireNonNull(getWorld()), getPos().up(1), allstacks);
    }

    /**
     * Removes a particle (proton, neutron or electron depending on passed param) from the bohrblock.
     *
     * @param item : item particle to be removed.
     */
    public ActionResult removeParticle(Item item) {
            if (item == Items.ANTI_PROTON) {
                for (int index = 0; index < 3; index++) {
                    if (protonInventory.get(index).getCount() > 0) {
                        protonInventory.get(index).setCount(protonInventory.get(index).getCount() - 1);
                        return ActionResult.SUCCESS;
                    }
                }
            }
        else if (item == Items.ANTI_NEUTRON) {
                for (int index = 0; index < 3; index++) {
                    if (neutronInventory.get(index).getCount() > 0) {
                        neutronInventory.get(index).setCount(neutronInventory.get(index).getCount() - 1);
                        return ActionResult.SUCCESS;
                    }
                }
            }
        else if (item == Items.POSITRON) {
                for (int index = 0; index < 3; index++) {
                    if (electronInventory.get(index).getCount() > 0) {
                        electronInventory.get(index).setCount(electronInventory.get(index).getCount() - 1);
                        return ActionResult.SUCCESS;
                    }
                }
        }
        return ActionResult.FAIL;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt = Inventories.writeNbt(nbt, this.protonInventory,true);
        nbt = Inventories.writeNbt(nbt, this.neutronInventory,true);
        nbt = Inventories.writeNbt(nbt, this.electronInventory,true);
        super.writeNbt(nbt);
    }

}
