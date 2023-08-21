package com.kjmaster.inventorygenerators.generators;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

public interface IInventoryGenerator {

    void giveTagCompound(ItemStack stack);

    boolean isItemValid(ItemStack stack);

    int calculatePower(ItemStack stack);

    int calculateTime(ItemStack stack);

    boolean isInChargingMode(ItemStack stack);

    void changeMode(ItemStack stack, IInventoryGenerator inventoryGenerator, Player player);

    boolean isOn(ItemStack stack);

    void turnOn(ItemStack stack);

    int getBurnTime(ItemStack stack);

    void setBurnTime(ItemStack stack, int burnTime);

    ItemStack getFuel(ItemStack stack);

    void receiveInternalEnergy(ItemStack stack, int energy);

    int getInternalEnergyStored(ItemStack stack);

    ArrayList<ItemStack> getChargeables(Player player);

    void giveEnergyToChargeables(ArrayList<ItemStack> chargeables, ItemStack stack);

    ItemStack getCurrentFuel(ItemStack stack);
}
