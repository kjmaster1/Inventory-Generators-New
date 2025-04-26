package com.kjmaster.inventorygenerators.keys;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;

public class KeyBindings {

    public static KeyMapping changeMode;

    public static void init(RegisterKeyMappingsEvent event) {
        changeMode = new KeyMapping("key.inventorygenerators.changeMode", KeyConflictContext.GUI, InputConstants.getKey("key.keyboard.m"), "key.categories.inventorygenerators");
        event.register(changeMode);
    }
}
