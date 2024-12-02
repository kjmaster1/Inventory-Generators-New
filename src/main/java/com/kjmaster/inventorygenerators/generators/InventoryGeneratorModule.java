package com.kjmaster.inventorygenerators.generators;

import com.kjmaster.inventorygenerators.curios.CuriosIntegration;
import com.kjmaster.inventorygenerators.setup.Registration;
import mcjty.lib.modules.IModule;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredItem;

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

    public InventoryGeneratorModule(IEventBus bus, Dist dist) {
        bus.addListener(this::registerScreens);
    }

    private static MenuType<InventoryGeneratorContainer> createContainer() {
        return IMenuTypeExtension.create((windowId, inv, data) -> {
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
            InventoryGeneratorItem.initOverrides(INVENTORY_CULINARY_GENERATOR.get());
            InventoryGeneratorItem.initOverrides(INVENTORY_DEATH_GENERATOR.get());
            InventoryGeneratorItem.initOverrides(INVENTORY_END_GENERATOR.get());
            InventoryGeneratorItem.initOverrides(INVENTORY_EXPLOSIVE_GENERATOR.get());
            InventoryGeneratorItem.initOverrides(INVENTORY_FROSTY_GENERATOR.get());
            InventoryGeneratorItem.initOverrides(INVENTORY_FURNACE_GENERATOR.get());
            InventoryGeneratorItem.initOverrides(INVENTORY_NETHER_STAR_GENERATOR.get());
            InventoryGeneratorItem.initOverrides(INVENTORY_OVERCLOCKED_GENERATOR.get());
            InventoryGeneratorItem.initOverrides(INVENTORY_PINK_GENERATOR.get());
            InventoryGeneratorItem.initOverrides(INVENTORY_POTION_GENERATOR.get());
            InventoryGeneratorItem.initOverrides(INVENTORY_SLIMEY_GENERATOR.get());
            InventoryGeneratorItem.initOverrides(INVENTORY_SURVIVALIST_GENERATOR.get());
            // InventoryGeneratorItem.initOverrides(INVENTORY_MAGMATIC_GENERATOR.get());

            InventoryHalitosisGeneratorItem.initOverrides(INVENTORY_HALITOSIS_GENERATOR.get());

            if (CuriosIntegration.hasMod()) {
                CuriosIntegration.curiosSetup();
            }
        });
    }

    @Override
    public void initConfig(IEventBus iEventBus) {

    }

    public void registerScreens(RegisterMenuScreensEvent event) {
        InventoryGeneratorGui.register(event);
    }
}
