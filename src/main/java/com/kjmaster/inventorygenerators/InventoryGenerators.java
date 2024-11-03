package com.kjmaster.inventorygenerators;

import com.kjmaster.inventorygenerators.curios.CuriosIntegration;
import com.kjmaster.inventorygenerators.generators.InventoryGeneratorModule;
import com.kjmaster.inventorygenerators.setup.ClientSetup;
import com.kjmaster.inventorygenerators.setup.Config;
import com.kjmaster.inventorygenerators.setup.ModSetup;
import com.kjmaster.inventorygenerators.setup.Registration;
import com.kjmaster.inventorygenerators.upgrades.UpgradesModule;
import com.kjmaster.kjlib.modules.Modules;
import com.mojang.logging.LogUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.function.Supplier;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(InventoryGenerators.MODID)
public class InventoryGenerators {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "inventorygenerators";
    @SuppressWarnings("PublicField")
    public static final ModSetup setup = new ModSetup();
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    @SuppressWarnings("PublicField")
    public static InventoryGenerators instance;

    private final Modules modules = new Modules();

    public InventoryGenerators() {

        instance = this;
        setupModules();

        Registration.register();

        CuriosIntegration.load();

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(setup::init);
        bus.addListener(modules::init);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            bus.addListener(ClientSetup::init);
            bus.addListener(ClientSetup::registerKeybinds);
            bus.addListener(modules::initClient);
        });

        MinecraftForge.EVENT_BUS.addListener(setup::reload);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    public static <T extends Item> Supplier<T> tab(Supplier<T> supplier) {
        return setup.tab(supplier);
    }

    private void setupModules() {
        modules.register(new InventoryGeneratorModule());
        modules.register(new UpgradesModule());
    }
}
