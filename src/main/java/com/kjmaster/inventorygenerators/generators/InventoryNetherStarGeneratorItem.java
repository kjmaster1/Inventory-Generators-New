package com.kjmaster.inventorygenerators.generators;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import static com.kjmaster.inventorygenerators.setup.Config.*;

public class InventoryNetherStarGeneratorItem extends InventoryGeneratorItem {

    public InventoryNetherStarGeneratorItem() {
        super("inventory_nether_star_generator");
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        return netherStarGeneratorCapacity;
    }

    @Override
    public boolean hasSideEffect() {
        return true;
    }

    @Override
    public void giveSideEffect(Player player) {
        player.addEffect(new MobEffectInstance(MobEffects.WITHER, witherDuration, witherAmplifier));
    }
}
