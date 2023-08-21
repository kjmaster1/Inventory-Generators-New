package com.kjmaster.inventorygenerators.curios;

import com.kjmaster.inventorygenerators.generators.InventoryGeneratorModule;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class CuriosIntegration {
    @SuppressWarnings("EmptyMethod") public static void load() {}

    private static final String MODID = "curios";
    private static final String SLOT_ID = "inventory_generators";

    public static boolean hasMod() {
        return ModList.get().isLoaded(MODID);
    }

    public static void curiosSetup() {
        CuriosRendererRegistry.register(InventoryGeneratorModule.INVENTORY_CULINARY_GENERATOR.get(), InventoryGeneratorRenderer::new);
        CuriosRendererRegistry.register(InventoryGeneratorModule.INVENTORY_DEATH_GENERATOR.get(), InventoryGeneratorRenderer::new);
        CuriosRendererRegistry.register(InventoryGeneratorModule.INVENTORY_END_GENERATOR.get(), InventoryGeneratorRenderer::new);
        CuriosRendererRegistry.register(InventoryGeneratorModule.INVENTORY_EXPLOSIVE_GENERATOR.get(), InventoryGeneratorRenderer::new);
        CuriosRendererRegistry.register(InventoryGeneratorModule.INVENTORY_FROSTY_GENERATOR.get(), InventoryGeneratorRenderer::new);
        CuriosRendererRegistry.register(InventoryGeneratorModule.INVENTORY_FURNACE_GENERATOR.get(), InventoryGeneratorRenderer::new);
        CuriosRendererRegistry.register(InventoryGeneratorModule.INVENTORY_HALITOSIS_GENERATOR.get(), InventoryGeneratorRenderer::new);
        CuriosRendererRegistry.register(InventoryGeneratorModule.INVENTORY_NETHER_STAR_GENERATOR.get(), InventoryGeneratorRenderer::new);
        CuriosRendererRegistry.register(InventoryGeneratorModule.INVENTORY_OVERCLOCKED_GENERATOR.get(), InventoryGeneratorRenderer::new);
        CuriosRendererRegistry.register(InventoryGeneratorModule.INVENTORY_PINK_GENERATOR.get(), InventoryGeneratorRenderer::new);
        CuriosRendererRegistry.register(InventoryGeneratorModule.INVENTORY_POTION_GENERATOR.get(), InventoryGeneratorRenderer::new);
        CuriosRendererRegistry.register(InventoryGeneratorModule.INVENTORY_SLIMEY_GENERATOR.get(), InventoryGeneratorRenderer::new);
        CuriosRendererRegistry.register(InventoryGeneratorModule.INVENTORY_SURVIVALIST_GENERATOR.get(), InventoryGeneratorRenderer::new);
    }

    public static ArrayList<IItemHandlerModifiable> getCuriosInventory(Player player) {
        if (hasMod())
            return Integrator.getCuriosInventory(player);
        return new ArrayList<>();
    }

    public static IItemHandlerModifiable getFullInventory(Player player) {
        IItemHandlerModifiable inventory = new PlayerInvWrapper(player.getInventory());
        ArrayList<IItemHandlerModifiable> itemHandlers = getCuriosInventory(player);
        itemHandlers.add(inventory);
        IItemHandlerModifiable[] itemHandlerModifiables = new IItemHandlerModifiable[itemHandlers.size()];
        itemHandlerModifiables = itemHandlers.toArray(itemHandlerModifiables);
        return new CombinedInvWrapper(itemHandlerModifiables);
    }

    private static final class Integrator {
        private Integrator() {}

        private static ArrayList<IItemHandlerModifiable> getCuriosInventory(Player player) {
            ArrayList<IItemHandlerModifiable> stacksHandlers = new ArrayList<>();
            Optional<ICuriosItemHandler> itemHandler = CuriosApi.getCuriosHelper().getCuriosHandler(player).resolve();
            if (itemHandler.isEmpty()) {
                return stacksHandlers;
            }
            Map<String, ICurioStacksHandler> curioStacksHandlerMap = itemHandler.get().getCurios();
            for (Map.Entry<String, ICurioStacksHandler> entry : curioStacksHandlerMap.entrySet()) {
                ICurioStacksHandler stacksHandler = entry.getValue();
                stacksHandlers.add(stacksHandler.getStacks());
            }
            return stacksHandlers;
        }
    }
}