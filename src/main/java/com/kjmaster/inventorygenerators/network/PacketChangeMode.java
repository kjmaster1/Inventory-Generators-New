package com.kjmaster.inventorygenerators.network;

import com.kjmaster.inventorygenerators.generators.IInventoryGenerator;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static com.kjmaster.inventorygenerators.InventoryGenerators.MODID;

public record PacketChangeMode(String empty) implements CustomPacketPayload {


    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(MODID, "changemode");
    public static final CustomPacketPayload.Type<PacketChangeMode> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, PacketChangeMode> CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, PacketChangeMode::empty, PacketChangeMode::new);

    public static PacketChangeMode create() {
        return new PacketChangeMode("");
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            if (player instanceof ServerPlayer serverPlayer) {
                ItemStack stack = serverPlayer.getItemInHand(serverPlayer.getUsedItemHand());
                if (stack.getItem() instanceof IInventoryGenerator inventoryGenerator) {
                    inventoryGenerator.changeMode(stack, inventoryGenerator, serverPlayer);
                }
            }
        });
    }
}
