package com.kjmaster.inventorygenerators.network;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

import static com.kjmaster.inventorygenerators.InventoryGenerators.MODID;

public record PacketSyncGeneratorEnergy(int energy, UUID uuid) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(MODID, "syncgeneratorenergy");
    public static final CustomPacketPayload.Type<PacketSyncGeneratorEnergy> TYPE = new Type<>(ID);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, PacketSyncGeneratorEnergy> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PacketSyncGeneratorEnergy::energy,
            UUIDUtil.STREAM_CODEC, PacketSyncGeneratorEnergy::uuid,
            PacketSyncGeneratorEnergy::new);

    public static PacketSyncGeneratorEnergy create(int energy, UUID uuid) {
        return new PacketSyncGeneratorEnergy(energy, uuid);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            SyncGeneratorEnergyHelper.syncEnergy(energy, uuid);
        });
    }
}
