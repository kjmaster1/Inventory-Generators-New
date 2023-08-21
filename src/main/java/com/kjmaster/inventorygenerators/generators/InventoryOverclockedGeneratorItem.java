package com.kjmaster.inventorygenerators.generators;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;

import static com.kjmaster.inventorygenerators.setup.Config.*;

public class InventoryOverclockedGeneratorItem extends InventoryGeneratorItem {

    public InventoryOverclockedGeneratorItem() {
        super("inventory_overclocked_generator");
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        return overclockedGeneratorCapacity;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return ForgeHooks.getBurnTime(stack, null) > 0 || super.isItemValid(stack);
    }

    @Override
    public int calculateTime(ItemStack stack) {

        return ForgeHooks.getBurnTime(stack, null) > 0 ?
                (int) Math.ceil(ForgeHooks.getBurnTime(stack, null) / overclockedGeneratorDivisor) + 1 : super.calculateTime(stack);

    }

    @Override
    public int calculatePower(ItemStack stack) {

        if (ForgeHooks.getBurnTime(getCurrentFuel(stack), null) > 0) {
            int burnTime = ForgeHooks.getBurnTime(getCurrentFuel(stack), null);
            int minSend = Math.min(burnTime, overclockedGeneratorMinRfPerTick);
            return Math.min(getMaxEnergyStored(stack) - getInternalEnergyStored(stack),  minSend);
        }
        return super.calculatePower(stack);
    }
}
