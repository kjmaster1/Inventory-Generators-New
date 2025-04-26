package com.kjmaster.inventorygenerators.setup;

import com.kjmaster.inventorygenerators.compat.curios.CuriosIntegration;
import com.kjmaster.inventorygenerators.generators.IInventoryGenerator;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;

public class ForgeEventHandlers {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onItemPickup(EntityItemPickupEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) {
            return;
        }
        IItemHandlerModifiable inventory = CuriosIntegration.hasMod() ? CuriosIntegration.getFullInventory(player) : new PlayerInvWrapper(player.getInventory());
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            Item item = stack.getItem();
            if (item instanceof IInventoryGenerator inventoryGenerator) {
                LazyOptional<IItemHandler> itemHandlerLazyOptional = stack.getCapability(ForgeCapabilities.ITEM_HANDLER);
                itemHandlerLazyOptional.ifPresent(inv -> {
                    ItemStack autoPullStack = inv.getStackInSlot(2);
                    if (!autoPullStack.isEmpty() && inventoryGenerator.isItemValid(stack, event.getItem().getItem(), player.level())) {
                        ItemStack result = inv.insertItem(0, event.getItem().getItem(), false);
                        int numPickedUp = event.getItem().getItem().getCount() - result.getCount();
                        event.getItem().setItem(result);
                        if (numPickedUp > 0) {
                            event.setCanceled(true);
                            if (!event.getItem().isSilent()) {
                                double x = player.getX();
                                double y = player.getY();
                                double z = player.getZ();
                                event.getItem().level().playSound(null, x, y, z, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F,
                                        ((event.getItem().level().random.nextFloat() - event.getItem().level().random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                            }
                            if (player instanceof ServerPlayer serverPlayer) {
                                serverPlayer.connection.send(new ClientboundTakeItemEntityPacket(event.getItem().getId(), player.getId(), numPickedUp));
                            }
                        }
                    }
                });
            }
        }
    }
}
