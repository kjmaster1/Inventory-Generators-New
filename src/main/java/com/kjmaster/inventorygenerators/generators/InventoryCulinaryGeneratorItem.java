package com.kjmaster.inventorygenerators.generators;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

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
    public boolean isItemValid(ItemStack stack) {
        if (stack.getItem().isEdible()) {
            return true;
        }
        return super.isItemValid(stack);
    }

    @Override
    public int calculateTime(ItemStack stack) {
        if (!stack.isEmpty()) {
            Item item = stack.getItem();
            FoodProperties foodProperties = item.getFoodProperties();
            if (foodProperties != null) {
                int healAmount = getHealAmount(foodProperties);
                int energyOutput = nerfLevels(healAmount * getSaturationModifier(foodProperties) * 8000, 64000);
                int energyRate = getEnergyRate(foodProperties);
                return Math.round((float) energyOutput / (float) energyRate);
            }
        }
        return super.calculateTime(stack);
    }

    @Override
    public int calculatePower(ItemStack stack) {
        Item fuel = getCurrentFuel(stack).getItem();
        FoodProperties foodProperties = fuel.getFoodProperties();
        if (foodProperties != null) {
            return Math.min(getMaxEnergyStored(stack) - getInternalEnergyStored(stack),  getEnergyRate(foodProperties));
        }
        return super.calculatePower(stack);
    }

    private int getEnergyRate(FoodProperties food) {
        int healAmount = getHealAmount(food);
        return Math.max(nerfLevels(healAmount * 8, 64), 1);
    }

    private int nerfLevels(double energy, double maxLevel) {
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

    private int getHealAmount(FoodProperties food) {
        float healAmount = food.getNutrition();
        for (Pair<MobEffectInstance, Float> pair : food.getEffects()) {
            MobEffectInstance effectInstance = pair.getFirst();
            MobEffect mobEffect = effectInstance.getEffect();
            if (mobEffect == MobEffects.HEAL) {
                healAmount += Math.min(4 << effectInstance.getAmplifier(), 20);
            } else if (mobEffect == MobEffects.SATURATION) {
                healAmount += (effectInstance.getAmplifier() + 1) / 2F;
            } else if (mobEffect == MobEffects.REGENERATION) {
                healAmount += effectInstance.getDuration() / (float)(50 >> effectInstance.getAmplifier()) / 4F;
            } else if (mobEffect == MobEffects.ABSORPTION) {
                healAmount += (effectInstance.getAmplifier() + 1) / 2F;
            } else {
                healAmount += 1;
            }
        }
        return (int) (healAmount + 0.5f);
    }

    private float getSaturationModifier(FoodProperties food) {
        float saturationModifier = food.getSaturationModifier();
        for (Pair<MobEffectInstance, Float> pair : food.getEffects()) {
            MobEffectInstance effectInstance = pair.getFirst();
            MobEffect mobEffect = effectInstance.getEffect();
            if (mobEffect == MobEffects.SATURATION) {
                saturationModifier = Math.max(1F, saturationModifier);
            }
        }
        return saturationModifier;
    }
}
