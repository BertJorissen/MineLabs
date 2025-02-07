package be.uantwerpen.minelabs.event;

import be.uantwerpen.minelabs.block.entity.BohrBlockEntity;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

public class ClientModsEvents {
    public static void registerEvents() {
        // render bohr block text on hud rendering when a bohr block is being looked at
        HudRenderCallback.EVENT.register(
            (matrixStack, delta) -> {
                MinecraftClient client = MinecraftClient.getInstance();
                HitResult hitResult = client.crosshairTarget;

                assert client.world != null;
                if (hitResult instanceof BlockHitResult blockHitResult) {
                    BlockEntity blockEntity = client.world.getBlockEntity(blockHitResult.getBlockPos());
                    if (blockEntity instanceof BohrBlockEntity) {
                        ((BohrBlockEntity) blockEntity).renderText();
                    }
                }
            }
        );
    }
}
