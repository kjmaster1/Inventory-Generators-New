package com.kjmaster.inventorygenerators.compat.jei;

import com.kjmaster.inventorygenerators.recipe.GeneratorRecipe;
import com.kjmaster.inventorygenerators.setup.Registration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.List;

public class RecipesList {
    private final RecipeManager recipe_manager;

    public RecipesList() {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel world = minecraft.level;
        this.recipe_manager = world.getRecipeManager();
    }

    public List<GeneratorRecipe> getGeneratorRecipes() {
        RecipeType<GeneratorRecipe> generatorRecipeRecipeType = (RecipeType<GeneratorRecipe>) Registration.GENERATOR_RECIPE_TYPE.get();
        List<RecipeHolder<GeneratorRecipe>> recipeHolders = recipe_manager.getAllRecipesFor(generatorRecipeRecipeType);
        List<GeneratorRecipe> generatorRecipes = new ArrayList<GeneratorRecipe>();
        for (RecipeHolder<GeneratorRecipe> holder : recipeHolders) {
            generatorRecipes.add(holder.value());
        }
        return generatorRecipes;
    }
}
