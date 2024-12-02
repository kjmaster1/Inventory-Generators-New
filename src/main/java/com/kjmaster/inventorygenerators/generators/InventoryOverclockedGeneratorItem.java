package com.kjmaster.inventorygenerators.generators;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

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
    public boolean isItemValid(ItemStack generator, ItemStack fuel, Level level) {
        return fuel.getBurnTime(null) > 0 || super.isItemValid(generator, fuel, level);
    }

    @Override
    public int calculateTime(ItemStack generator, ItemStack fuel, Level level) {
        return fuel.getBurnTime(null) > 0 ?
                (int) Math.ceil(fuel.getBurnTime(null) / overclockedGeneratorDivisor) + 1 : super.calculateTime(generator, fuel, level);
    }

    @Override
    public int calculatePower(ItemStack generator, Level level) {
        if (getCurrentFuel(generator).getBurnTime(null) > 0) {
            int burnTime = getCurrentFuel(generator).getBurnTime(null);
            int minSend = Math.min(burnTime, overclockedGeneratorMinRfPerTick);
            return Math.min(getMaxEnergyStored(generator) - getInternalEnergyStored(generator), minSend);
        }
        return super.calculatePower(generator, level);
    }
}
