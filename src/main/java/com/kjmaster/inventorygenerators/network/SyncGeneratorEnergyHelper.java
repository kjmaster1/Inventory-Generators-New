package com.kjmaster.inventorygenerators.network;

import com.kjmaster.inventorygenerators.generators.InventoryGeneratorGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class SyncGeneratorEnergyHelper {

    public static void syncEnergy(int energy) {
        Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof InventoryGeneratorGui inventoryGeneratorGui) {
            inventoryGeneratorGui.energyBar.value(energy);
        }
    }
}
