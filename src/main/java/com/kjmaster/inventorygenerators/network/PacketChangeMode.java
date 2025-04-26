package com.kjmaster.inventorygenerators.network;

import com.kjmaster.inventorygenerators.generators.IInventoryGenerator;
import mcjty.lib.network.CustomPacketPayload;
import mcjty.lib.network.PlayPayloadContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import static com.kjmaster.inventorygenerators.InventoryGenerators.MODID;

public record PacketChangeMode() implements CustomPacketPayload {


    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(MODID, "changemode");

    public static PacketChangeMode create(FriendlyByteBuf friendlyByteBuf) {
        return new PacketChangeMode();
    }

    public static PacketChangeMode create() {
        return new PacketChangeMode();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {}

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {

        ctx.workHandler().submitAsync(() -> {
            ctx.player().ifPresent(player -> {
                ItemStack stack = player.getItemInHand(player.getUsedItemHand());
                if (stack.getItem() instanceof IInventoryGenerator inventoryGenerator) {
                    inventoryGenerator.changeMode(stack, inventoryGenerator, player);
                }
            });
        });
    }
}
