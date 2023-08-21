package com.kjmaster.inventorygenerators.generators;

import net.minecraft.world.item.ItemStack;

import static com.kjmaster.inventorygenerators.setup.Config.endGeneratorCapacity;

public class InventoryEndGeneratorItem extends InventoryGeneratorItem {

    public InventoryEndGeneratorItem() {
        super("inventory_end_generator");
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        return endGeneratorCapacity;
    }
}
