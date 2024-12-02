package com.kjmaster.inventorygenerators.generators;


import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

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

            PrimedTnt primedTnt = new PrimedTnt(player.level(), player.getX(), player.getY(), player.getZ(), player) {

                @Override
                public boolean shouldBlockExplode(Explosion pExplosion, BlockGetter pLevel, BlockPos pPos, BlockState pBlockState, float pExplosionPower) {
                    return false;
                }

                @Override
                protected void explode() {
                    ExplosionDamageCalculator explosionDamageCalculator = new ExplosionDamageCalculator() {
                        @Override
                        public float getEntityDamageAmount(Explosion explosion, Entity entity) {
                            return super.getEntityDamageAmount(explosion, entity) / explosionDamageDivisor;
                        }
                    };
                    this.level().explode(this, Explosion.getDefaultDamageSource(this.level(), this), explosionDamageCalculator, this.getX(), this.getY(0.0625), this.getZ(), 4.0F, false, Level.ExplosionInteraction.TNT);
                }
            };

            primedTnt.setFuse(0);
            player.level().addFreshEntity(primedTnt);
        }
    }
}
