package com.kjmaster.inventorygenerators.generators;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
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
    public boolean isItemValid(ItemStack generator, ItemStack fuel, Level level) {
        return ForgeHooks.getBurnTime(fuel, RecipeType.SMELTING) > 0 || super.isItemValid(generator, fuel, level);
    }

    @Override
    public int calculateTime(ItemStack generator, ItemStack fuel, Level level) {
        return ForgeHooks.getBurnTime(fuel, RecipeType.SMELTING) > 0 ?
                (int) Math.ceil(ForgeHooks.getBurnTime(fuel, RecipeType.SMELTING) / furnaceGeneratorDivisor) : super.calculateTime(generator, fuel, level);
    }

    @Override
    public int calculatePower(ItemStack generator, Level level) {
        if (ForgeHooks.getBurnTime(getCurrentFuel(generator), RecipeType.SMELTING) > 0) {
            return Math.min(getMaxEnergyStored(generator) - getInternalEnergyStored(generator), furnaceGeneratorRfPerTick);
        }
        return super.calculatePower(generator, level);
    }
}
