package com.kjmaster.inventorygenerators.setup;

import com.kjmaster.inventorygenerators.InventoryGenerators;
import com.kjmaster.inventorygenerators.network.PacketChangeMode;
import com.kjmaster.inventorygenerators.network.PacketSyncGeneratorEnergy;
import mcjty.lib.network.CustomPacketPayload;
import mcjty.lib.network.IPayloadRegistrar;
import mcjty.lib.network.Networking;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;

public class InventoryGeneratorsBaseMessages {

    private static IPayloadRegistrar registrar;

    public static void registerMessages() {

        registrar = Networking.registrar(InventoryGenerators.MODID)
                .versioned("1.0")
                .optional();

        registrar.play(PacketChangeMode.class, PacketChangeMode::create, handler -> handler.server(PacketChangeMode::handle));
        registrar.play(PacketSyncGeneratorEnergy.class, PacketSyncGeneratorEnergy::create, handler -> handler.client(PacketSyncGeneratorEnergy::handle));
    }

    public static <T extends CustomPacketPayload> void sendToPlayer(T packet, Player player) {
        registrar.getChannel().sendTo(packet, ((ServerPlayer) player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static <T extends CustomPacketPayload> void sendToServer(T packet) {
        if (Minecraft.getInstance().getConnection() == null) return;
        registrar.getChannel().sendToServer(packet);
    }
}
