package com.kjmaster.inventorygenerators;

import com.kjmaster.inventorygenerators.compat.curios.CuriosIntegration;
import com.kjmaster.inventorygenerators.generators.InventoryGeneratorModule;
import com.kjmaster.inventorygenerators.setup.ClientSetup;
import com.kjmaster.inventorygenerators.setup.Config;
import com.kjmaster.inventorygenerators.setup.ModSetup;
import com.kjmaster.inventorygenerators.setup.Registration;
import com.kjmaster.inventorygenerators.upgrades.UpgradesModule;
import com.mojang.logging.LogUtils;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.modules.Modules;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

import java.util.function.Supplier;

@Mod(InventoryGenerators.MODID)
public class InventoryGenerators {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "inventorygenerators";
    @SuppressWarnings("PublicField")
    public static final ModSetup setup = new ModSetup();
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    @SuppressWarnings("PublicField")
    public static InventoryGenerators instance;

    private final Modules modules = new Modules();

    public InventoryGenerators(FMLJavaModLoadingContext context) {

        IEventBus bus = context.getModEventBus();
        Dist dist = FMLEnvironment.dist;

        instance = this;
        setupModules(bus, dist);

        Registration.register(bus);

        CuriosIntegration.load();

        bus.addListener(setup::init);
        bus.addListener(modules::init);
        bus.addListener(this::onDataGen);

        if (dist.isClient()) {
            bus.addListener(ClientSetup::init);
            bus.addListener(ClientSetup::registerKeybinds);
            bus.addListener(modules::initClient);
        }

        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    public static <T extends Item> Supplier<T> tab(Supplier<T> supplier) {
        return instance.setup.tab(supplier);
    }

    private void setupModules(IEventBus bus, Dist dist) {
        modules.register(new InventoryGeneratorModule());
        modules.register(new UpgradesModule());
    }

    private void onDataGen(GatherDataEvent event) {
        DataGen dataGen = new DataGen(MODID, event);
        modules.datagen(dataGen);
        dataGen.generate();
    }
}
