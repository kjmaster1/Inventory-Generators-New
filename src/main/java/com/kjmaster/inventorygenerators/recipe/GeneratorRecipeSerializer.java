package com.kjmaster.inventorygenerators.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kjmaster.inventorygenerators.InventoryGenerators;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GeneratorRecipeSerializer implements RecipeSerializer<GeneratorRecipe> {

    @Override
    public @NotNull GeneratorRecipe fromJson(@NotNull ResourceLocation resourceLocation, @NotNull JsonObject pJson) {

        System.out.println("From JSON: " + resourceLocation);

        JsonObject generatorObject = GsonHelper.getAsJsonObject(pJson, "generator");
        Item item = GsonHelper.getAsItem(generatorObject, "item");
        Ingredient generator = Ingredient.of(item);
        JsonArray fuelsArray = GsonHelper.getAsJsonArray(pJson, "fuels");

        ArrayList<Ingredient> fuels = new ArrayList<>();

        fuelsArray.forEach(jsonElement -> {
                    fuels.add(Ingredient.fromJson(jsonElement, false));
                }
        );

        int burnTime = GsonHelper.getAsInt(pJson, "burnTime", 0);
        int RF = GsonHelper.getAsInt(pJson, "RF", 0);

        return new GeneratorRecipe(resourceLocation, fuels, generator, burnTime, RF);
    }

    @Override
    public @Nullable GeneratorRecipe fromNetwork(@NotNull ResourceLocation resourceLocation, @NotNull FriendlyByteBuf buffer) {
        try {
            List<Ingredient> fuels = buffer.readList(Ingredient::fromNetwork);
            Ingredient generator = Ingredient.fromNetwork(buffer);
            int burnTime = buffer.readInt();
            int RF = buffer.readInt();
            return new GeneratorRecipe(resourceLocation, fuels, generator, burnTime, RF);
        } catch (Exception e) {
            InventoryGenerators.LOGGER.error("Error reading generator recipe from packet. ", e);
            throw e;
        }
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull GeneratorRecipe recipe) {
        try {
            buffer.writeCollection(recipe.fuels(), (buf, fuel) -> fuel.toNetwork(buf));
            recipe.generator().toNetwork(buffer);
            buffer.writeInt(recipe.burnTime());
            buffer.writeInt(recipe.RF());
        } catch (Exception e) {
            InventoryGenerators.LOGGER.error("Error writing generator recipe to packet.", e);
            throw e;
        }
    }
}
