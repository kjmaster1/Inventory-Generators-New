package com.kjmaster.inventorygenerators.network;

import com.kjmaster.inventorygenerators.generators.InventoryGeneratorGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import java.util.UUID;

public class SyncGeneratorEnergyHelper {

    public static void syncEnergy(int energy, UUID uuid) {
        Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof InventoryGeneratorGui inventoryGeneratorGui) {
            if (uuid.equals(inventoryGeneratorGui.container.getItemUUID())) {
                inventoryGeneratorGui.energyBar.value(energy);
            }
        }
    }
}
