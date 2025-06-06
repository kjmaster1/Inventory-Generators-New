package com.kjmaster.inventorygenerators.setup;

import com.kjmaster.inventorygenerators.InventoryGenerators;
import com.kjmaster.inventorygenerators.generators.InventoryGeneratorModule;
import com.kjmaster.inventorygenerators.recipe.GeneratorRecipeSerializer;
import mcjty.lib.setup.DeferredItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.kjmaster.inventorygenerators.InventoryGenerators.MODID;

public class Registration {

    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(BuiltInRegistries.MENU, MODID);
    public static final DeferredItems ITEMS = DeferredItems.create(MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, InventoryGenerators.MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, InventoryGenerators.MODID);

    public static Supplier<CreativeModeTab> TAB = TABS.register("inventorygenerators", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + MODID))
            .icon(() -> new ItemStack(InventoryGeneratorModule.INVENTORY_FURNACE_GENERATOR.get()))
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .displayItems((featureFlags, output) -> {
                InventoryGenerators.setup.populateTab(output);
            })
            .build());

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
        CONTAINERS.register(bus);
        RECIPE_SERIALIZERS.register(bus);
        RECIPE_TYPES.register(bus);
        TABS.register(bus);
        InvGensDataComponents.COMPONENTS.register(bus);
    }

    public static Supplier<GeneratorRecipeSerializer> GENERATOR_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("generator_recipe", GeneratorRecipeSerializer::new);

    public static Supplier<RecipeType<?>> GENERATOR_RECIPE_TYPE = registerRecipeType("generator_recipe");

    static <T extends Recipe<RecipeInput>> Supplier<RecipeType<?>> registerRecipeType(final String name) {
        return Registration.RECIPE_TYPES.register(name, () -> new RecipeType<T>() {
            public String toString() {
                return InventoryGenerators.MODID + ":" + name;
            }
        });
    }
}
