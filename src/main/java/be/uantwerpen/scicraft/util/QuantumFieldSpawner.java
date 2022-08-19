package be.uantwerpen.scicraft.util;

import be.uantwerpen.scicraft.block.AtomicFloor;
import be.uantwerpen.scicraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import static be.uantwerpen.scicraft.block.QuantumfieldBlock.MASTER;
import static be.uantwerpen.scicraft.block.entity.QuantumFieldBlockEntity.AGE;
import static be.uantwerpen.scicraft.block.entity.QuantumFieldBlockEntity.max_age;

public class QuantumFieldSpawner {
    static java.util.Random r = new java.util.Random();
    public static void tryToSpawnCloud(World world, BlockPos pos) {
        BlockState field_to_spawn;
        float chance = (float) 1 / 1_000;

        if (shouldSpawnCloud(chance)) {
            field_to_spawn = pickField(world, pos);
            if (field_to_spawn != null) {
                spawnCloud(world, pos, field_to_spawn);
            }
        }
    }

    public static Boolean shouldSpawnCloud(float chance) {
        java.util.Random r = new java.util.Random();
        return r.nextFloat() <= chance;
    }

    public static BlockState pickField(World world, BlockPos pos) {
        BlockState quantumfield = null;
        int type = r.nextInt(7);
        //Only change air blocks so other fields don't get replaced
        switch (type) {
            case 0 -> quantumfield = Blocks.ELECTRON_QUANTUMFIELD.getDefaultState();
            case 1 -> quantumfield = Blocks.DOWNQUARK_QUANTUMFIELD.getDefaultState();
            case 2 -> quantumfield = Blocks.GLUON_QUANTUMFIELD.getDefaultState();
            case 3 -> quantumfield = Blocks.PHOTON_QUANTUMFIELD.getDefaultState();
            case 4 -> quantumfield = Blocks.UPQUARK_QUANTUMFIELD.getDefaultState();
            case 5 -> quantumfield = Blocks.WEAK_BOSON_QUANTUMFIELD.getDefaultState();
            case 6 -> quantumfield = Blocks.NEUTRINO_QUANTUMFIELD.getDefaultState();
        }
        return quantumfield;
    }



    private static void spawnCloud(World world, BlockPos pos, BlockState state) {

        int x_size = r.nextInt(5, 25);
        int y_size = r.nextInt(4, 15);
        int z_size = r.nextInt(5, 25);
        int max_cloud_height = 100;
        int cloudHeight = r.nextInt(0, max_cloud_height) - max_cloud_height / 2;

        BlockPos blockPos;
        pos = pos.up(cloudHeight);
        float a = x_size / 2.0f;
        float b = y_size / 2.0f;
        float c = z_size / 2.0f;
        float x_sub, y_sub, z_sub;
        BlockState tempState;

//      only replace air and not too close to other fields
        /**
         * Elipse:   x^2/a^2 + y^2/b^2 + z^2/c^2 = 1
         * (a,0,0); (0,b,0),(0,0,c)
         */
        if (!checkFields(pos, world, x_size, y_size, z_size)) {
            world.setBlockState(pos, state.getBlock().getDefaultState().with(MASTER, true));
            for (int x = -x_size / 2; x <= x_size / 2; x++) {
//                x_sub = x^2/a^2
                x_sub = x * x / (a * a);
                for (int y = -y_size / 2; y <= y_size / 2; y++) {
//                y_sub = y^2/b^2
                    y_sub = y * y / (b * b);
                    for (int z = -z_size / 2; z <= z_size / 2; z++) {
//                z_sub = z^2/b^2
                        z_sub = z * z / (c * c);
                        if (x_sub + z_sub + y_sub <= 1.0f) {
                            blockPos = new BlockPos(x + pos.getX(), y + pos.getY(), z + pos.getZ());
                            tempState = world.getBlockState(blockPos);
                            if (tempState.isAir() || tempState.getBlock().equals(Blocks.ATOM_FLOOR)) {
                                world.setBlockState(blockPos, state.getBlock().getDefaultState());
                            }
                        }
                    }
                }
            }

        }
    }

    //Checking if there are no other clouds near
    private static Boolean checkFields(BlockPos pos, World world, int x_size, int y_size, int z_size) {
        int blocks_between = 5;
        BlockPos pos1 = pos.subtract(new Vec3i(x_size / 2 + blocks_between,z_size / 2 + blocks_between,y_size / 2 + blocks_between));
        BlockPos pos2 = pos.add(new Vec3i(x_size / 2 + blocks_between,z_size / 2 + blocks_between,y_size / 2 + blocks_between));
        BlockState tempState;
        Box cloudbox = new Box(pos,pos2);
        if (world instanceof ServerWorld serverWorld){
            for (ServerPlayerEntity serverPlayerEntity : serverWorld.getPlayers()){
                if (cloudbox.contains(serverPlayerEntity.getPos())){
                    return true;
                }
            }
        }
        for (int x = pos1.getX(); x <= pos2.getX(); x++) {

            for (int y = pos1.getY(); y <= pos2.getY(); y++) {
                for (int z = pos1.getZ(); z <= pos2.getZ() ; z++) {
                    tempState = world.getBlockState(new BlockPos(x, y, z));
                    if (!(tempState.isAir() || tempState.getBlock().equals(Blocks.ATOM_FLOOR))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
