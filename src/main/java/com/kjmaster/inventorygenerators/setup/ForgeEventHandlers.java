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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.PlayerInvWrapper;

public class ForgeEventHandlers {

    @SubscribeEvent
    public void onItemPickup(ItemEntityPickupEvent.Pre event) {
        Player player = event.getPlayer();
        if (player.level().isClientSide()) {
            return;
        }
        IItemHandlerModifiable inventory = CuriosIntegration.hasMod() ? CuriosIntegration.getFullInventory(player) : new PlayerInvWrapper(player.getInventory());
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            Item item = stack.getItem();
            if (item instanceof IInventoryGenerator inventoryGenerator) {
                IItemHandler inv = stack.getCapability(Capabilities.ItemHandler.ITEM);
                if (inv == null) {
                    return;
                }
                ItemStack autoPullStack = inv.getStackInSlot(2);
                if (!autoPullStack.isEmpty() && inventoryGenerator.isItemValid(stack, event.getItemEntity().getItem(), player.level())) {
                    ItemStack result = inv.insertItem(0, event.getItemEntity().getItem(), false);
                    int numPickedUp = event.getItemEntity().getItem().getCount() - result.getCount();
                    event.getItemEntity().setItem(result);
                    if (numPickedUp > 0) {
                        event.setCanPickup(TriState.FALSE);
                        if (!event.getItemEntity().isSilent()) {
                            double x = player.getX();
                            double y = player.getY();
                            double z = player.getZ();
                            event.getItemEntity().level().playSound(null, x, y, z, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F,
                                    ((event.getItemEntity().level().random.nextFloat() - event.getItemEntity().level().random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                        }
                        if (player instanceof ServerPlayer serverPlayer) {
                            serverPlayer.connection.send(new ClientboundTakeItemEntityPacket(event.getItemEntity().getId(), player.getId(), numPickedUp));
                        }
                    }
                }
            }
        }
    }
}
