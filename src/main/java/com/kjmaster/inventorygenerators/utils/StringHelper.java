package com.kjmaster.inventorygenerators.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import static com.kjmaster.inventorygenerators.InventoryGenerators.MODID;

/**
 * Contains various helper functions to assist with String manipulation.
 *
 * @author King Lemming
 * <p>
 * This class is used in KJLib under the 'CoFH "Don't Be a Jerk" License' which can be found here https://goo.gl/QsbhBa
 * The license states 'You CAN... Copy portions of this code for use in other projects.'
 */

public final class StringHelper {

    private StringHelper() {

    }

    public static final DecimalFormat DF0 = new DecimalFormat("#");
    public static final DecimalFormat DF2 = new DecimalFormat("#.##");

    public static String titleCase(String input) {

        return input.substring(0, 1).toUpperCase(Locale.ROOT) + input.substring(1);
    }

    public static String localize(String key) {

        return I18n.get(key);
    }

    public static MutableComponent getNoticeText(String key) {
        MutableComponent mutableComponent = getTextComponent(key);
        mutableComponent = mutableComponent.setStyle(Style.EMPTY.withColor(TextColor.parseColor("#fc9d03").getOrThrow()));
        return mutableComponent;
    }

    public static MutableComponent getDeactivationText(String key) {
        MutableComponent mutableComponent = getTextComponent(key);
        return mutableComponent.withStyle(ChatFormatting.YELLOW);
    }

    public static MutableComponent getActivationText(String key) {
        MutableComponent mutableComponent = getTextComponent(key);
        mutableComponent = mutableComponent.setStyle(Style.EMPTY.withColor(TextColor.parseColor("#03a1fc").getOrThrow()));
        return mutableComponent;
    }

    public static String localize(String key, Object... format) {

        return I18n.get(key, format);
    }

    public static boolean canLocalize(String key) {

        return I18n.exists(key);
    }

    public static String format(long number) {

        return StringUtils.normalizeSpace(NumberFormat.getInstance().format(number));
    }

    public static MutableComponent getFluidName(FluidStack stack) {

        Fluid fluid = stack.getFluid();
        MutableComponent name = fluid.getFluidType().getDescription(stack).copy();

        switch (fluid.getFluidType().getRarity(stack)) {
            case UNCOMMON -> name.withStyle(ChatFormatting.YELLOW);
            case RARE -> name.withStyle(ChatFormatting.AQUA);
            case EPIC -> name.withStyle(ChatFormatting.LIGHT_PURPLE);
        }
        return name;
    }

    public static MutableComponent getItemName(ItemStack stack) {

        Item item = stack.getItem();
        MutableComponent name = item.getName(stack).copy();

        switch (stack.getRarity()) {
            case UNCOMMON -> name.withStyle(ChatFormatting.YELLOW);
            case RARE -> name.withStyle(ChatFormatting.AQUA);
            case EPIC -> name.withStyle(ChatFormatting.LIGHT_PURPLE);
        }
        return name;
    }

    public static String getScaledNumber(long number) {

        if (number >= 1_000_000_000) {
            return number / 1_000_000_000 + "." + (number % 1_000_000_000 / 100_000_000) + (number % 100_000_000 / 10_000_000) + "G";
        } else if (number >= 1_000_000) {
            return number / 1_000_000 + "." + (number % 1_000_000 / 100_000) + (number % 100_000 / 10_000) + "M";
        } else if (number >= 1000) {
            return number / 1000 + "." + (number % 1000 / 100) + (number % 100 / 10) + "k";
        } else {
            return String.valueOf(number);
        }
    }

    public static String toJSON(Component chatComponent, HolderLookup.Provider registries) {

        return Component.Serializer.toJson(chatComponent, registries);
    }

    public static MutableComponent fromJSON(String string, HolderLookup.Provider registries) {

        return Component.Serializer.fromJsonLenient(string, registries);
    }

    public static MutableComponent getEmptyLine() {

        return Component.literal("");
    }

    public static MutableComponent getTextComponent(String key) {

        return canLocalize(key) ? Component.translatable(key) : Component.literal(key);
    }

    public static MutableComponent getInfoTextComponent(String key) {

        return getTextComponent(key).withStyle(ChatFormatting.GOLD);
    }

    public static MutableComponent getKeywordTextComponent(String key) {

        return getTextComponent(key).withStyle(Style.EMPTY.withFont(ResourceLocation.fromNamespaceAndPath(MODID, "invis")));
    }

    public static MutableComponent getRomanNumeral(int number) {

        return Component.translatable("enchantment.level." + number);
    }
    // endregion

    // region RESOURCE LOCATION
    public static String[] decompose(String resourceLoc, char delimiter) {

        return decompose("minecraft", resourceLoc, delimiter);
    }

    public static String[] decompose(String modid, String resourceLoc, char delimiter) {

        String[] decomposed = new String[]{modid, resourceLoc};
        int delIndex = resourceLoc.indexOf(delimiter);
        if (delIndex >= 0) {
            decomposed[1] = resourceLoc.substring(delIndex + 1);
            if (delIndex >= 1) {
                decomposed[0] = resourceLoc.substring(0, delIndex);
            }
        }
        return decomposed;
    }

    public static String namespace(String resourceLoc) {

        return decompose(resourceLoc, ':')[0];
    }

    public static String path(String resourceLoc) {

        return decompose(resourceLoc, ':')[1];
    }
    // endregion
}
