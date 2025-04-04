package com.kjmaster.inventorygenerators.setup;

import com.kjmaster.inventorygenerators.InventoryGenerators;
import com.kjmaster.inventorygenerators.network.PacketChangeMode;
import com.kjmaster.inventorygenerators.network.PacketSyncGeneratorEnergy;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class InventoryGeneratorsBaseMessages {

    public static void registerMessages(RegisterPayloadHandlersEvent event) {

        final PayloadRegistrar registrar = event.registrar(InventoryGenerators.MODID)
                .versioned("1.0")
                .optional();
        registrar.playToServer(PacketChangeMode.TYPE, PacketChangeMode.CODEC, PacketChangeMode::handle);
        registrar.playToClient(PacketSyncGeneratorEnergy.TYPE, PacketSyncGeneratorEnergy.CODEC, PacketSyncGeneratorEnergy::handle);
    }

    public static <T extends CustomPacketPayload> void sendToPlayer(T packet, Player player) {
        PacketDistributor.sendToPlayer((ServerPlayer) player, packet);
    }

    public static <T extends CustomPacketPayload> void sendToServer(T packet) {
        if (Minecraft.getInstance().getConnection() == null) return;
        PacketDistributor.sendToServer(packet);
    }
}
