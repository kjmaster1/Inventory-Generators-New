package com.kjmaster.inventorygenerators.network;

import mcjty.lib.network.CustomPacketPayload;
import mcjty.lib.network.PlayPayloadContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

import static com.kjmaster.inventorygenerators.InventoryGenerators.MODID;

public record PacketSyncGeneratorEnergy(int energy, UUID uuid) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(MODID, "syncgeneratorenergy");

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(energy);
        friendlyByteBuf.writeUUID(uuid);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public static PacketSyncGeneratorEnergy create(FriendlyByteBuf buf) {
        return new PacketSyncGeneratorEnergy(buf.readInt(), buf.readUUID());
    }

    public static PacketSyncGeneratorEnergy create(int energy, UUID uuid) {
        return new PacketSyncGeneratorEnergy(energy, uuid);
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            SyncGeneratorEnergyHelper.syncEnergy(energy, uuid);
        });
    }
}
