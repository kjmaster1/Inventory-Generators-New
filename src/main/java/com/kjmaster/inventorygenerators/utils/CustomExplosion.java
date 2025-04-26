package com.kjmaster.inventorygenerators.utils;

import com.google.common.collect.Sets;
import com.kjmaster.inventorygenerators.setup.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.EntityBasedExplosionDamageCalculator;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class CustomExplosion extends Explosion {

    private static final ExplosionDamageCalculator EXPLOSION_DAMAGE_CALCULATOR = new ExplosionDamageCalculator();

    private final Level level;
    private final Entity source;
    private final float radius;
    private final ExplosionDamageCalculator damageCalculator;

    public CustomExplosion(Level pLevel, @Nullable Entity pSource, double pToBlowX, double pToBlowY, double pToBlowZ, float pRadius, boolean pFire, BlockInteraction pBlockInteraction) {
        super(pLevel, pSource, pToBlowX, pToBlowY, pToBlowZ, pRadius, pFire, pBlockInteraction);
        this.level = pLevel;
        this.source = pSource;
        this.radius = pRadius;
        this.damageCalculator = makeDamageCalculator(pSource);
    }

    private ExplosionDamageCalculator makeDamageCalculator(@javax.annotation.Nullable Entity pEntity) {
        return (ExplosionDamageCalculator) (pEntity == null ? EXPLOSION_DAMAGE_CALCULATOR : new EntityBasedExplosionDamageCalculator(pEntity));
    }

    @Override
    public void explode() {
        this.level.gameEvent(this.source, GameEvent.EXPLODE, new Vec3(this.getPosition().x, this.getPosition().y, this.getPosition().z));
        Set<BlockPos> set = Sets.newHashSet();
        int i = 16;

        for (int j = 0; j < 16; ++j) {
            for (int k = 0; k < 16; ++k) {
                for (int l = 0; l < 16; ++l) {
                    if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
                        double d0 = (double) ((float) j / 15.0F * 2.0F - 1.0F);
                        double d1 = (double) ((float) k / 15.0F * 2.0F - 1.0F);
                        double d2 = (double) ((float) l / 15.0F * 2.0F - 1.0F);
                        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                        d0 /= d3;
                        d1 /= d3;
                        d2 /= d3;
                        float f = this.radius * (0.7F + this.level.random.nextFloat() * 0.6F);
                        double d4 = this.getPosition().x;
                        double d6 = this.getPosition().y;
                        double d8 = this.getPosition().z;

                        for (float f1 = 0.3F; f > 0.0F; f -= 0.22500001F) {
                            BlockPos blockpos = BlockPos.containing(d4, d6, d8);
                            BlockState blockstate = this.level.getBlockState(blockpos);
                            FluidState fluidstate = this.level.getFluidState(blockpos);
                            if (!this.level.isInWorldBounds(blockpos)) {
                                break;
                            }

                            Optional<Float> optional = this.damageCalculator.getBlockExplosionResistance(this, this.level, blockpos, blockstate, fluidstate);
                            if (optional.isPresent()) {
                                f -= ((Float) optional.get() + 0.3F) * 0.3F;
                            }

                            if (f > 0.0F && this.damageCalculator.shouldBlockExplode(this, this.level, blockpos, blockstate, f)) {
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

        this.getToBlow().addAll(set);
        float f2 = this.radius * 2.0F;
        int k1 = Mth.floor(this.getPosition().x - (double) f2 - (double) 1.0F);
        int l1 = Mth.floor(this.getPosition().x + (double) f2 + (double) 1.0F);
        int i2 = Mth.floor(this.getPosition().y - (double) f2 - (double) 1.0F);
        int i1 = Mth.floor(this.getPosition().y + (double) f2 + (double) 1.0F);
        int j2 = Mth.floor(this.getPosition().z - (double) f2 - (double) 1.0F);
        int j1 = Mth.floor(this.getPosition().z + (double) f2 + (double) 1.0F);
        List<Entity> list = this.level.getEntities(this.source, new AABB((double) k1, (double) i2, (double) j2, (double) l1, (double) i1, (double) j1));
        ForgeEventFactory.onExplosionDetonate(this.level, this, list, (double) f2);
        Vec3 vec3 = new Vec3(this.getPosition().x, this.getPosition().y, this.getPosition().z);

        for (int k2 = 0; k2 < list.size(); ++k2) {
            Entity entity = (Entity) list.get(k2);
            if (!entity.ignoreExplosion()) {
                double d12 = Math.sqrt(entity.distanceToSqr(vec3)) / (double) f2;
                if (d12 <= (double) 1.0F) {
                    double d5 = entity.getX() - this.getPosition().x;
                    double d7 = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - this.getPosition().y;
                    double d9 = entity.getZ() - this.getPosition().z;
                    double d13 = Math.sqrt(d5 * d5 + d7 * d7 + d9 * d9);
                    if (d13 != (double) 0.0F) {
                        d5 /= d13;
                        d7 /= d13;
                        d9 /= d13;
                        double d14 = (double) getSeenPercent(vec3, entity);
                        double d10 = ((double) 1.0F - d12) * d14;
                        entity.hurt(this.getDamageSource(), ((float) ((int) ((d10 * d10 + d10) / (double) 2.0F * (double) 7.0F * (double) f2 + (double) 1.0F))) / Config.explosionDamageDivisor);
                        double d11;
                        if (entity instanceof LivingEntity) {
                            LivingEntity livingentity = (LivingEntity) entity;
                            d11 = ProtectionEnchantment.getExplosionKnockbackAfterDampener(livingentity, d10);
                        } else {
                            d11 = d10;
                        }

                        d5 *= d11;
                        d7 *= d11;
                        d9 *= d11;
                        Vec3 vec31 = new Vec3(d5, d7, d9);
                        entity.setDeltaMovement(entity.getDeltaMovement().add(vec31));
                        if (entity instanceof Player) {
                            Player player = (Player) entity;
                            if (!player.isSpectator() && (!player.isCreative() || !player.getAbilities().flying)) {
                                getHitPlayers().put(player, vec31);
                            }
                        }
                    }
                }
            }
        }
    }
}
