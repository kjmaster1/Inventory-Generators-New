package com.kjmaster.inventorygenerators.upgrades;

import com.kjmaster.inventorygenerators.setup.Registration;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.lib.setup.DeferredItem;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static com.kjmaster.inventorygenerators.InventoryGenerators.tab;

public class UpgradesModule implements IModule {

    public static final DeferredItem<AutoPullUpgradeItem> AUTO_PULL_UPGRADE = Registration.ITEMS.register("auto_pull_upgrade", tab(AutoPullUpgradeItem::new));
    public static final DeferredItem<NoEffectUpgradeItem> NO_EFFECT_UPGRADE = Registration.ITEMS.register("no_effect_upgrade", tab(NoEffectUpgradeItem::new));
    public static final DeferredItem<SpeedUpgradeItem> SPEED_UPGRADE = Registration.ITEMS.register("speed_upgrade", tab(SpeedUpgradeItem::new));

    @Override
    public void init(FMLCommonSetupEvent fmlCommonSetupEvent) {

    }

    @Override
    public void initClient(FMLClientSetupEvent fmlClientSetupEvent) {

    }

    @Override
    public void initConfig(IEventBus iEventBus) {

    }

    @Override
    public void initDatagen(DataGen dataGen) {
        dataGen.add(
                Dob.itemBuilder(AUTO_PULL_UPGRADE).shaped(shapedRecipeBuilder -> shapedRecipeBuilder
                                .define('h', Items.HOPPER)
                                .define('g', Tags.Items.STORAGE_BLOCKS_GOLD)
                                .unlockedBy("has_hopper", InventoryChangeTrigger.TriggerInstance.hasItems(Items.HOPPER)),
                        "ghg", "RRR", "RRR"
                ),
                Dob.itemBuilder(NO_EFFECT_UPGRADE).shaped(shapedRecipeBuilder -> shapedRecipeBuilder
                                .define('z', Items.GLASS_BOTTLE)
                                .define('g', Tags.Items.STORAGE_BLOCKS_GOLD)
                                .unlockedBy("has_glass_bottle", InventoryChangeTrigger.TriggerInstance.hasItems(Items.GLASS_BOTTLE)),
                        "gzg", "RRR", "RRR"
                ),
                Dob.itemBuilder(SPEED_UPGRADE).shaped(shapedRecipeBuilder -> shapedRecipeBuilder
                                .define('s', Items.SUGAR)
                                .define('g', Tags.Items.STORAGE_BLOCKS_GOLD)
                                .unlockedBy("has_sugar", InventoryChangeTrigger.TriggerInstance.hasItems(Items.SUGAR)),
                        "gsg", "RRR", "RRR"
                )
        );
    }
}
