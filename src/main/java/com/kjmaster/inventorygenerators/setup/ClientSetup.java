package com.kjmaster.inventorygenerators.setup;

import com.kjmaster.inventorygenerators.keys.KeyBindings;
import com.kjmaster.inventorygenerators.keys.KeyInputHandler;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientSetup {

    public static void init(FMLClientSetupEvent e) {
        MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
    }

    public static void registerKeybinds(RegisterKeyMappingsEvent event) {
        KeyBindings.init(event);
    }
}
