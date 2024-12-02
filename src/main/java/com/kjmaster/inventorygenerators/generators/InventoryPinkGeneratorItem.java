package com.kjmaster.inventorygenerators.generators;

import net.minecraft.world.item.ItemStack;

import static com.kjmaster.inventorygenerators.setup.Config.pinkGeneratorCapacity;

public class InventoryPinkGeneratorItem extends InventoryGeneratorItem {
    public InventoryPinkGeneratorItem() {
        super("inventory_pink_generator");
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        return pinkGeneratorCapacity;
    }
}
