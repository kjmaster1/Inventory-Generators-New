package com.kjmaster.inventorygenerators.generators;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;

import static com.kjmaster.inventorygenerators.setup.Config.*;

public class InventoryFurnaceGeneratorItem extends InventoryGeneratorItem {

    public InventoryFurnaceGeneratorItem() {
        super("inventory_furnace_generator");
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        return furnaceGeneratorCapacity;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {

        return ForgeHooks.getBurnTime(stack, null) > 0 || super.isItemValid(stack);
    }

    @Override
    public int calculateTime(ItemStack stack) {

        return ForgeHooks.getBurnTime(stack, null) > 0 ?
                (int) Math.ceil(ForgeHooks.getBurnTime(stack, null) / furnaceGeneratorDivisor) : super.calculateTime(stack);

    }

    @Override
    public int calculatePower(ItemStack stack) {

        if (ForgeHooks.getBurnTime(getCurrentFuel(stack), null) > 0) {
            return Math.min(getMaxEnergyStored(stack) - getInternalEnergyStored(stack),  furnaceGeneratorRfPerTick);
        }
        return super.calculatePower(stack);
    }
}
