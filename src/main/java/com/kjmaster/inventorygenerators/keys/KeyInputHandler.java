package com.kjmaster.inventorygenerators.keys;

import com.kjmaster.inventorygenerators.network.PacketChangeMode;
import com.kjmaster.inventorygenerators.setup.InventoryGeneratorsBaseMessages;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;


public class KeyInputHandler {

    @SubscribeEvent
    public void onKeyInput(InputEvent.Key key) {
        if (KeyBindings.changeMode.consumeClick()) {
            PacketChangeMode packet = new PacketChangeMode(new FriendlyByteBuf(Unpooled.buffer()));
            InventoryGeneratorsBaseMessages.INSTANCE.sendToServer(packet);
        }
    }
}
