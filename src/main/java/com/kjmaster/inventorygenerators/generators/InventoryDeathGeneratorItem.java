package com.kjmaster.inventorygenerators.generators;

import net.minecraft.world.item.ItemStack;

import static com.kjmaster.inventorygenerators.setup.Config.deathGeneratorCapacity;

public class InventoryDeathGeneratorItem extends InventoryGeneratorItem {

    public InventoryDeathGeneratorItem() {
        super("inventory_death_generator");
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        return deathGeneratorCapacity;
    }
}
