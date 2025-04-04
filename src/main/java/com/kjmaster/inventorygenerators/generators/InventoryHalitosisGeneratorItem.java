package com.kjmaster.inventorygenerators.generators;

import net.minecraft.world.item.ItemStack;

import static com.kjmaster.inventorygenerators.setup.Config.halitosisGeneratorCapacity;

public class InventoryHalitosisGeneratorItem extends InventoryGeneratorItem {

    public InventoryHalitosisGeneratorItem() {
        super("inventory_halitosis_generator");
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        return halitosisGeneratorCapacity;
    }
}
