package com.kjmaster.inventorygenerators.upgrades;

import mcjty.lib.items.BaseItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class SpeedUpgradeItem extends BaseItem {
    public SpeedUpgradeItem() {
        super(new Item.Properties().stacksTo(3));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        tooltip.add(Component.translatable("info.invgens.speed_upgrade").withStyle(ChatFormatting.BOLD, ChatFormatting.GOLD));
    }

}
