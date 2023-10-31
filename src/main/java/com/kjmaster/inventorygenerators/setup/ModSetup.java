package com.kjmaster.inventorygenerators.setup;

import com.kjmaster.inventorygenerators.data.InventoryGeneratorManager;
import com.kjmaster.kjlib.setup.DefaultModSetup;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ModSetup extends DefaultModSetup {

    @Override
    public void init(FMLCommonSetupEvent e) {
        super.init(e);
        MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());
        InventoryGeneratorsBaseMessages.registerMessages("inventorygenerators");
    }

    @Override
    protected void setupModCompat() {

    }

    public void reload(final AddReloadListenerEvent evt) {
        evt.addListener(InventoryGeneratorManager.INSTANCE);
    }
}
