package com.kjmaster.inventorygenerators.upgrades;

import com.kjmaster.kjlib.items.BaseItem;
import com.kjmaster.kjlib.util.helpers.StringHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NoEffectUpgradeItem extends BaseItem {

    public NoEffectUpgradeItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    protected void tooltipDelegate(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(Component.translatable("info.invgens.no_effect_upgrade").withStyle(ChatFormatting.BOLD, ChatFormatting.GOLD));
    }
}
