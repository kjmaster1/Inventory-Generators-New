package com.kjmaster.inventorygenerators.setup;

import com.kjmaster.inventorygenerators.InventoryGenerators;
import com.kjmaster.inventorygenerators.generators.IInventoryGenerator;
import com.kjmaster.inventorygenerators.generators.InventoryGeneratorItem;
import com.kjmaster.inventorygenerators.keys.KeyBindings;
import com.kjmaster.inventorygenerators.keys.KeyInputHandler;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientSetup {

    public static void init(FMLClientSetupEvent e) {
        MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
    }

    public static void registerKeybinds(RegisterKeyMappingsEvent event) {
        KeyBindings.init(event);
    }

    public static void initOverrides(InventoryGeneratorItem item) {
        ItemProperties.register(item, ResourceLocation.fromNamespaceAndPath(InventoryGenerators.MODID, "on"), (stack, worldIn, entityIn, integer) -> {
            if (stack.getItem() instanceof IInventoryGenerator inventoryGenerator) {
                return inventoryGenerator.isOn(stack) && (inventoryGenerator.getBurnTime(stack) > 0) ? 1 : 0;
            }
            return 0;
        });
    }

    public static void initHalitosisOverrides(InventoryGeneratorItem item) {
        ItemProperties.register(item, ResourceLocation.fromNamespaceAndPath(InventoryGenerators.MODID, "on"), (stack, worldIn, entityIn, integer) -> {
            if (stack.getItem() instanceof IInventoryGenerator inventoryGenerator) {
                int burnTime = inventoryGenerator.getBurnTime(stack);
                if (inventoryGenerator.isOn(stack) && burnTime > 0) {
                    int time = (inventoryGenerator).calculateTime(stack, inventoryGenerator.getCurrentFuel(stack), worldIn);
                    if (burnTime < time / 4) {
                        return 1;
                    } else if (burnTime < time / 2) {
                        return 2;
                    } else if (burnTime < time * 0.75) {
                        return 3;
                    } else {
                        return 4;
                    }
                }
            }
            return 0;
        });
    }
}
