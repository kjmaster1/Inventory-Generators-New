package com.kjmaster.inventorygenerators.recipe;

import com.kjmaster.inventorygenerators.InventoryGenerators;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class GeneratorRecipeSerializer implements RecipeSerializer<GeneratorRecipe> {

    private static final MapCodec<GeneratorRecipe> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                            Ingredient.CODEC.listOf().fieldOf("fuels").orElse(List.of()).forGetter(GeneratorRecipe::fuels),
                            Ingredient.CODEC.fieldOf("generator").forGetter(GeneratorRecipe::generator),
                            Codec.INT.fieldOf("burnTime").forGetter(GeneratorRecipe::burnTime),
                            Codec.INT.fieldOf("RF").forGetter(GeneratorRecipe::RF)
                    )
                    .apply(builder, GeneratorRecipe::new)
    );

    public final StreamCodec<RegistryFriendlyByteBuf, GeneratorRecipe> STREAM_CODEC = StreamCodec.of(
            this::toNetwork, this::fromNetwork
    );

    @Override
    public MapCodec<GeneratorRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, GeneratorRecipe> streamCodec() {
        return STREAM_CODEC;
    }

    public GeneratorRecipe fromNetwork(@Nonnull RegistryFriendlyByteBuf buffer) {
        try {
            List<Ingredient> fuels = new ArrayList<>();
            IntStream.range(0, buffer.readInt()).forEach(
                    i -> fuels.add(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer))
            );
            System.out.println("Fuels List: " + fuels);
            return new GeneratorRecipe(fuels, Ingredient.CONTENTS_STREAM_CODEC.decode(buffer), buffer.readInt(), buffer.readInt());
        } catch (Exception e) {
            InventoryGenerators.LOGGER.error("Error reading solid fuels recipe from packet. ", e);
            throw e;
        }
    }

    public void toNetwork(@Nonnull RegistryFriendlyByteBuf buffer, GeneratorRecipe recipe) {
        System.out.println("Recipe: " + recipe);
        try {
            buffer.writeInt(recipe.fuels().size());
            recipe.fuels().forEach(fuel -> {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, fuel);
            });
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.generator());
            buffer.writeInt(recipe.burnTime());
            buffer.writeInt(recipe.RF());
        } catch (Exception e) {
            InventoryGenerators.LOGGER.error("Error writing solid fuels recipe to packet.", e);
            throw e;
        }
    }
}
