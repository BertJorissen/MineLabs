package be.uantwerpen.minelabs;

import be.uantwerpen.minelabs.entity.ElectronEntity;
import be.uantwerpen.minelabs.entity.NeutronEntity;
import be.uantwerpen.minelabs.entity.ProtonEntity;
import be.uantwerpen.minelabs.entity.AntiProtonEntity;
import be.uantwerpen.minelabs.entity.AntiNeutronEntity;
import be.uantwerpen.minelabs.item.Items;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class ExtraDispenserBehavior {
    /**
     * Main class method
     * Registers all dispenser behaviors
     */
    public static void registerBehaviors() {
        /**
         * Register dispenser behavior for shooting out electrons
         * Implements abstract class ProjectileDispenserBehavior to create the correct entity
         *
         * The entity is shot up slight as defined in dispenseSilently in ProjectileDispenserBehavior:
         * direction.getOffsetY() + 0.1F
         */
        DispenserBlock.registerBehavior(Items.ELECTRON, new ProjectileDispenserBehavior() {
            @Override
            protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
                return Util.make(new ElectronEntity(world, position.getX(), position.getY(), position.getZ()), (electronEntity) -> electronEntity.setItem(stack));
            }
        });

        /**
         * Register dispenser behavior for shooting out protons
         * Implements abstract class ProjectileDispenserBehavior to create the correct entity
         *
         * The entity is shot up slight as defined in dispenseSilently in ProjectileDispenserBehavior:
         * direction.getOffsetY() + 0.1F
         */
        DispenserBlock.registerBehavior(Items.PROTON, new ProjectileDispenserBehavior() {
            @Override
            protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
                return Util.make(new ProtonEntity(world, position.getX(), position.getY(), position.getZ()), (protonEntity) -> protonEntity.setItem(stack));
            }
        });

        DispenserBlock.registerBehavior(Items.ANTI_PROTON, new ProjectileDispenserBehavior() {
            @Override
            protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
                return Util.make(new AntiProtonEntity(world, position.getX(), position.getY(), position.getZ()), (AntiprotonEntity) -> {
                    AntiprotonEntity.setItem(stack);
                });
            }
        });

        /**
         * Register dispenser behavior for shooting out neutrons
         * Implements abstract class {@link ProjectileDispenserBehavior} to create the correct entity
         *
         * The entity is shot up slight as defined in dispenseSilently in ProjectileDispenserBehavior:
         * direction.getOffsetY() + 0.1F
         */
        DispenserBlock.registerBehavior(Items.NEUTRON, new ProjectileDispenserBehavior() {
            @Override
            protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
                return Util.make(new NeutronEntity(world, position.getX(), position.getY(), position.getZ()), (neutronEntity) -> neutronEntity.setItem(stack));
            }
        });

        DispenserBlock.registerBehavior(Items.ANTI_NEUTRON, new ProjectileDispenserBehavior() {
            @Override
            protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
                return Util.make(new AntiNeutronEntity(world, position.getX(), position.getY(), position.getZ()), (antiNeutronEntity) -> {
                    antiNeutronEntity.setItem(stack);
                });
            }
        });

        /**
         * Register dispenser behavior for using Entropy Creeper Spawn Egg
         * Implements abstract class {@link ItemDispenserBehavior} to summon the correct entity
         */
        DispenserBlock.registerBehavior(Items.ENTROPY_CREEPER_SPAWN_EGG, new ItemDispenserBehavior() {
            @Override
            protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                Direction direction = pointer.getBlockState().get(DispenserBlock.FACING);
                EntityType<?> entityType = ((SpawnEggItem) stack.getItem()).getEntityType(stack.getNbt());

                try {
                    entityType.spawnFromItemStack(pointer.getWorld(), stack, null, pointer.getPos().offset(direction), SpawnReason.DISPENSER, direction != Direction.UP, false);
                } catch (Exception var6) {
                    LOGGER.error("Error while dispensing spawn egg from dispenser at {}", pointer.getPos(), var6);
                    return ItemStack.EMPTY;
                }

                stack.decrement(1);
                pointer.getWorld().emitGameEvent(null, GameEvent.ENTITY_PLACE, pointer.getPos());
                return stack;
            }
        });
    }
}
