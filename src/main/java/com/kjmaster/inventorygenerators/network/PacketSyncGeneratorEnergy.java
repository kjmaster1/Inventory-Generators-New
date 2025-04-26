package com.kjmaster.inventorygenerators.network;

import mcjty.lib.network.CustomPacketPayload;
import mcjty.lib.network.PlayPayloadContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import static com.kjmaster.inventorygenerators.InventoryGenerators.MODID;

public record PacketSyncGeneratorEnergy(int energy) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(MODID, "syncgeneratorenergy");

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(energy);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public static PacketSyncGeneratorEnergy create(FriendlyByteBuf buf) {
        return new PacketSyncGeneratorEnergy(buf.readInt());
    }

    public static PacketSyncGeneratorEnergy create(int energy) {
        return new PacketSyncGeneratorEnergy(energy);
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            SyncGeneratorEnergyHelper.syncEnergy(energy);
        });
    }
}
