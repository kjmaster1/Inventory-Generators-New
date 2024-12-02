package com.kjmaster.inventorygenerators.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.NotNull;

public record GeneratorRecipeInput(ItemStack generator, ItemStack fuel) implements RecipeInput {

    public @NotNull ItemStack getItem(int p_346205_) {
        ItemStack var10000;
        switch (p_346205_) {
            case 0 -> var10000 = this.generator;
            case 1 -> var10000 = this.fuel;
            default -> throw new IllegalArgumentException("Recipe does not contain slot " + p_346205_);
        }
        return var10000;
    }

    public int size() {
        return 2;
    }

    public boolean isEmpty() {
        return this.generator.isEmpty() && this.fuel.isEmpty();
    }

    public ItemStack generator() {
        return this.generator;
    }

    public ItemStack fuel() {
        return this.fuel;
    }
}
