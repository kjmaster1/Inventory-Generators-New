package com.kjmaster.inventorygenerators.generators;

import net.minecraft.world.item.ItemStack;

import static com.kjmaster.inventorygenerators.setup.Config.frostyGeneratorCapacity;

public class InventoryFrostyGeneratorItem extends InventoryGeneratorItem {

    public InventoryFrostyGeneratorItem() {
        super("inventory_frosty_generator");
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        return frostyGeneratorCapacity;
    }
}
