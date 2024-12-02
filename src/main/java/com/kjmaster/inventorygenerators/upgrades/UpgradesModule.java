package com.kjmaster.inventorygenerators.upgrades;

import com.kjmaster.inventorygenerators.setup.Registration;
import mcjty.lib.modules.IModule;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredItem;

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
}
