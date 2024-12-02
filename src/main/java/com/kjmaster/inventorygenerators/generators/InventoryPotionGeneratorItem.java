package com.kjmaster.inventorygenerators.generators;

import net.minecraft.world.item.ItemStack;

import static com.kjmaster.inventorygenerators.setup.Config.potionGeneratorCapacity;

public class InventoryPotionGeneratorItem extends InventoryGeneratorItem {

    public InventoryPotionGeneratorItem() {
        super("inventory_potion_generator");
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        return potionGeneratorCapacity;
    }
}
