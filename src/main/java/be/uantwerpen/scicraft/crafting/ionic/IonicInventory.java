package be.uantwerpen.scicraft.crafting.ionic;

import be.uantwerpen.scicraft.lewisrecipes.LewisCraftingGrid;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;

import static be.uantwerpen.scicraft.gui.ionic_gui.IonicBlockScreenHandler.GRIDSIZE;

public class IonicInventory extends SimpleInventory {

    public IonicInventory(int gridLeft, int gridRight, int other) {
        super(gridLeft + gridRight + other);
    }

    public LewisCraftingGrid getLeftGrid() {
        ItemStack[] items = new ItemStack[9];
        for (int i = 0; i < 9; i++) {
            items[i] = this.getStack(i);
        }
        return new LewisCraftingGrid(3 ,3 ,items);
    }

    public LewisCraftingGrid getRightGrid() {
        ItemStack[] items = new ItemStack[9];
        for (int i = 0; i < GRIDSIZE; i++) {
            items[i] = this.getStack(i + GRIDSIZE);
        }
        return new LewisCraftingGrid(3, 3, items);
    }
}
