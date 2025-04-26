package com.kjmaster.inventorygenerators.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.EnergyStorage;

public class EnergyStorageItemStack extends EnergyStorage {

    protected final ItemStack itemStack;

    public EnergyStorageItemStack(int capacity, ItemStack itemStack) {
        super(capacity, 0, capacity, 0);
        this.itemStack = itemStack;
        this.energy = itemStack.getOrCreateTag().getInt("energy");
    }

    public void setEnergy(int energy) {
        this.energy = energy;
        itemStack.getOrCreateTag().putInt("energy", energy);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive())
            return 0;

        int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
        if (!simulate) {
            energy += energyReceived;
            setEnergy(energy);
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract())
            return 0;

        int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
        if (!simulate) {
            energy -= energyExtracted;
            setEnergy(energy);
        }
        return energyExtracted;
    }

    @Override
    public int getEnergyStored() {
        CompoundTag tag = itemStack.getOrCreateTag();
        if (tag.contains("energy")) {
            return tag.getInt("energy");
        }
        return 0;
    }

    @Override
    public int getMaxEnergyStored() {
        return capacity;
    }

    @Override
    public boolean canExtract() {
        return this.maxExtract > 0;
    }

    @Override
    public boolean canReceive() {
        return this.maxReceive > 0;
    }
}
