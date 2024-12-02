package com.kjmaster.inventorygenerators.setup;

import com.kjmaster.inventorygenerators.keys.KeyBindings;
import com.kjmaster.inventorygenerators.keys.KeyInputHandler;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;

public class ClientSetup {

    public static void init(FMLClientSetupEvent e) {
        NeoForge.EVENT_BUS.register(new KeyInputHandler());
    }

    public static void registerKeybinds(RegisterKeyMappingsEvent event) {
        KeyBindings.init(event);
    }
}
