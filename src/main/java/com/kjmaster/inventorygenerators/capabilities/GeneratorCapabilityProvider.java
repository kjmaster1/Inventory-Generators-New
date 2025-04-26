package com.kjmaster.inventorygenerators.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GeneratorCapabilityProvider implements ICapabilitySerializable<CompoundTag> {

    private final ItemStack stack;
    private final ItemStackHandler stackHandler;
    private final EnergyStorageItemStack energyStorageItemStack;

    public GeneratorCapabilityProvider(ItemStack stack, ItemStackHandler stackHandler, EnergyStorageItemStack energyStorageItemStack) {
        this.stack = stack;
        this.stackHandler = stackHandler;
        this.energyStorageItemStack = energyStorageItemStack;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
        if (capability == ForgeCapabilities.ITEM_HANDLER) {
            LazyOptional<IItemHandler> itemHandlerLazyOptional = LazyOptional.of(() -> this.stackHandler);
            return itemHandlerLazyOptional.cast();
        }
        if (capability == ForgeCapabilities.ENERGY) {
            LazyOptional<IEnergyStorage> energyStorageLazyOptional = LazyOptional.of(() -> this.energyStorageItemStack);
            return energyStorageLazyOptional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag inventory = tag.getCompound("inventory");
        Tag energy = tag.get("energy");
        this.stackHandler.deserializeNBT(inventory);
        this.energyStorageItemStack.deserializeNBT(energy);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag inventory = this.stackHandler.serializeNBT();
        Tag energy = this.energyStorageItemStack.serializeNBT();
        tag.put("energy", energy);
        tag.put("inventory", inventory);
        return tag;
    }
}
