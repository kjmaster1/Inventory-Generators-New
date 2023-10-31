package com.kjmaster.inventorygenerators.setup;

import com.kjmaster.inventorygenerators.InventoryGenerators;
import com.kjmaster.inventorygenerators.network.PacketChangeMode;
import com.kjmaster.inventorygenerators.network.PacketSyncGeneratorEnergy;
import com.kjmaster.kjlib.network.ChannelBoundHandler;
import com.kjmaster.kjlib.network.PacketHandler;
import com.kjmaster.kjlib.network.PacketRequestDataFromServer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class InventoryGeneratorsBaseMessages {

    public static SimpleChannel INSTANCE;

    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void registerMessages(String name) {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(InventoryGenerators.MODID, name))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.registerMessage(id(), PacketRequestDataFromServer.class, PacketRequestDataFromServer::toBytes, PacketRequestDataFromServer::new, new ChannelBoundHandler<>(net, PacketRequestDataFromServer::handle));
        net.registerMessage(id(), PacketChangeMode.class, PacketChangeMode::toBytes, PacketChangeMode::new, new ChannelBoundHandler<>(net, PacketChangeMode::handle));
        net.registerMessage(id(), PacketSyncGeneratorEnergy.class, PacketSyncGeneratorEnergy::toBytes, PacketSyncGeneratorEnergy::new, PacketSyncGeneratorEnergy::handle);

        PacketHandler.registerStandardMessages(id(), net);
    }
}
