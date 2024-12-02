package com.kjmaster.inventorygenerators.generators;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;

public interface IInventoryGenerator {

    void giveDataComponents(ItemStack stack);

    boolean isItemValid(ItemStack generator, ItemStack fuel, Level level);

    int calculateTime(ItemStack generator, ItemStack fuel, Level level);

    int calculatePower(ItemStack generator, Level level);

    boolean isInChargingMode(ItemStack stack);

    void changeMode(ItemStack stack, IInventoryGenerator inventoryGenerator, Player player);

    boolean isOn(ItemStack stack);

    void turnOn(ItemStack stack);

    int getBurnTime(ItemStack stack);

    void setBurnTime(ItemStack stack, int burnTime);

    ItemStack getFuel(ItemStack stack, Level level);

    void receiveInternalEnergy(ItemStack stack, int energy);

    int getInternalEnergyStored(ItemStack stack);

    ArrayList<ItemStack> getChargeables(Player player);

    void giveEnergyToChargeables(ArrayList<ItemStack> chargeables, ItemStack stack);
}
