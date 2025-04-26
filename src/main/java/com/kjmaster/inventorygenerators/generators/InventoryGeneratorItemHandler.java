package com.kjmaster.inventorygenerators.generators;

import com.kjmaster.inventorygenerators.InventoryGenerators;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

public class InventoryGeneratorItemHandler implements IItemHandlerModifiable {

    final ItemStack generator;
    private IItemHandlerModifiable generatorInventory;

    public InventoryGeneratorItemHandler(ItemStack generator) {
        this.generator = generator;
        LazyOptional<IItemHandler> itemHandlerLazyOptional = generator.getCapability(ForgeCapabilities.ITEM_HANDLER);
        try {
            this.generatorInventory = (IItemHandlerModifiable) itemHandlerLazyOptional.orElseThrow(Exception::new);
        } catch (Exception e) {
            InventoryGenerators.LOGGER.error("No IItemHandlerModifiable capability found on generator!", e);
        }
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
    public @NotNull ItemStack getStackInSlot(int slot) {
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
