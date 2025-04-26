package com.kjmaster.inventorygenerators.generators;


import com.kjmaster.inventorygenerators.utils.CustomExplosion;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.ForgeEventFactory;

import static com.kjmaster.inventorygenerators.setup.Config.*;

public class InventoryExplosiveGeneratorItem extends InventoryGeneratorItem {

    public InventoryExplosiveGeneratorItem() {
        super("inventory_explosive_generator");
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        return explosiveGeneratorCapacity;
    }

    @Override
    public boolean hasSideEffect() {
        return true;
    }

    @Override
    public void giveSideEffect(Player player) {
        if (player.getRandom().nextIntBetweenInclusive(1, explosiveGeneratorSideEffectProbability) == 1) {
            // Is there a better way to do custom explosion damage than this?
            CustomExplosion explosion = new CustomExplosion(player.level(), null,  player.getX(), player.getY(), player.getZ(), 4.0F, false, Explosion.BlockInteraction.KEEP);
            if (!ForgeEventFactory.onExplosionStart(player.level(), explosion)) {
                explosion.explode();
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0F, (1.0F + (player.level().random.nextFloat() - player.level().random.nextFloat()) * 0.2F) * 0.7F);
                explosion.finalizeExplosion(true);
            }
        }
    }
}
