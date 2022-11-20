package be.uantwerpen.minelabs;

import be.uantwerpen.minelabs.block.Blocks;
import be.uantwerpen.minelabs.block.entity.BlockEntities;
import be.uantwerpen.minelabs.entity.Entities;
import be.uantwerpen.minelabs.event.ClientModsEvents;
import be.uantwerpen.minelabs.fluid.Fluids;
import be.uantwerpen.minelabs.gui.ScreenHandlers;
import be.uantwerpen.minelabs.gui.ionic_gui.IonicScreen;
import be.uantwerpen.minelabs.gui.lab_chest_gui.LabChestScreen;
import be.uantwerpen.minelabs.gui.lewis_gui.LewisScreen;
import be.uantwerpen.minelabs.item.ItemModels;
import be.uantwerpen.minelabs.item.Items;
import be.uantwerpen.minelabs.model.ModelProvider;
import be.uantwerpen.minelabs.network.IonicDataPacket;
import be.uantwerpen.minelabs.network.LewisDataPacket;
import be.uantwerpen.minelabs.network.NetworkingConstants;
import be.uantwerpen.minelabs.renderer.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.item.Item;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import java.util.Map;


@SuppressWarnings("UNUSED")
@Environment(EnvType.CLIENT)
public class MinelabsClient implements ClientModInitializer {
    @Override()
    public void onInitializeClient() {
        ClientModsEvents.registerEvents();

        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.ATOM_FLOOR, RenderLayer.getTranslucent());
        //BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PORTAL_BLOCK, RenderLayer.getTranslucent());

        //Register ItemModels
        ItemModels.registerModels();


        ModelLoadingRegistry.INSTANCE.registerResourceProvider(ModelProvider::new);
        ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> {
            Map<Identifier, Resource> molecules = manager.findResources("models/molecules", (i) -> true);
            for (Identifier resource: molecules.keySet()) {
                out.accept(new Identifier(resource.getNamespace(), resource.getPath().split("models/")[1].split(".json")[0]));
            }
            out.accept(new Identifier(Minelabs.MOD_ID,"block/mologram_beam"));
        });


        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PION_NUL, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PION_MINUS, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PION_PLUS, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.WEAK_BOSON, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.NEUTRINO, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.ANTINEUTRINO, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.POSITRON, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.ELECTRON, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.NEUTRON, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.ANTI_NEUTRON, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.PROTON, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.ANTI_PROTON, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.SALT_CRYSTAL, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.LARGE_SALT_CRYSTAL, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.MEDIUM_SALT_CRYSTAL, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.SMALL_SALT_CRYSTAL, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.SALT_WIRE, RenderLayer.getCutout());

        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.MOLOGRAM_BLOCK, RenderLayer.getTranslucent());
        BlockEntityRendererRegistry.register(BlockEntities.MOLOGRAM_BLOCK_ENTITY, MologramBlockEntityRenderer::new);

        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.GREEN_FIRE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.HELIUM, RenderLayer.getTranslucent());

        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.ERLENMEYER_STAND, RenderLayer.getCutout());
        // Register rendering for electron entity
        EntityRendererRegistry.register(Entities.ELECTRON_ENTITY, FlyingItemEntityRenderer::new);
        EntityRendererRegistry.register(Entities.PROTON_ENTITY, FlyingItemEntityRenderer::new);
        EntityRendererRegistry.register(Entities.ANTI_PROTON_ENTITY, FlyingItemEntityRenderer::new);
        EntityRendererRegistry.register(Entities.NEUTRON_ENTITY, FlyingItemEntityRenderer::new);
        EntityRendererRegistry.register(Entities.ANTI_NEUTRON_ENTITY, FlyingItemEntityRenderer::new);
        EntityRendererRegistry.register(Entities.ENTROPY_CREEPER, EntropyCreeperEntityRenderer::new);

        BlockEntityRendererRegistry.register(BlockEntities.BOHR_BLOCK_ENTITY, BohrBlockEntityRenderer::new);

        BlockEntityRendererRegistry.register(BlockEntities.ANIMATED_CHARGED_BLOCK_ENTITY, ChargedBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(BlockEntities.CHARGED_PLACEHOLDER_BLOCK_ENTITY, ChargedPlaceholderBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(BlockEntities.ELECTRIC_FIELD_SENSOR, ElectricFieldSensorRenderer::new);
        //BlockEntityRendererRegistry.register(BlockEntities.LAB_CHEST_BLOCK_ENTITY, LabChestBlockEntityRenderer::new);

        // Register rendering lewis crafting table inventory
        HandledScreens.register(ScreenHandlers.LEWIS_SCREEN_HANDLER, LewisScreen::new);

        ScreenEvents.BEFORE_INIT.register((a, screen, b, c) -> {
            if (screen instanceof LewisScreen)
                ScreenMouseEvents.afterMouseRelease(screen).register((d, mouseX, mouseY, e) -> ((LewisScreen) screen).getButtonWidget().onClick(mouseX, mouseY));
        });


        //Register rendering ionic block inventory
        HandledScreens.register(ScreenHandlers.IONIC_SCREEN_HANDLER, IonicScreen::new);
        HandledScreens.register(ScreenHandlers.LAB_CHEST_SCREEN_HANDLER, LabChestScreen::new);

//        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.LEWIS_BLOCK, RenderLayer.getTranslucent());

        // Gas

        // TODO - toevoegen van 02, N2, ... als extra texture voor kleurloze gassen.
        // TODO - enchantment visuals voor zeldzame stoffen

        // GASSES
        registerErlenmeyer(Items.ERLENMEYER_O2, 0xAFAFAF, 0);
        registerErlenmeyer(Items.ERLENMEYER_N2, 0xAFAFAF, 0);
        registerErlenmeyer(Items.ERLENMEYER_CH4, 0xAFAFAF, 0);
        registerErlenmeyer(Items.ERLENMEYER_H2, 0xAFAFAF, 0);
        registerErlenmeyer(Items.ERLENMEYER_NO, 0xAFAFAF, 0);
        registerErlenmeyer(Items.ERLENMEYER_NO2, 0x991c00, 0);
        registerErlenmeyer(Items.ERLENMEYER_Cl2, 0xE8F48C, 0);
        registerErlenmeyer(Items.ERLENMEYER_CO2, 0xAFAFAF, 0);
        registerErlenmeyer(Items.ERLENMEYER_CO, 0xAFAFAF, 0);
        registerErlenmeyer(Items.ERLENMEYER_NH3, 0xAFAFAF, 0);
        registerErlenmeyer(Items.ERLENMEYER_N2O, 0xAFAFAF, 0);
        registerErlenmeyer(Items.ERLENMEYER_HCl, 0xAFAFAF, 0);
        registerErlenmeyer(Items.ERLENMEYER_He, 0xAFAFAF, 0);
        registerErlenmeyer(Items.ERLENMEYER_Ne, 0xAFAFAF, 0);
        registerErlenmeyer(Items.ERLENMEYER_Ar, 0xAFAFAF, 0);
        registerErlenmeyer(Items.ERLENMEYER_CL2O,0xAFAFAF, 0);
        registerErlenmeyer(Items.ERLENMEYER_H2CO3,0xAFAFAF, 0);
        registerErlenmeyer(Items.ERLENMEYER_CH4S,0xAFAFAF, 0);
        registerErlenmeyer(Items.ERLENMEYER_CH2O,0xAFAFAF, 0);
        //Fluids
        registerErlenmeyer(Items.ERLENMEYER_HNO3, 0xFFCC33, 0);
        registerErlenmeyer(Items.ERLENMEYER_H2O, 0x3495eb, 0);
        registerErlenmeyer(Items.ERLENMEYER_NCL3, 0xe8dc5a, 0);
        registerErlenmeyer(Items.ERLENMEYER_CS2, 0xAFAFAF, 0);
        registerErlenmeyer(Items.ERLENMEYER_CCL4, 0xAFAFAF, 0);
        registerErlenmeyer(Items.ERLENMEYER_PCl3, 0xAFAFAF, 0);
        registerErlenmeyer(Items.ERLENMEYER_SCl2, 0xAFAFAF, 0);
        registerErlenmeyer(Items.ERLENMEYER_HCN, 0xCCCCFF, 0);
        registerErlenmeyer(Items.ERLENMEYER_CH4O, 0xAFAFAF, 0);
//        public static Block ACID = Registry.register(Registry.BLOCK, new Identifier(Minelabs.MOD_ID, "acid"), new FluidBlock(be.uantwerpen.minelabs.item.Items.STILL_ACID, FabricBlockSettings.copy(net.minecraft.block.)){});
        FluidRenderHandlerRegistry.INSTANCE.register(Fluids.STILL_HNO3, Fluids.FLOWING_HNO3, new SimpleFluidRenderHandler(
                new Identifier("minecraft:block/water_still"),
                new Identifier("minecraft:block/water_flow"),
                0xA1FFCC33 // ARGB (alpha / R / G /B)
        ));
        FluidRenderHandlerRegistry.INSTANCE.register(Fluids.STILL_H2O, Fluids.FLOWING_H2O, new SimpleFluidRenderHandler(
                new Identifier("minecraft:block/water_still"),
                new Identifier("minecraft:block/water_flow"),
                0xA10084FF // ARGB (alpha / R / G /B)
        ));
        FluidRenderHandlerRegistry.INSTANCE.register(Fluids.STILL_NCl3, Fluids.FLOWING_NCl3, new SimpleFluidRenderHandler(
                new Identifier("minecraft:block/water_still"),
                new Identifier("minecraft:block/water_flow"),
                0xA1E8DC5A
        ));
        FluidRenderHandlerRegistry.INSTANCE.register(Fluids.STILL_HCN, Fluids.FLOWING_HCN, new SimpleFluidRenderHandler(
                new Identifier("minecraft:block/water_still"),
                new Identifier("minecraft:block/water_flow"),
                0xA1CCCCFF));
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(),
                Fluids.STILL_HNO3, Fluids.FLOWING_HNO3,
                Fluids.STILL_H2O, Fluids.FLOWING_H2O,
                Fluids.STILL_HCN, Fluids.FLOWING_HCN,
                Fluids.STILL_NCl3, Fluids.FLOWING_NCl3);
        //Lewis Data Sync
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.LEWISDATASYNC, (c, h, b, s) -> LewisDataPacket.receive(c.world, b, s));
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.IONICDATASYNC, (c, h, b, s) -> IonicDataPacket.receive(c.world, b, s));
        NetworkingConstants.registerC2SPackets();
        ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> 0x3495eb, Blocks.LAB_SINK);
    }

    public void registerErlenmeyer(Item item, int color, int index) {
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            if (tintIndex == index) return color;
            return 0xFFFFFF;
        }, item);
    }

}

