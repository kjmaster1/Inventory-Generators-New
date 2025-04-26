package com.kjmaster.inventorygenerators.generators;

import com.kjmaster.inventorygenerators.compat.curios.CuriosIntegration;
import com.kjmaster.inventorygenerators.setup.ClientSetup;
import com.kjmaster.inventorygenerators.setup.Registration;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.lib.setup.DeferredItem;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.function.Supplier;

import static com.kjmaster.inventorygenerators.InventoryGenerators.tab;

public class InventoryGeneratorModule implements IModule {

    public static final DeferredItem<InventoryCulinaryGeneratorItem> INVENTORY_CULINARY_GENERATOR = Registration.ITEMS.register("inventory_culinary_generator", tab(InventoryCulinaryGeneratorItem::new));
    public static final DeferredItem<InventoryDeathGeneratorItem> INVENTORY_DEATH_GENERATOR = Registration.ITEMS.register("inventory_death_generator", tab(InventoryDeathGeneratorItem::new));
    public static final DeferredItem<InventoryEndGeneratorItem> INVENTORY_END_GENERATOR = Registration.ITEMS.register("inventory_end_generator", tab(InventoryEndGeneratorItem::new));
    public static final DeferredItem<InventoryExplosiveGeneratorItem> INVENTORY_EXPLOSIVE_GENERATOR = Registration.ITEMS.register("inventory_explosive_generator", tab(InventoryExplosiveGeneratorItem::new));
    public static final DeferredItem<InventoryFrostyGeneratorItem> INVENTORY_FROSTY_GENERATOR = Registration.ITEMS.register("inventory_frosty_generator", tab(InventoryFrostyGeneratorItem::new));
    public static final DeferredItem<InventoryFurnaceGeneratorItem> INVENTORY_FURNACE_GENERATOR = Registration.ITEMS.register("inventory_furnace_generator", tab(InventoryFurnaceGeneratorItem::new));
    public static final DeferredItem<InventoryHalitosisGeneratorItem> INVENTORY_HALITOSIS_GENERATOR = Registration.ITEMS.register("inventory_halitosis_generator", tab(InventoryHalitosisGeneratorItem::new));
    public static final DeferredItem<InventoryNetherStarGeneratorItem> INVENTORY_NETHER_STAR_GENERATOR = Registration.ITEMS.register("inventory_nether_star_generator", tab(InventoryNetherStarGeneratorItem::new));
    public static final DeferredItem<InventoryOverclockedGeneratorItem> INVENTORY_OVERCLOCKED_GENERATOR = Registration.ITEMS.register("inventory_overclocked_generator", tab(InventoryOverclockedGeneratorItem::new));
    public static final DeferredItem<InventoryPinkGeneratorItem> INVENTORY_PINK_GENERATOR = Registration.ITEMS.register("inventory_pink_generator", tab(InventoryPinkGeneratorItem::new));
    public static final DeferredItem<InventoryPotionGeneratorItem> INVENTORY_POTION_GENERATOR = Registration.ITEMS.register("inventory_potion_generator", tab(InventoryPotionGeneratorItem::new));
    public static final DeferredItem<InventorySlimeyGeneratorItem> INVENTORY_SLIMEY_GENERATOR = Registration.ITEMS.register("inventory_slimey_generator", tab(InventorySlimeyGeneratorItem::new));
    public static final DeferredItem<InventorySurvivalistGeneratorItem> INVENTORY_SURVIVALIST_GENERATOR = Registration.ITEMS.register("inventory_survivalist_generator", tab(InventorySurvivalistGeneratorItem::new));
    // public static final RegistryObject<InventoryMagmaticGeneratorItem> INVENTORY_MAGMATIC_GENERATOR = Registration.ITEMS.register("inventory_magmatic_generator", tab(InventoryMagmaticGeneratorItem::new));

    public static final Supplier<MenuType<InventoryGeneratorContainer>> CONTAINER_INVENTORY_GENERATOR = Registration.CONTAINERS.register("inventory_generator", InventoryGeneratorModule::createContainer);

    private static MenuType<InventoryGeneratorContainer> createContainer() {
        return IForgeMenuType.create((windowId, inv, data) -> {
            Player player = inv.player;
            ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
            InventoryGeneratorContainer container = new InventoryGeneratorContainer(windowId, player.blockPosition(), player, stack);
            container.setupInventories(new InventoryGeneratorItemHandler(stack), inv);
            return container;
        });
    }

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            InventoryGeneratorGui.register();
            ClientSetup.initOverrides(INVENTORY_CULINARY_GENERATOR.get());
            ClientSetup.initOverrides(INVENTORY_DEATH_GENERATOR.get());
            ClientSetup.initOverrides(INVENTORY_END_GENERATOR.get());
            ClientSetup.initOverrides(INVENTORY_EXPLOSIVE_GENERATOR.get());
            ClientSetup.initOverrides(INVENTORY_FROSTY_GENERATOR.get());
            ClientSetup.initOverrides(INVENTORY_FURNACE_GENERATOR.get());
            ClientSetup.initOverrides(INVENTORY_NETHER_STAR_GENERATOR.get());
            ClientSetup.initOverrides(INVENTORY_OVERCLOCKED_GENERATOR.get());
            ClientSetup.initOverrides(INVENTORY_PINK_GENERATOR.get());
            ClientSetup.initOverrides(INVENTORY_POTION_GENERATOR.get());
            ClientSetup.initOverrides(INVENTORY_SLIMEY_GENERATOR.get());
            ClientSetup.initOverrides(INVENTORY_SURVIVALIST_GENERATOR.get());
            // ClientSetup.initOverrides(INVENTORY_MAGMATIC_GENERATOR.get());
            ClientSetup.initHalitosisOverrides(INVENTORY_HALITOSIS_GENERATOR.get());

            if (CuriosIntegration.hasMod()) {
                CuriosIntegration.curiosSetup();
            }
        });
    }

    @Override
    public void initConfig(IEventBus iEventBus) {

    }

    @Override
    public void initDatagen(DataGen dataGen) {
        dataGen.add(
                Dob.itemBuilder(INVENTORY_CULINARY_GENERATOR).shaped(shapedRecipeBuilder -> shapedRecipeBuilder
                                .define('w', Tags.Items.CROPS_WHEAT)
                                .define('z', Items.COOKED_PORKCHOP)
                                .define('f', INVENTORY_FURNACE_GENERATOR.get())
                                .unlockedBy("has_furnace_generator", InventoryChangeTrigger.TriggerInstance.hasItems(INVENTORY_FURNACE_GENERATOR.get())),
                        "www", "wzw", "rfr"
                ),
                Dob.itemBuilder(INVENTORY_DEATH_GENERATOR).shaped(shapedRecipeBuilder -> shapedRecipeBuilder
                                .define('z', Tags.Items.BONES)
                                .define('s', Items.SPIDER_EYE)
                                .define('f', INVENTORY_FURNACE_GENERATOR.get())
                                .unlockedBy("has_furnace_generator", InventoryChangeTrigger.TriggerInstance.hasItems(INVENTORY_FURNACE_GENERATOR.get())),
                        "zzz", "zsz", "rfr"
                ),
                Dob.itemBuilder(INVENTORY_END_GENERATOR).shaped(shapedRecipeBuilder -> shapedRecipeBuilder
                                .define('f', INVENTORY_FURNACE_GENERATOR.get())
                                .unlockedBy("has_furnace_generator", InventoryChangeTrigger.TriggerInstance.hasItems(INVENTORY_FURNACE_GENERATOR.get())),
                        "ooo", "oOo", "rfr"
                ),
                Dob.itemBuilder(INVENTORY_EXPLOSIVE_GENERATOR).shaped(shapedRecipeBuilder -> shapedRecipeBuilder
                                .define('g', Tags.Items.GUNPOWDER)
                                .define('t', Items.TNT)
                                .define('f', INVENTORY_FURNACE_GENERATOR.get())
                                .unlockedBy("has_furnace_generator", InventoryChangeTrigger.TriggerInstance.hasItems(INVENTORY_FURNACE_GENERATOR.get())),
                        "ggg", "gtg", "rfr"
                ),
                Dob.itemBuilder(INVENTORY_FROSTY_GENERATOR).shaped(shapedRecipeBuilder -> shapedRecipeBuilder
                                .define('z', Items.ICE)
                                .define('s', Items.SNOWBALL)
                                .define('f', INVENTORY_FURNACE_GENERATOR.get())
                                .unlockedBy("has_furnace_generator", InventoryChangeTrigger.TriggerInstance.hasItems(INVENTORY_FURNACE_GENERATOR.get())),
                        "sss", "szs", "rfr"
                ),
                Dob.itemBuilder(INVENTORY_FURNACE_GENERATOR).shaped(shapedRecipeBuilder -> shapedRecipeBuilder
                                .define('s', INVENTORY_SURVIVALIST_GENERATOR.get())
                                .define('f', Items.FURNACE)
                                .unlockedBy("has_survivalist_generator", InventoryChangeTrigger.TriggerInstance.hasItems(INVENTORY_SURVIVALIST_GENERATOR.get())),
                        "iii", "isi", "rfr"
                ),
                Dob.itemBuilder(INVENTORY_HALITOSIS_GENERATOR).shaped(shapedRecipeBuilder -> shapedRecipeBuilder
                                .define('z', Items.PURPUR_BLOCK)
                                .define('y', Items.END_ROD)
                                .define('f', INVENTORY_FURNACE_GENERATOR.get())
                                .unlockedBy("has_furnace_generator", InventoryChangeTrigger.TriggerInstance.hasItems(INVENTORY_FURNACE_GENERATOR.get())),
                        "zzz", "zyz", "rfr"
                ),
                Dob.itemBuilder(INVENTORY_NETHER_STAR_GENERATOR).shaped(shapedRecipeBuilder -> shapedRecipeBuilder
                                .define('w', Items.WITHER_SKELETON_SKULL)
                                .define('n', Tags.Items.NETHER_STARS)
                                .define('f', INVENTORY_FURNACE_GENERATOR.get())
                                .unlockedBy("has_furnace_generator", InventoryChangeTrigger.TriggerInstance.hasItems(INVENTORY_FURNACE_GENERATOR.get())),
                        "www", "wnw", "rfr"
                ),
                Dob.itemBuilder(INVENTORY_OVERCLOCKED_GENERATOR).shaped(shapedRecipeBuilder -> shapedRecipeBuilder
                                .define('l', Tags.Items.GEMS_LAPIS)
                                .define('g', Tags.Items.STORAGE_BLOCKS_GOLD)
                                .define('f', INVENTORY_FURNACE_GENERATOR.get())
                                .unlockedBy("has_furnace_generator", InventoryChangeTrigger.TriggerInstance.hasItems(INVENTORY_FURNACE_GENERATOR.get())),
                        "lll", "lgl", "rfr"
                ),
                Dob.itemBuilder(INVENTORY_PINK_GENERATOR).shaped(shapedRecipeBuilder -> shapedRecipeBuilder
                                .define('z', Items.PINK_WOOL)
                                .define('y', Tags.Items.DYES_PINK)
                                .define('f', INVENTORY_FURNACE_GENERATOR.get())
                                .unlockedBy("has_furnace_generator", InventoryChangeTrigger.TriggerInstance.hasItems(INVENTORY_FURNACE_GENERATOR.get())),
                        "zzz", "zyz", "rfr"
                ),
                Dob.itemBuilder(INVENTORY_POTION_GENERATOR).shaped(shapedRecipeBuilder -> shapedRecipeBuilder
                                .define('z', Tags.Items.RODS_BLAZE)
                                .define('s', Items.BREWING_STAND)
                                .define('f', INVENTORY_FURNACE_GENERATOR.get())
                                .unlockedBy("has_furnace_generator", InventoryChangeTrigger.TriggerInstance.hasItems(INVENTORY_FURNACE_GENERATOR.get())),
                        "zzz", "zsz", "rfr"
                ),
                Dob.itemBuilder(INVENTORY_SLIMEY_GENERATOR).shaped(shapedRecipeBuilder -> shapedRecipeBuilder
                                .define('z', Items.SLIME_BLOCK)
                                .define('s', Tags.Items.SLIMEBALLS)
                                .define('f', INVENTORY_FURNACE_GENERATOR.get())
                                .unlockedBy("has_furnace_generator", InventoryChangeTrigger.TriggerInstance.hasItems(INVENTORY_FURNACE_GENERATOR.get())),
                        "sss", "szs", "rfr"
                ),
                Dob.itemBuilder(INVENTORY_SURVIVALIST_GENERATOR).shaped(shapedRecipeBuilder -> shapedRecipeBuilder
                                .define('s', Tags.Items.COBBLESTONE_NORMAL)
                                .define('f', Items.FURNACE)
                                .unlockedBy("has_furnace", InventoryChangeTrigger.TriggerInstance.hasItems(Items.FURNACE)),
                        "sss", "sis", "RfR"
                )
        );
    }
}
