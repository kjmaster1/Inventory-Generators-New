package com.kjmaster.inventorygenerators.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.kjmaster.inventorygenerators.InventoryGenerators.MODID;

public record PacketSyncGeneratorEnergy(int energy) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(MODID, "syncgeneratorenergy");
    public static final CustomPacketPayload.Type<PacketSyncGeneratorEnergy> TYPE = new Type<>(ID);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, PacketSyncGeneratorEnergy> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PacketSyncGeneratorEnergy::energy,
            PacketSyncGeneratorEnergy::new);

    public static PacketSyncGeneratorEnergy create(int energy) {
        return new PacketSyncGeneratorEnergy(energy);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            SyncGeneratorEnergyHelper.syncEnergy(energy);
        });
    }
}
