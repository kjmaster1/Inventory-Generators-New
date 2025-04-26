package com.kjmaster.inventorygenerators.recipe;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public record GeneratorRecipeInput(ItemStack generator, ItemStack fuel) implements Container {

    @Override
    public int getContainerSize() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return this.generator.isEmpty() && this.fuel.isEmpty();
    }

    @Override
    public @NotNull ItemStack getItem(int i) {
        ItemStack itemStack;
        switch (i) {
            case 0 -> itemStack = this.generator;
            case 1 -> itemStack = this.fuel;
            default -> throw new IllegalArgumentException("Recipe does not contain slot " + i);
        }
        return itemStack;
    }

    public ItemStack generator() {
        return this.generator;
    }

    public ItemStack fuel() {
        return this.fuel;
    }

    @Override
    public ItemStack removeItem(int i, int i1) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        return null;
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {

    }

    @Override
    public void setChanged() {

    }

    @Override
    public boolean stillValid(Player player) {
        return false;
    }


    @Override
    public void clearContent() {

    }
}
