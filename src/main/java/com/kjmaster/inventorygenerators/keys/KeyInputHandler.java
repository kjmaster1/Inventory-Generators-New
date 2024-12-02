package com.kjmaster.inventorygenerators.keys;

import com.kjmaster.inventorygenerators.network.PacketChangeMode;
import com.kjmaster.inventorygenerators.setup.InventoryGeneratorsBaseMessages;
import com.mojang.blaze3d.platform.InputConstants;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.InputEvent;


public class KeyInputHandler {

    @SubscribeEvent
    public void onKeyInput(InputEvent.Key key) {
        if (key.getAction() == InputConstants.PRESS && (key.getKey() == KeyBindings.changeMode.getKey().getValue())) {
            InventoryGeneratorsBaseMessages.sendToServer(PacketChangeMode.create());
        }
    }
}
