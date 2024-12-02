package com.kjmaster.inventorygenerators.setup;

import com.kjmaster.inventorygenerators.InventoryGenerators;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class InvGensDataComponents {

    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, InventoryGenerators.MODID);


    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> GENERATOR_ON = COMPONENTS.register("generator_on", () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> GENERATOR_CHARGING = COMPONENTS.register("generator_charging", () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> GENERATOR_BURN_TIME = COMPONENTS.register("generator_burn_time", () -> DataComponentType.<Integer>builder().persistent(Codec.INT.orElse(0)).networkSynchronized(ByteBufCodecs.VAR_INT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemContainerContents>> GENERATOR_CONTENTS = COMPONENTS.register("generator_contents", () -> DataComponentType.<ItemContainerContents>builder().persistent(ItemContainerContents.CODEC).networkSynchronized(ByteBufCodecs.fromCodec(ItemContainerContents.CODEC)).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FORGE_ENERGY = COMPONENTS.register("forge_energy", () -> DataComponentType.<Integer>builder().persistent(Codec.INT.orElse(0)).networkSynchronized(ByteBufCodecs.VAR_INT).build());

    private static @NotNull <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, final Codec<T> codec) {
        return register(name, codec, null);
    }

    private static @NotNull <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, final Codec<T> codec, @Nullable final StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        if (streamCodec == null) {
            return COMPONENTS.register(name, () -> DataComponentType.<T>builder().persistent(codec).build());
        } else {
            return COMPONENTS.register(name, () -> DataComponentType.<T>builder().persistent(codec).networkSynchronized(streamCodec).build());
        }
    }
}
