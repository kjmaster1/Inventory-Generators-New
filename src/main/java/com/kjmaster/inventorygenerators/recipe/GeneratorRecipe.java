package com.kjmaster.inventorygenerators.recipe;

import com.kjmaster.inventorygenerators.setup.Registration;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public record GeneratorRecipe(ResourceLocation id, List<Ingredient> fuels, Ingredient generator, int burnTime,
                              int RF) implements Recipe<GeneratorRecipeInput> {

    @Override
    public boolean matches(GeneratorRecipeInput generatorRecipeInput, Level level) {
        if (generator.test(generatorRecipeInput.generator())) {
            for (Ingredient ingredient : fuels) {
                if (ingredient.test(generatorRecipeInput.fuel())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull GeneratorRecipeInput generatorRecipeInput, @NotNull RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return Registration.GENERATOR_RECIPE_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return Registration.GENERATOR_RECIPE_TYPE.get();
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return this.id;
    }
}
