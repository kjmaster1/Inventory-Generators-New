package com.kjmaster.inventorygenerators.generators;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
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
    public boolean isItemValid(ItemStack generator, ItemStack fuel, Level level) {
        return ForgeHooks.getBurnTime(fuel, RecipeType.SMELTING) > 0 || super.isItemValid(generator, fuel, level);
    }

    @Override
    public int calculateTime(ItemStack generator, ItemStack fuel, Level level) {
        return ForgeHooks.getBurnTime(fuel, RecipeType.SMELTING) > 0 ?
                ForgeHooks.getBurnTime(fuel, RecipeType.SMELTING) : super.calculateTime(generator, fuel, level);
    }

    @Override
    public int calculatePower(ItemStack generator, Level level) {
        if (ForgeHooks.getBurnTime(getCurrentFuel(generator), RecipeType.SMELTING) > 0) {
            return Math.min(getMaxEnergyStored(generator) - getInternalEnergyStored(generator), survivalistGeneratorRfPerTick);
        }
        return super.calculatePower(generator, level);
    }
}
