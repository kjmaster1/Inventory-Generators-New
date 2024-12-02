package com.kjmaster.inventorygenerators.recipe;

import com.kjmaster.inventorygenerators.setup.Registration;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;

public record GeneratorRecipe(List<Ingredient> fuels, Ingredient generator, int burnTime,
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
    public ItemStack assemble(GeneratorRecipeInput generatorRecipeInput, HolderLookup.Provider provider) {
        return null;
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return null;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Registration.GENERATOR_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return Registration.GENERATOR_RECIPE_TYPE.get();
    }
}
