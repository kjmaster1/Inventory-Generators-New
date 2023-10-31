package com.kjmaster.inventorygenerators.network;

import com.kjmaster.inventorygenerators.generators.IInventoryGenerator;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class PacketChangeMode {

    public PacketChangeMode(FriendlyByteBuf buf) {

    }

    public void toBytes(FriendlyByteBuf buf) {

    }

    public void handle(SimpleChannel channel, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player != null) {
                ItemStack stack = player.getItemInHand(player.getUsedItemHand());
                if (stack.getItem() instanceof IInventoryGenerator inventoryGenerator) {
                    inventoryGenerator.changeMode(stack, inventoryGenerator, player);
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}
