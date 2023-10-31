package com.kjmaster.inventorygenerators.generators;

import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.kjmaster.inventorygenerators.setup.Config.*;
import static net.minecraft.world.level.Explosion.getSeenPercent;

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

            // Please let me know if there's a better way to do custom explosion damage than this mess...


            PrimedTnt primedTnt = new PrimedTnt(player.level(), player.getX(), player.getY(), player.getZ(), player) {

                @Override
                public boolean shouldBlockExplode(Explosion pExplosion, BlockGetter pLevel, BlockPos pPos, BlockState pBlockState, float pExplosionPower) {
                    return false;
                }

                @Override
                protected void explode() {
                    ExplosionDamageCalculator explosionDamageCalculator = new ExplosionDamageCalculator();

                    Explosion.BlockInteraction explosion$blockinteraction1 = Explosion.BlockInteraction.KEEP;
                    Explosion explosion = new Explosion(level(), player, null, new ExplosionDamageCalculator(), player.getX(), player.getY(), player.getZ(), 4.0F, false, explosion$blockinteraction1);
                    if (net.minecraftforge.event.ForgeEventFactory.onExplosionStart(level(), explosion))
                        return;
                    explode(explosion, explosionDamageCalculator);

                    if (this.level() instanceof ServerLevel serverLevel) {
                        serverLevel.playSound(null, new BlockPos(this.getBlockX(), this.getBlockY(), this.getBlockZ()), SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0F, (1.0F + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2F) * 0.7F);
                        serverLevel.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY() + 0.5, this.getZ(), 10, 1.0D, 0.0D, 0.0D, 1.0D);
                    }
                }

                public void explode(Explosion explosion, ExplosionDamageCalculator explosionDamageCalculator) {
                    Set<BlockPos> set = Sets.newHashSet();
                    int i = 16;

                    for (int j = 0; j < 16; ++j) {
                        for (int k = 0; k < 16; ++k) {
                            for (int l = 0; l < 16; ++l) {
                                if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
                                    double d0 = (float) j / 15.0F * 2.0F - 1.0F;
                                    double d1 = (float) k / 15.0F * 2.0F - 1.0F;
                                    double d2 = (float) l / 15.0F * 2.0F - 1.0F;
                                    double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                                    d0 /= d3;
                                    d1 /= d3;
                                    d2 /= d3;
                                    float f = 4.0F * (0.7F + this.level().random.nextFloat() * 0.6F);
                                    double d4 = this.getX();
                                    double d6 = this.getY();
                                    double d8 = this.getZ();

                                    for (float f1 = 0.3F; f > 0.0F; f -= 0.22500001F) {
                                        BlockPos blockpos = BlockPos.containing(d4, d6, d8);
                                        BlockState blockstate = this.level().getBlockState(blockpos);
                                        FluidState fluidstate = this.level().getFluidState(blockpos);
                                        if (!this.level().isInWorldBounds(blockpos)) {
                                            break;
                                        }

                                        Optional<Float> optional = explosionDamageCalculator.getBlockExplosionResistance(explosion, this.level(), blockpos, blockstate, fluidstate);
                                        if (optional.isPresent()) {
                                            f -= (optional.get() + 0.3F) * 0.3F;
                                        }

                                        if (f > 0.0F && explosionDamageCalculator.shouldBlockExplode(explosion, this.level(), blockpos, blockstate, f)) {
                                            set.add(blockpos);
                                        }

                                        d4 += d0 * (double) 0.3F;
                                        d6 += d1 * (double) 0.3F;
                                        d8 += d2 * (double) 0.3F;
                                    }
                                }
                            }
                        }
                    }

                    explosion.getToBlow().addAll(set);
                    float f2 = 4.0f * 2.0F;
                    int k1 = Mth.floor(this.getX() - (double) f2 - 1.0D);
                    int l1 = Mth.floor(this.getX() + (double) f2 + 1.0D);
                    int i2 = Mth.floor(this.getY() - (double) f2 - 1.0D);
                    int i1 = Mth.floor(this.getY() + (double) f2 + 1.0D);
                    int j2 = Mth.floor(this.getZ() - (double) f2 - 1.0D);
                    int j1 = Mth.floor(this.getZ() + (double) f2 + 1.0D);
                    List<Entity> list = this.level().getEntities(this, new AABB(k1, i2, j2, l1, i1, j1));
                    net.minecraftforge.event.ForgeEventFactory.onExplosionDetonate(this.level(), explosion, list, f2);
                    Vec3 vec3 = new Vec3(this.getX(), this.getY(), this.getZ());

                    for (Entity entity : list) {
                        if (!entity.ignoreExplosion()) {
                            double d12 = Math.sqrt(entity.distanceToSqr(vec3)) / (double) f2;
                            if (d12 <= 1.0D) {
                                double d5 = entity.getX() - this.getX();
                                double d7 = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - this.getY();
                                double d9 = entity.getZ() - this.getZ();
                                double d13 = Math.sqrt(d5 * d5 + d7 * d7 + d9 * d9);
                                if (d13 != 0.0D) {
                                    d5 /= d13;
                                    d7 /= d13;
                                    d9 /= d13;
                                    double d14 = getSeenPercent(vec3, entity);
                                    double d10 = (1.0D - d12) * d14;

                                    // The single line that we actually care about
                                    entity.hurt(explosion.getDamageSource(), ((float) ((int) ((d10 * d10 + d10) / 2.0D * 7.0D * (double) f2 + 1.0D))) / explosionDamageDivisor);

                                    double d11;
                                    if (entity instanceof LivingEntity livingentity) {
                                        d11 = ProtectionEnchantment.getExplosionKnockbackAfterDampener(livingentity, d10);
                                    } else {
                                        d11 = d10;
                                    }

                                    d5 *= d11;
                                    d7 *= d11;
                                    d9 *= d11;
                                    Vec3 vec31 = new Vec3(d5, d7, d9);
                                    entity.setDeltaMovement(entity.getDeltaMovement().add(vec31));
                                    if (entity instanceof Player player) {
                                        if (!player.isSpectator() && (!player.isCreative() || !player.getAbilities().flying)) {
                                            explosion.getHitPlayers().put(player, vec31);
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            };

            primedTnt.setFuse(0);
            player.level().addFreshEntity(primedTnt);
        }
    }
}
