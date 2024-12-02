package com.kjmaster.inventorygenerators.generators;

import mcjty.lib.setup.Registration;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public class InventoryGeneratorItemHandler implements IItemHandlerModifiable {

    final ItemStack generator;
    private final IItemHandlerModifiable generatorInventory;

    public InventoryGeneratorItemHandler(ItemStack generator) {
        this.generator = generator;
        this.generatorInventory = (IItemHandlerModifiable) generator.getCapability(Capabilities.ItemHandler.ITEM);
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        generatorInventory.setStackInSlot(slot, stack);
    }

    @Override
    public int getSlots() {
        return generatorInventory.getSlots();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {;
        return generatorInventory.getStackInSlot(slot);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return generatorInventory.insertItem(slot, stack, simulate);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        return generatorInventory.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return generatorInventory.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return generatorInventory.isItemValid(slot, stack);
    }
}
