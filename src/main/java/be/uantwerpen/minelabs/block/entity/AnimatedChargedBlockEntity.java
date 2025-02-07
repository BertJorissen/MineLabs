package be.uantwerpen.minelabs.block.entity;

import be.uantwerpen.minelabs.item.Items;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AnimatedChargedBlockEntity extends BlockEntity {
    public long time = 0;
    public Direction movement_direction;
    public final static int time_move_ticks = 4;
    public BlockState render_state = net.minecraft.block.Blocks.AIR.getDefaultState();
    public boolean annihilation = false;

    public AnimatedChargedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        tag.putBoolean("an", annihilation);
        tag.putLong("time", time);
        tag.putInt("md", movement_direction.getId());
        tag.put("rs",NbtHelper.fromBlockState(render_state));
        super.writeNbt(tag);
    }

    // Deserialize the BlockEntity
    @Override
    public void readNbt(NbtCompound tag) {
        annihilation = tag.getBoolean("an");
        time = tag.getLong("time");
        movement_direction = Direction.byId(tag.getInt("md"));
        render_state = NbtHelper.toBlockState(tag.getCompound("rs"));
        super.readNbt(tag);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbtWithIdentifyingData();
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (time == 0) {
            time = world.getTime();
            world.updateListeners(pos,state,state,3);
        }
        if (!world.isClient) {
            if (world.getTime() - time > time_move_ticks) {
                world.removeBlockEntity(pos);
                world.removeBlock(pos, false);
                BlockPos blockPos = pos.mutableCopy().offset(movement_direction);
                if (world.getBlockState(blockPos).getBlock().equals(be.uantwerpen.minelabs.block.Blocks.CHARGED_PLACEHOLDER)) { //also change other particle for client
                    world.setBlockState(blockPos, render_state, Block.NOTIFY_ALL);
                }
                if (annihilation) {
                    ItemStack itemStack = new ItemStack(Items.PHOTON, 1);
                    double a = pos.getX() + movement_direction.getVector().getX() / 2d;
                    double b = pos.getY() + movement_direction.getVector().getY() / 2d;
                    double c = pos.getZ() + movement_direction.getVector().getZ() / 2d;
                    ItemEntity itemEntity = new ItemEntity(world, a, b, c, itemStack);
                    world.spawnEntity(itemEntity);
                }
                markDirty();
            }
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, AnimatedChargedBlockEntity be) {
        be.tick(world, pos, state);
    }

}
