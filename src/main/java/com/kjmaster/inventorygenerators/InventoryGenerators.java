package com.kjmaster.inventorygenerators;

import com.kjmaster.inventorygenerators.capabilities.EnergyStorageItemstack;
import com.kjmaster.inventorygenerators.curios.CuriosIntegration;
import com.kjmaster.inventorygenerators.generators.InventoryGeneratorItem;
import com.kjmaster.inventorygenerators.generators.InventoryGeneratorModule;
import com.kjmaster.inventorygenerators.setup.*;
import com.kjmaster.inventorygenerators.upgrades.UpgradesModule;
import com.mojang.logging.LogUtils;
import mcjty.lib.modules.Modules;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

import java.util.function.Supplier;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
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

    public InventoryGenerators(ModContainer mod, IEventBus bus, Dist dist) {

        instance = this;
        setupModules(bus, dist);

        Registration.register(bus);

        CuriosIntegration.load();

        bus.addListener(setup::init);
        bus.addListener(modules::init);

        bus.addListener(this::registerCapabilities);
        bus.addListener(InventoryGeneratorsBaseMessages::registerMessages);

        if (dist.isClient()) {
            bus.addListener(ClientSetup::init);
            bus.addListener(ClientSetup::registerKeybinds);
            bus.addListener(modules::initClient);
        }

        mod.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(ForgeCapabilities.ENERGY,
                (itemStack, context) -> new EnergyStorageItemstack(((InventoryGeneratorItem) itemStack.getItem()).getMaxEnergyStored(itemStack), itemStack),
                InventoryGeneratorModule.INVENTORY_CULINARY_GENERATOR.get(),
                InventoryGeneratorModule.INVENTORY_DEATH_GENERATOR.get(),
                InventoryGeneratorModule.INVENTORY_END_GENERATOR.get(),
                InventoryGeneratorModule.INVENTORY_EXPLOSIVE_GENERATOR.get(),
                InventoryGeneratorModule.INVENTORY_FROSTY_GENERATOR.get(),
                InventoryGeneratorModule.INVENTORY_FURNACE_GENERATOR.get(),
                InventoryGeneratorModule.INVENTORY_HALITOSIS_GENERATOR.get(),
                InventoryGeneratorModule.INVENTORY_NETHER_STAR_GENERATOR.get(),
                InventoryGeneratorModule.INVENTORY_OVERCLOCKED_GENERATOR.get(),
                InventoryGeneratorModule.INVENTORY_PINK_GENERATOR.get(),
                InventoryGeneratorModule.INVENTORY_POTION_GENERATOR.get(),
                InventoryGeneratorModule.INVENTORY_SLIMEY_GENERATOR.get(),
                InventoryGeneratorModule.INVENTORY_SURVIVALIST_GENERATOR.get()
        );
        event.register(Capabilities.ItemHandler.ITEM,
                (itemstack, context) -> new ComponentItemHandler(itemstack, InvGensDataComponents.GENERATOR_CONTENTS.get(), 5),
                InventoryGeneratorModule.INVENTORY_CULINARY_GENERATOR.get(),
                InventoryGeneratorModule.INVENTORY_DEATH_GENERATOR.get(),
                InventoryGeneratorModule.INVENTORY_END_GENERATOR.get(),
                InventoryGeneratorModule.INVENTORY_EXPLOSIVE_GENERATOR.get(),
                InventoryGeneratorModule.INVENTORY_FROSTY_GENERATOR.get(),
                InventoryGeneratorModule.INVENTORY_FURNACE_GENERATOR.get(),
                InventoryGeneratorModule.INVENTORY_HALITOSIS_GENERATOR.get(),
                InventoryGeneratorModule.INVENTORY_NETHER_STAR_GENERATOR.get(),
                InventoryGeneratorModule.INVENTORY_OVERCLOCKED_GENERATOR.get(),
                InventoryGeneratorModule.INVENTORY_PINK_GENERATOR.get(),
                InventoryGeneratorModule.INVENTORY_POTION_GENERATOR.get(),
                InventoryGeneratorModule.INVENTORY_SLIMEY_GENERATOR.get(),
                InventoryGeneratorModule.INVENTORY_SURVIVALIST_GENERATOR.get()
        );
    }

    public static <T extends Item> Supplier<T> tab(Supplier<T> supplier) {
        return setup.tab(supplier);
    }

    private void setupModules(IEventBus bus, Dist dist) {
        modules.register(new InventoryGeneratorModule(bus, dist));
        modules.register(new UpgradesModule());
    }
}
