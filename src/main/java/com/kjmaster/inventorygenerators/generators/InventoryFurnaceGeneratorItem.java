package com.kjmaster.inventorygenerators.generators;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

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
    public boolean isItemValid(ItemStack generator, ItemStack fuel, Level level) {
        return fuel.getBurnTime(null) > 0 || super.isItemValid(generator, fuel, level);
    }

    @Override
    public int calculateTime(ItemStack generator, ItemStack fuel, Level level) {
        return fuel.getBurnTime(null) > 0 ?
                (int) Math.ceil(fuel.getBurnTime(null) / furnaceGeneratorDivisor) : super.calculateTime(generator, fuel, level);
    }

    @Override
    public int calculatePower(ItemStack generator, Level level) {
        if (getCurrentFuel(generator).getBurnTime(null) > 0) {
            return Math.min(getMaxEnergyStored(generator) - getInternalEnergyStored(generator), furnaceGeneratorRfPerTick);
        }
        return super.calculatePower(generator, level);
    }
}
