package com.kjmaster.inventorygenerators.generators;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;

import static com.kjmaster.inventorygenerators.setup.Config.survivalistGeneratorCapacity;
import static com.kjmaster.inventorygenerators.setup.Config.survivalistGeneratorRfPerTick;

public class InventorySurvivalistGeneratorItem extends InventoryGeneratorItem {

    public InventorySurvivalistGeneratorItem() {
        super("inventory_survivalist_generator");
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        return survivalistGeneratorCapacity;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return ForgeHooks.getBurnTime(stack, null) > 0 || super.isItemValid(stack);
    }

    @Override
    public int calculateTime(ItemStack stack) {
        return ForgeHooks.getBurnTime(stack, null) > 0 ?
                ForgeHooks.getBurnTime(stack, null) : super.calculateTime(stack);
    }

    @Override
    public int calculatePower(ItemStack stack) {

        if (ForgeHooks.getBurnTime(getCurrentFuel(stack), null) > 0) {
            return Math.min(getMaxEnergyStored(stack) - getInternalEnergyStored(stack), survivalistGeneratorRfPerTick);
        }
        return super.calculatePower(stack);
    }
}
