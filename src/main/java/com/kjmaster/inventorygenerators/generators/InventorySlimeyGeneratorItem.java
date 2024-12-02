package com.kjmaster.inventorygenerators.generators;

import net.minecraft.world.item.ItemStack;

import static com.kjmaster.inventorygenerators.setup.Config.slimeyGeneratorCapacity;

public class InventorySlimeyGeneratorItem extends InventoryGeneratorItem {

    public InventorySlimeyGeneratorItem() {
        super("inventory_slimey_generator");
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        return slimeyGeneratorCapacity;
    }
}
