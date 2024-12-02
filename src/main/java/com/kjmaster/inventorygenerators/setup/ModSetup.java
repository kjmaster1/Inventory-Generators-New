package com.kjmaster.inventorygenerators.setup;

import mcjty.lib.setup.DefaultModSetup;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

public class ModSetup extends DefaultModSetup {

    @Override
    public void init(FMLCommonSetupEvent e) {
        super.init(e);
        NeoForge.EVENT_BUS.register(new ForgeEventHandlers());
    }

    @Override
    protected void setupModCompat() {

    }
}
