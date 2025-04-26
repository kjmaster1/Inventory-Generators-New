package com.kjmaster.inventorygenerators.setup;

import com.kjmaster.inventorygenerators.InventoryGenerators;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = InventoryGenerators.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    static {
        init(BUILDER);
        SPEC = BUILDER.build();
    }

    private static ForgeConfigSpec.BooleanValue DO_SIDE_EFFECTS;
    private static ForgeConfigSpec.BooleanValue DO_SEND_ENERGY;
    private static ForgeConfigSpec.IntValue CULINARY_GENERATOR_CAPACITY;
    private static ForgeConfigSpec.IntValue DEATH_GENERATOR_CAPACITY;
    private static ForgeConfigSpec.IntValue END_GENERATOR_CAPACITY;
    private static ForgeConfigSpec.IntValue EXPLOSIVE_GENERATOR_CAPACITY;
    private static ForgeConfigSpec.IntValue FROSTY_GENERATOR_CAPACITY;
    private static ForgeConfigSpec.IntValue FURNACE_GENERATOR_CAPACITY;
    private static ForgeConfigSpec.IntValue HALITOSIS_GENERATOR_CAPACITY;
    private static ForgeConfigSpec.IntValue NETHER_STAR_GENERATOR_CAPACITY;
    private static ForgeConfigSpec.IntValue OVERCLOCKED_GENERATOR_CAPACITY;
    private static ForgeConfigSpec.IntValue PINK_GENERATOR_CAPACITY;
    private static ForgeConfigSpec.IntValue POTION_GENERATOR_CAPACITY;
    private static ForgeConfigSpec.IntValue SLIMEY_GENERATOR_CAPACITY;
    private static ForgeConfigSpec.IntValue SURVIVALIST_GENERATOR_CAPACITY;
    private static ForgeConfigSpec.DoubleValue FURNACE_GENERATOR_DIVISOR;
    private static ForgeConfigSpec.IntValue FURNACE_GENERATOR_RF_PER_TICK;
    private static ForgeConfigSpec.DoubleValue OVERCLOCKED_GENERATOR_DIVISOR;
    private static ForgeConfigSpec.IntValue OVERCLOCKED_GENERATOR_MIN_RF_PER_TICK;
    private static ForgeConfigSpec.IntValue SURVIVALIST_GENERATOR_RF_PER_TICK;
    private static ForgeConfigSpec.IntValue EXPLOSIVE_GENERATOR_SIDE_EFFECT_PROBABILITY;
    private static ForgeConfigSpec.IntValue EXPLOSION_DAMAGE_DIVISOR;
    private static ForgeConfigSpec.IntValue WITHER_DURATION;
    private static ForgeConfigSpec.IntValue WITHER_AMPLIFIER;
    public static boolean doSideEffects;
    public static boolean doSendEnergy;

    public static int culinaryGeneratorCapacity;
    public static int deathGeneratorCapacity;
    public static int endGeneratorCapacity;
    public static int explosiveGeneratorCapacity;
    public static int frostyGeneratorCapacity;
    public static int furnaceGeneratorCapacity;
    public static int halitosisGeneratorCapacity;
    public static int netherStarGeneratorCapacity;
    public static int overclockedGeneratorCapacity;
    public static int pinkGeneratorCapacity;
    public static int potionGeneratorCapacity;
    public static int slimeyGeneratorCapacity;
    public static int survivalistGeneratorCapacity;

    public static double furnaceGeneratorDivisor;
    public static int furnaceGeneratorRfPerTick;
    public static double overclockedGeneratorDivisor;
    public static int overclockedGeneratorMinRfPerTick;
    public static int survivalistGeneratorRfPerTick;

    public static int explosiveGeneratorSideEffectProbability;
    public static int explosionDamageDivisor;

    public static int witherDuration;
    public static int witherAmplifier;

    public static void init(ForgeConfigSpec.Builder BUILDER) {
        DO_SIDE_EFFECTS = BUILDER
                .comment("Whether Inventory Generators have side effects or not")
                .define("doSideEffects", true);
        DO_SEND_ENERGY = BUILDER
                .comment("Whether Inventory Generators send energy to items in inventory and curios slots when in charging mode")
                .define("doSendEnergy", true);
        CULINARY_GENERATOR_CAPACITY = BUILDER
                .comment("Energy capacity of the inventory culinary generator")
                .defineInRange("culinaryGeneratorCapacity", 100000, 0, Integer.MAX_VALUE);
        DEATH_GENERATOR_CAPACITY = BUILDER
                .comment("Energy capacity of the inventory death generator")
                .defineInRange("deathGeneratorCapacity", 100000, 0, Integer.MAX_VALUE);
        END_GENERATOR_CAPACITY = BUILDER
                .comment("Energy capacity of the inventory end generator")
                .defineInRange("endGeneratorCapacity", 100000, 0, Integer.MAX_VALUE);
        EXPLOSIVE_GENERATOR_CAPACITY = BUILDER
                .comment("Energy capacity of the inventory explosive generator")
                .defineInRange("explosiveGeneratorCapacity", 100000, 0, Integer.MAX_VALUE);
        FROSTY_GENERATOR_CAPACITY = BUILDER
                .comment("Energy capacity of the inventory frosty generator")
                .defineInRange("frostyGeneratorCapacity", 100000, 0, Integer.MAX_VALUE);
        FURNACE_GENERATOR_CAPACITY = BUILDER
                .comment("Energy capacity of the inventory furnace generator")
                .defineInRange("furnaceGeneratorCapacity", 100000, 0, Integer.MAX_VALUE);
        HALITOSIS_GENERATOR_CAPACITY = BUILDER
                .comment("Energy capacity of the inventory halitosis generator")
                .defineInRange("halitosisGeneratorCapacity", 100000, 0, Integer.MAX_VALUE);
        NETHER_STAR_GENERATOR_CAPACITY = BUILDER
                .comment("Energy capacity of the inventory nether star generator")
                .defineInRange("netherStarGeneratorCapacity", 10000000, 0, Integer.MAX_VALUE);
        OVERCLOCKED_GENERATOR_CAPACITY = BUILDER
                .comment("Energy capacity of the inventory overclocked generator")
                .defineInRange("overclockedGeneratorCapacity", 1000000, 0, Integer.MAX_VALUE);
        PINK_GENERATOR_CAPACITY = BUILDER
                .comment("Energy capacity of the inventory pink generator")
                .defineInRange("pinkGeneratorCapacity", 10000, 0, Integer.MAX_VALUE);
        POTION_GENERATOR_CAPACITY = BUILDER
                .comment("Energy capacity of the inventory potion generator")
                .defineInRange("potionGeneratorCapacity", 100000, 0, Integer.MAX_VALUE);
        SLIMEY_GENERATOR_CAPACITY = BUILDER
                .comment("Energy capacity of the inventory slimey generator")
                .defineInRange("slimeyGeneratorCapacity", 100000, 0, Integer.MAX_VALUE);
        SURVIVALIST_GENERATOR_CAPACITY = BUILDER
                .comment("Energy capacity of the inventory survivalist generator")
                .defineInRange("survivalistGeneratorCapacity", 10000, 0, Integer.MAX_VALUE);
        FURNACE_GENERATOR_DIVISOR = BUILDER
                .comment("What the burn time of items should be divided by to calculate their generator burn time for the furnace generator")
                .defineInRange("furnaceGeneratorDivisor", 16D, 1, Double.MAX_VALUE);
        FURNACE_GENERATOR_RF_PER_TICK = BUILDER
                .comment("The amount of rf per tick generated by burning items in the furnace generator")
                .defineInRange("furnaceGeneratorRfPerTick", 40, 0, Integer.MAX_VALUE);
        OVERCLOCKED_GENERATOR_DIVISOR = BUILDER
                .comment("What the burn time of items should be divided by to calculate their generator burn time for the overclocked generator")
                .defineInRange("overclockedGeneratorDivisor", 4000D, 1, Double.MAX_VALUE);
        OVERCLOCKED_GENERATOR_MIN_RF_PER_TICK = BUILDER
                .comment("The amount of rf per tick the overclocked generator will compare to the burn time of the fuel. The overclocked generator then sends the smaller of the two as the rf per tick")
                .defineInRange("overclockedGeneratorDivisor", 4000, 1, Integer.MAX_VALUE);
        SURVIVALIST_GENERATOR_RF_PER_TICK = BUILDER
                .comment("The amount of rf per tick generated by burning items in the survivalist generator")
                .defineInRange("survivalistGeneratorRfPerTick", 5, 0, Integer.MAX_VALUE);
        EXPLOSIVE_GENERATOR_SIDE_EFFECT_PROBABILITY = BUILDER
                .comment("The probability that the explosive generator will do it's side effect of exploding the player each tick. Calculated as 1/input")
                .defineInRange("explosiveGeneratorSideEffectProbability", 400, 1, Integer.MAX_VALUE);
        EXPLOSION_DAMAGE_DIVISOR = BUILDER
                .comment("The amount that the overall explosion damage of the explosive generator side effect will be divided by to make it less lethal")
                .defineInRange("explosionDamageDivisor", 8, 1, Integer.MAX_VALUE);
        WITHER_DURATION = BUILDER
                .comment("The duration of the wither effect that the nether star generator gives you")
                .defineInRange("witherDuration", 20, 0, Integer.MAX_VALUE);
        WITHER_AMPLIFIER = BUILDER
                .comment("The amplifier of the wither effect that the nether star generator gives you")
                .defineInRange("witherAmplifier", 9, 0, Integer.MAX_VALUE);
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        doSideEffects = DO_SIDE_EFFECTS.get();
        doSendEnergy = DO_SEND_ENERGY.get();

        culinaryGeneratorCapacity = CULINARY_GENERATOR_CAPACITY.get();
        deathGeneratorCapacity = DEATH_GENERATOR_CAPACITY.get();
        endGeneratorCapacity = END_GENERATOR_CAPACITY.get();
        explosiveGeneratorCapacity = EXPLOSIVE_GENERATOR_CAPACITY.get();
        frostyGeneratorCapacity = FROSTY_GENERATOR_CAPACITY.get();
        furnaceGeneratorCapacity = FURNACE_GENERATOR_CAPACITY.get();
        halitosisGeneratorCapacity = HALITOSIS_GENERATOR_CAPACITY.get();
        netherStarGeneratorCapacity = NETHER_STAR_GENERATOR_CAPACITY.get();
        overclockedGeneratorCapacity = OVERCLOCKED_GENERATOR_CAPACITY.get();
        pinkGeneratorCapacity = PINK_GENERATOR_CAPACITY.get();
        potionGeneratorCapacity = POTION_GENERATOR_CAPACITY.get();
        slimeyGeneratorCapacity = SLIMEY_GENERATOR_CAPACITY.get();
        survivalistGeneratorCapacity = SURVIVALIST_GENERATOR_CAPACITY.get();

        furnaceGeneratorDivisor = FURNACE_GENERATOR_DIVISOR.get();
        furnaceGeneratorRfPerTick = FURNACE_GENERATOR_RF_PER_TICK.get();
        overclockedGeneratorDivisor = OVERCLOCKED_GENERATOR_DIVISOR.get();
        overclockedGeneratorMinRfPerTick = OVERCLOCKED_GENERATOR_MIN_RF_PER_TICK.get();
        survivalistGeneratorRfPerTick = SURVIVALIST_GENERATOR_RF_PER_TICK.get();

        explosiveGeneratorSideEffectProbability = EXPLOSIVE_GENERATOR_SIDE_EFFECT_PROBABILITY.get();
        explosionDamageDivisor = EXPLOSION_DAMAGE_DIVISOR.get();

        witherDuration = WITHER_DURATION.get();
        witherAmplifier = WITHER_AMPLIFIER.get();
    }
}
