package be.uantwerpen.minelabs;

import be.uantwerpen.minelabs.block.Blocks;
import be.uantwerpen.minelabs.block.entity.BlockEntities;
import be.uantwerpen.minelabs.crafting.CraftingRecipes;
import be.uantwerpen.minelabs.entity.Entities;
import be.uantwerpen.minelabs.entity.ScientificVillager;
import be.uantwerpen.minelabs.event.ModEvents;
import be.uantwerpen.minelabs.gui.ScreenHandlers;
import be.uantwerpen.minelabs.item.Items;
import be.uantwerpen.minelabs.paintings.Paintings;
import be.uantwerpen.minelabs.sound.SoundEvents;
import be.uantwerpen.minelabs.world.gen.OreGenerations;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Minelabs implements ModInitializer {

    public static final String MOD_ID = "minelabs";

    // This logger is used to write text to the console and the log file.
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        Items.registerItems();
        Blocks.registerBlocks();
        BlockEntities.registerBlockEntities();
        Entities.registerEntities();
        ExtraDispenserBehavior.registerBehaviors();
        SoundEvents.registerSounds();
        ScreenHandlers.registerScreens();
        Paintings.registerPaintings();
        OreGenerations.generateOres();

        CraftingRecipes.register();

        ScientificVillager.registerVillagers();
        ScientificVillager.registerTrades();
        ModEvents.registerEvents();
    }
}