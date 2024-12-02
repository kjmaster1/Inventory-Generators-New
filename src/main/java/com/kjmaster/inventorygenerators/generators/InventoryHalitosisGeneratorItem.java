package com.kjmaster.inventorygenerators.generators;

import com.kjmaster.inventorygenerators.InventoryGenerators;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import static com.kjmaster.inventorygenerators.setup.Config.halitosisGeneratorCapacity;

public class InventoryHalitosisGeneratorItem extends InventoryGeneratorItem {

    public InventoryHalitosisGeneratorItem() {
        super("inventory_halitosis_generator");
    }

    public static void initOverrides(InventoryGeneratorItem item) {
        ItemProperties.register(item, ResourceLocation.fromNamespaceAndPath(InventoryGenerators.MODID, "on"), (stack, worldIn, entityIn, integer) -> {
            if (stack.getItem() instanceof IInventoryGenerator inventoryGenerator) {
                int burnTime = inventoryGenerator.getBurnTime(stack);
                if (inventoryGenerator.isOn(stack) && burnTime > 0) {
                    int time = (inventoryGenerator).calculateTime(stack, inventoryGenerator.getFuel(stack, worldIn), worldIn);
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

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        return halitosisGeneratorCapacity;
    }
}
