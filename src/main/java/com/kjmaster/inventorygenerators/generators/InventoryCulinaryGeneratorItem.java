package com.kjmaster.inventorygenerators.generators;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import static com.kjmaster.inventorygenerators.setup.Config.culinaryGeneratorCapacity;

public class InventoryCulinaryGeneratorItem extends InventoryGeneratorItem {

    public InventoryCulinaryGeneratorItem() {
        super("inventory_culinary_generator");
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        return culinaryGeneratorCapacity;
    }

    @Override
    public boolean isItemValid(ItemStack generator, ItemStack fuel, Level level) {
        if (fuel.getFoodProperties(null) != null) {
            return true;
        }
        return super.isItemValid(generator, fuel, level);
    }

    @Override
    public int calculateTime(ItemStack generator, ItemStack fuel, Level level) {
        if (!fuel.isEmpty()) {
            Item item = fuel.getItem();
            FoodProperties foodProperties = item.getFoodProperties(fuel, null);
            if (foodProperties != null) {
                int healAmount = getHealAmount(foodProperties);
                int energyOutput = nerfLevels(healAmount * getSaturationModifier(foodProperties) * 8000, 64000);
                int energyRate = getEnergyRate(foodProperties);
                return Math.round((float) energyOutput / (float) energyRate);
            }
        }
        return super.calculateTime(generator, fuel, level);
    }

    @Override
    public int calculatePower(ItemStack generator, Level level) {
        Item fuel = getCurrentFuel(generator).getItem();
        FoodProperties foodProperties = fuel.getFoodProperties(getCurrentFuel(generator), null);
        if (foodProperties != null) {
            return Math.min(getMaxEnergyStored(generator) - getInternalEnergyStored(generator), getEnergyRate(foodProperties));
        }
        return super.calculatePower(generator, level);
    }

    public static int getEnergyRate(FoodProperties food) {
        int healAmount = getHealAmount(food);
        return Math.max(nerfLevels(healAmount * 8, 64), 1);
    }

    public static int nerfLevels(double energy, double maxLevel) {
        if (energy < maxLevel) return (int) Math.ceil(energy);
        double f = 1;
        double totalEnergy = 0;
        while (energy > maxLevel) {
            totalEnergy += maxLevel / f;
            energy -= maxLevel;
            f += 1;

        }
        totalEnergy += energy / f;
        return (int) Math.ceil(totalEnergy);
    }

    public static int getHealAmount(FoodProperties food) {
        float healAmount = food.nutrition();
        for (FoodProperties.PossibleEffect possibleEffect : food.effects()) {
            MobEffectInstance effectInstance = possibleEffect.effect();
            MobEffect mobEffect = effectInstance.getEffect().value();
            if (mobEffect == MobEffects.HEAL) {
                healAmount += Math.min(4 << effectInstance.getAmplifier(), 20);
            } else if (mobEffect == MobEffects.SATURATION) {
                healAmount += (effectInstance.getAmplifier() + 1) / 2F;
            } else if (mobEffect == MobEffects.REGENERATION) {
                healAmount += effectInstance.getDuration() / (float) (50 >> effectInstance.getAmplifier()) / 4F;
            } else if (mobEffect == MobEffects.ABSORPTION) {
                healAmount += (effectInstance.getAmplifier() + 1) / 2F;
            } else {
                healAmount += 1;
            }
        }
        return (int) (healAmount + 0.5f);
    }

    public static float getSaturationModifier(FoodProperties food) {
        float saturationModifier = food.saturation();
        for (FoodProperties.PossibleEffect possibleEffect : food.effects()) {
            MobEffectInstance effectInstance = possibleEffect.effect();
            MobEffect mobEffect = effectInstance.getEffect().value();
            if (mobEffect == MobEffects.SATURATION) {
                saturationModifier = Math.max(1F, saturationModifier);
            }
        }
        return saturationModifier;
    }
}
