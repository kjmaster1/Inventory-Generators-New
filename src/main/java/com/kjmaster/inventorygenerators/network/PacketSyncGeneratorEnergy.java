package com.kjmaster.inventorygenerators.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSyncGeneratorEnergy {

    private final int energy;

    public PacketSyncGeneratorEnergy(FriendlyByteBuf buf) {
        energy = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(energy);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            SyncGeneratorEnergyHelper.syncEnergy(energy);
        });
        ctx.setPacketHandled(true);
    }
}
