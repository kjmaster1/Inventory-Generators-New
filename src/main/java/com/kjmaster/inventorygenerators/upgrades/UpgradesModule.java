package com.kjmaster.inventorygenerators.upgrades;

import com.kjmaster.inventorygenerators.setup.Registration;
import com.kjmaster.kjlib.modules.IModule;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryObject;

import static com.kjmaster.inventorygenerators.InventoryGenerators.tab;

public class UpgradesModule implements IModule {

    public static final RegistryObject<AutoPullUpgradeItem> AUTO_PULL_UPGRADE = Registration.ITEMS.register("auto_pull_upgrade", tab(AutoPullUpgradeItem::new));
    public static final RegistryObject<NoEffectUpgradeItem> NO_EFFECT_UPGRADE = Registration.ITEMS.register("no_effect_upgrade", tab(NoEffectUpgradeItem::new));
    public static final RegistryObject<SpeedUpgradeItem> SPEED_UPGRADE = Registration.ITEMS.register("speed_upgrade", tab(SpeedUpgradeItem::new));

    @Override
    public void init(FMLCommonSetupEvent fmlCommonSetupEvent) {

    }

    @Override
    public void initClient(FMLClientSetupEvent fmlClientSetupEvent) {

    }
}
