package com.kjmaster.inventorygenerators.upgrades;

import mcjty.lib.items.BaseItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AutoPullUpgradeItem extends BaseItem {

    public AutoPullUpgradeItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> tooltip, TooltipFlag pIsAdvanced) {
        tooltip.add(Component.translatable("info.invgens.auto_pull_upgrade").withStyle(ChatFormatting.BOLD, ChatFormatting.GOLD));
    }
}
