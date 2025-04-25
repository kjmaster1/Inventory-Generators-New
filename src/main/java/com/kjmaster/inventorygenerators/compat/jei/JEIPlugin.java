package com.kjmaster.inventorygenerators.compat.jei;

import com.kjmaster.inventorygenerators.InventoryGenerators;
import com.kjmaster.inventorygenerators.generators.InventoryGeneratorModule;
import com.kjmaster.inventorygenerators.recipe.GeneratorRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.library.plugins.vanilla.cooking.fuel.FuelRecipeMaker;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

import static com.kjmaster.inventorygenerators.generators.InventoryCulinaryGeneratorItem.*;
import static com.kjmaster.inventorygenerators.setup.Config.*;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

    private static final ResourceLocation pluginId = ResourceLocation.fromNamespaceAndPath(InventoryGenerators.MODID, InventoryGenerators.MODID);

    public static RecipeType<GeneratorRecipe> GENERATOR_RECIPE_TYPE = RecipeType.create(InventoryGenerators.MODID, "generator_recipe", GeneratorRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return pluginId;
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        List<Item> items = new ArrayList<>();
        Item culinaryGenerator = InventoryGeneratorModule.INVENTORY_CULINARY_GENERATOR.asItem();
        items.add(culinaryGenerator);
        Item deathGenerator = InventoryGeneratorModule.INVENTORY_DEATH_GENERATOR.asItem();
        items.add(deathGenerator);
        Item endGenerator = InventoryGeneratorModule.INVENTORY_END_GENERATOR.asItem();
        items.add(endGenerator);
        Item explosiveGenerator = InventoryGeneratorModule.INVENTORY_EXPLOSIVE_GENERATOR.asItem();
        items.add(explosiveGenerator);
        Item frostyGenerator = InventoryGeneratorModule.INVENTORY_FROSTY_GENERATOR.asItem();
        items.add(frostyGenerator);
        Item furnaceGenerator = InventoryGeneratorModule.INVENTORY_FURNACE_GENERATOR.asItem();
        items.add(furnaceGenerator);
        Item halitosisGenerator = InventoryGeneratorModule.INVENTORY_HALITOSIS_GENERATOR.asItem();
        items.add(halitosisGenerator);
        Item netherStarGenerator = InventoryGeneratorModule.INVENTORY_NETHER_STAR_GENERATOR.asItem();
        items.add(netherStarGenerator);
        Item overclockedGenerator = InventoryGeneratorModule.INVENTORY_OVERCLOCKED_GENERATOR.asItem();
        items.add(overclockedGenerator);
        Item pinkGenerator = InventoryGeneratorModule.INVENTORY_PINK_GENERATOR.asItem();
        items.add(pinkGenerator);
        Item potionGenerator = InventoryGeneratorModule.INVENTORY_POTION_GENERATOR.asItem();
        items.add(potionGenerator);
        Item slimeyGenerator = InventoryGeneratorModule.INVENTORY_SLIMEY_GENERATOR.asItem();
        items.add(slimeyGenerator);
        Item survivalistGenerator = InventoryGeneratorModule.INVENTORY_SURVIVALIST_GENERATOR.asItem();
        items.add(survivalistGenerator);

        for (Item item : items) {
            registration.addRecipeCatalyst(new ItemStack(item), GENERATOR_RECIPE_TYPE);
        }
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        registration.addRecipeCategories(new GeneratorRecipeCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipesList recipesFromJson = new RecipesList();
        registration.addRecipes(GENERATOR_RECIPE_TYPE, recipesFromJson.getGeneratorRecipes());

        var fuelRecipes = new ArrayList<GeneratorRecipe>();
        var vanillaFuelRecipes = FuelRecipeMaker.getFuelRecipes(registration.getIngredientManager());
        Item furnaceGenerator = InventoryGeneratorModule.INVENTORY_FURNACE_GENERATOR.asItem();
        Item overclockedGenerator = InventoryGeneratorModule.INVENTORY_OVERCLOCKED_GENERATOR.asItem();
        Item survivalistGenerator = InventoryGeneratorModule.INVENTORY_SURVIVALIST_GENERATOR.asItem();
        vanillaFuelRecipes.forEach(fuelRecipe -> {
            int burnTime = fuelRecipe.getBurnTime();
            fuelRecipes.add(new GeneratorRecipe(List.of(Ingredient.of(fuelRecipe.getInputs().stream())), Ingredient.of(survivalistGenerator), burnTime, survivalistGeneratorRfPerTick));
            burnTime = (int) Math.ceil(fuelRecipe.getBurnTime() / furnaceGeneratorDivisor);
            fuelRecipes.add(new GeneratorRecipe(List.of(Ingredient.of(fuelRecipe.getInputs().stream())), Ingredient.of(furnaceGenerator), burnTime, furnaceGeneratorRfPerTick));
            burnTime = (int) Math.ceil(fuelRecipe.getBurnTime() / overclockedGeneratorDivisor) + 1;
            fuelRecipes.add(new GeneratorRecipe(List.of(Ingredient.of(fuelRecipe.getInputs().stream())), Ingredient.of(overclockedGenerator), burnTime, overclockedGeneratorMinRfPerTick));
        });
        registration.addRecipes(GENERATOR_RECIPE_TYPE, fuelRecipes);

        var foodRecipes = new ArrayList<GeneratorRecipe>();
        var foodList = registration.getIngredientManager().getAllItemStacks().stream().filter((stack) -> {
            FoodProperties foodProperties = stack.getItem().getFoodProperties(stack, null);
            return foodProperties != null;
        }).toList();

        Item culinaryGenerator = InventoryGeneratorModule.INVENTORY_CULINARY_GENERATOR.asItem();

        foodList.forEach(stack -> {
            FoodProperties properties = stack.getFoodProperties(null);
            if (properties != null) {
                int healAmount = getHealAmount(properties);
                int energyOutput = nerfLevels(healAmount * getSaturationModifier(properties) * 8000, 64000);
                int energyRate = getEnergyRate(properties);
                int burnTime = Math.round((float) energyOutput / (float) energyRate);
                foodRecipes.add(new GeneratorRecipe(List.of(Ingredient.of(stack)), Ingredient.of(culinaryGenerator), burnTime, energyRate));
            }
        });
        registration.addRecipes(GENERATOR_RECIPE_TYPE, foodRecipes);
    }
}
