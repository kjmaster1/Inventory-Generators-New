package com.kjmaster.inventorygenerators.generators;

import com.kjmaster.inventorygenerators.capabilities.EnergyStorageItemStack;
import com.kjmaster.inventorygenerators.capabilities.GeneratorCapabilityProvider;
import com.kjmaster.inventorygenerators.compat.curios.CuriosIntegration;
import com.kjmaster.inventorygenerators.network.PacketSyncGeneratorEnergy;
import com.kjmaster.inventorygenerators.recipe.GeneratorRecipe;
import com.kjmaster.inventorygenerators.recipe.GeneratorRecipeInput;
import com.kjmaster.inventorygenerators.setup.InventoryGeneratorsBaseMessages;
import com.kjmaster.inventorygenerators.setup.Registration;
import com.kjmaster.inventorygenerators.utils.StringHelper;
import mcjty.lib.items.BaseItem;
import mcjty.lib.varia.ComponentFactory;
import mcjty.lib.varia.IEnergyItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.kjmaster.inventorygenerators.setup.Config.doSendEnergy;
import static com.kjmaster.inventorygenerators.setup.Config.doSideEffects;

public abstract class InventoryGeneratorItem extends BaseItem implements IInventoryGenerator, IEnergyItem {

    final String generatorName;
    private RecipeManager.CachedCheck<GeneratorRecipeInput, ? extends GeneratorRecipe> quickCheck;

    public InventoryGeneratorItem(String generatorName) {
        super(new Item.Properties()
                .stacksTo(1));
        this.generatorName = generatorName;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level pLevel, List<Component> tooltip, TooltipFlag pIsAdvanced) {
        tooltip.add(Component.translatable("info.invgens." + generatorName).withStyle(ChatFormatting.BOLD, ChatFormatting.GOLD));

        addOnOffTooltips(stack, tooltip);
        addModeTooltips(stack, tooltip);
        addEnergyAndBurnTimeTooltips(stack, tooltip);
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack itemStack, @Nullable CompoundTag nbt) {
        ItemStackHandler stackHandler = new ItemStackHandler(5);
        EnergyStorageItemStack energyStorageItemStack = new EnergyStorageItemStack(((InventoryGeneratorItem) itemStack.getItem()).getMaxEnergyStored(itemStack), itemStack);
        return new GeneratorCapabilityProvider(itemStack, stackHandler, energyStorageItemStack);
    }

    private void addOnOffTooltips(ItemStack stack, List<Component> tooltip) {
        if (isOn(stack)) {
            tooltip.add(StringHelper.getNoticeText("info.invgens.1"));
            tooltip.add(StringHelper.getDeactivationText("info.invgens.2"));
        } else {
            tooltip.add(StringHelper.getActivationText("info.invgens.3"));
        }
    }

    private void addModeTooltips(ItemStack stack, List<Component> tooltip) {
        tooltip.add(isInChargingMode(stack)
                ? StringHelper.getNoticeText("info.invgens.modeOn")
                : StringHelper.getNoticeText("info.invgens.modeOff"));
    }

    private void addEnergyAndBurnTimeTooltips(ItemStack stack, List<Component> tooltip) {
        tooltip.add(createEnergyTooltip(stack));
        tooltip.add(createBurnTimeTooltip(stack));
    }

    private MutableComponent createEnergyTooltip(ItemStack stack) {
        int stored = getInternalEnergyStored(stack);
        int max = getMaxEnergyStored(stack);
        return Component.literal(String.format("%s: %s / %s RF",
                StringHelper.localize("info.invgens.charge"),
                StringHelper.getScaledNumber(stored),
                StringHelper.getScaledNumber(max)));
    }

    private MutableComponent createBurnTimeTooltip(ItemStack stack) {
        int burnTime = getBurnTime(stack);
        return Component.literal(String.format("%s: %s",
                StringHelper.localize("info.invgens.burnTimeLeft"),
                StringHelper.format(burnTime)));
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(stack, level, entity, pSlotId, pIsSelected);

        if (!(entity instanceof Player player) || level.isClientSide) {
            return;
        }

        giveNBT(stack);

        if (getBurnTime(stack) < 0) {
            setBurnTime(stack, 0);
        }

        if (getBurnTime(stack) == 0) {
            setCurrentFuel(stack, ItemStack.EMPTY);
        }

        LazyOptional<IItemHandler> lazyOptional = stack.getCapability(ForgeCapabilities.ITEM_HANDLER);

        lazyOptional.ifPresent(itemHandler -> {
            ItemStack speedUpgradeStack = itemHandler.getStackInSlot(1);
            int numSpeedUpgrades = speedUpgradeStack.getCount();
            if (isOn(stack)) {
                handleFuel(stack, itemHandler, level);
                for (int i = 0; i <= numSpeedUpgrades; i++) {
                    handleEnergy(stack, player);
                }

                if (shouldGiveSideEffect(itemHandler, stack)) {
                    giveSideEffect(player);
                }
            }
            if (isInChargingMode(stack) && doSendEnergy) {
                handleCharging(stack, numSpeedUpgrades, player);
            }
        });
    }

    private boolean shouldGiveSideEffect(IItemHandler inv, ItemStack stack) {
        return inv.getStackInSlot(3).isEmpty() && hasSideEffect() && doSideEffects && getBurnTime(stack) > 0 && getInternalEnergyStored(stack) < getMaxEnergyStored(stack);
    }

    private void handleFuel(ItemStack stack, IItemHandler inv, Level level) {
        if (getBurnTime(stack) <= 0 && !getFuelSlotStack(stack, level).isEmpty()
                && getInternalEnergyStored(stack) < getMaxEnergyStored(stack)) {
            ItemStack fuel = getFuelSlotStack(stack, level);
            setBurnTime(stack, calculateTime(stack, fuel, level));
            setCurrentFuel(stack, fuel);
            consumeFuel(fuel, inv);
        }
    }

    private void consumeFuel(ItemStack fuel, IItemHandler inv) {
        if (inv instanceof IItemHandlerModifiable componentItemHandler) {
            if (fuel.getItem() instanceof PotionItem && fuel.getCount() == 1) {
                componentItemHandler.setStackInSlot(0, new ItemStack(Items.GLASS_BOTTLE));
            } else if (fuel.getItem() == Items.LAVA_BUCKET || fuel.getItem() == Items.WATER_BUCKET || fuel.getItem() == Items.POWDER_SNOW_BUCKET) {
                componentItemHandler.setStackInSlot(0, new ItemStack(Items.BUCKET));
            } else {
                ItemStack fuelShrink = fuel.copy();
                fuelShrink.shrink(1);
                componentItemHandler.setStackInSlot(0, fuelShrink);
            }
        }
    }

    private void handleEnergy(ItemStack stack, Player player) {
        if (getInternalEnergyStored(stack) < getMaxEnergyStored(stack) && getBurnTime(stack) > 0) {
            setBurnTime(stack, getBurnTime(stack) - 1);
            int rfToGive = calculatePower(stack, player.level());
            receiveInternalEnergy(stack, rfToGive);
            sendSyncPacket(stack, player);
        }
    }

    private void handleCharging(ItemStack stack, int numSpeedUpgrades, Player player) {
        for (int i = 0; i <= numSpeedUpgrades; i++) {
            ArrayList<ItemStack> chargeables = getChargeables(player);
            giveEnergyToChargeables(chargeables, stack);
            sendSyncPacket(stack, player);
        }
    }

    protected void openGui(Player player, String key, ItemStack stack) {

        if (player instanceof ServerPlayer serverPlayer) {

            serverPlayer.openMenu(new MenuProvider() {
                @Nonnull
                @Override
                public Component getDisplayName() {
                    return ComponentFactory.translatable(key);
                }

                @Override
                public AbstractContainerMenu createMenu(int id, @Nonnull Inventory playerInventory, @Nonnull Player player) {
                    InventoryGeneratorContainer container = new InventoryGeneratorContainer(id, player.blockPosition(), player, stack);
                    container.setupInventories(new InventoryGeneratorItemHandler(stack), playerInventory);
                    return container;
                }
            });
        }
        sendSyncPacket(stack, player);
    }

    protected void sendSyncPacket(@NotNull ItemStack stack, Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            InventoryGeneratorsBaseMessages.sendToPlayer(PacketSyncGeneratorEnergy.create(getInternalEnergyStored(stack)), serverPlayer);
        }
    }

    public void giveSideEffect(Player player) {
    }

    public boolean hasSideEffect() {
        return false;
    }

    @Override
    public void giveNBT(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            tag = new CompoundTag();
        }
        if (!tag.contains("energy")) {
            tag.putInt("energy", 0);
        }
        if (!tag.contains("on")) {
            tag.putBoolean("on", false);
        }
        if (!tag.contains("charging")) {
            tag.putBoolean("charging", false);
        }
        if (!tag.contains("burnTime")) {
            tag.putInt("burnTime", 0);
        }
        stack.setTag(tag);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        if (Objects.requireNonNull(pPlayer).isCrouching()) {
            turnOn(pPlayer.getItemInHand(pUsedHand));
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, pPlayer.getItemInHand(pUsedHand));
        }
        if (!pPlayer.isCrouching() && !pLevel.isClientSide() && pUsedHand.equals(InteractionHand.MAIN_HAND)) {
            openGui(pPlayer, "item.inventorygenerators." + generatorName, pPlayer.getItemInHand(pUsedHand));
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, pPlayer.getItemInHand(pUsedHand));
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {

        if (Objects.requireNonNull(context.getPlayer()).isCrouching()) {
            turnOn(stack);
            return InteractionResult.SUCCESS;
        }
        return super.onItemUseFirst(stack, context);
    }

    @Override
    public ArrayList<ItemStack> getChargeables(Player player) {
        ArrayList<ItemStack> chargeables = new ArrayList<>();
        IItemHandlerModifiable inventory = CuriosIntegration.hasMod() ? CuriosIntegration.getFullInventory(player) : new PlayerInvWrapper(player.getInventory());
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack invStack = inventory.getStackInSlot(i);
            LazyOptional<IEnergyStorage> lazyOptional = invStack.getCapability(ForgeCapabilities.ENERGY);
            lazyOptional.ifPresent(energy -> {
                if (energy.canReceive() && (energy.getEnergyStored() < energy.getMaxEnergyStored())) {
                    chargeables.add(invStack);
                }
            });
        }
        return chargeables;
    }

    @Override
    public void giveEnergyToChargeables(ArrayList<ItemStack> chargeables, ItemStack stack) {
        for (ItemStack chargeableStack : chargeables) {
            LazyOptional<IEnergyStorage> lazyOptional = chargeableStack.getCapability(ForgeCapabilities.ENERGY);
            lazyOptional.ifPresent(energy -> {
                int energySent = energy.receiveEnergy(getInternalEnergyStored(stack) / chargeables.size(), false);
                extractEnergy(stack, energySent, false);
            });
        }
        chargeables.clear();
    }

    @Override
    public void receiveInternalEnergy(ItemStack stack, int energy) {
        if (stack.getTag() != null) {
            stack.getTag().putInt("energy", getInternalEnergyStored(stack) + energy);
        }
    }

    @Override
    public int getInternalEnergyStored(ItemStack stack) {
        if (stack.getTag() != null) {
            if (stack.getTag().contains("energy")) {
                return stack.getTag().getInt("energy");
            }
        }
        return 0;
    }

    @Override
    public boolean isItemValid(ItemStack generator, ItemStack fuel, Level level) {
        return getRecipeHolder(generator, fuel, level).isPresent();
    }

    @Override
    public int calculateTime(ItemStack generator, ItemStack fuel, Level level) {
        return getCalculatedValue(generator, fuel, level, 0);
    }

    @Override
    public int calculatePower(ItemStack generator, Level level) {
        int minSend = getCalculatedValue(generator, getCurrentFuel(generator), level, 1);
        int calculated = Math.min(getMaxEnergyStored(generator) - getInternalEnergyStored(generator), minSend);
        return calculated;
    }

    private int getCalculatedValue(ItemStack generator, ItemStack fuel, Level level, int valueIndex) {
        Optional<? extends GeneratorRecipe> recipeHolder = getRecipeHolder(generator, fuel, level);
        if (recipeHolder.isPresent()) {
            GeneratorRecipe generatorRecipe = recipeHolder.get();
            if (valueIndex == 0) {
                return generatorRecipe.burnTime();
            } else if (valueIndex == 1) {
                return generatorRecipe.RF();
            }
        }
        return 0;
    }

    private Optional<? extends GeneratorRecipe> getRecipeHolder(ItemStack generator, ItemStack fuel, Level level) {
        if (quickCheck == null) {
            RecipeType<GeneratorRecipe> generatorRecipeRecipeType = (RecipeType<GeneratorRecipe>) Registration.GENERATOR_RECIPE_TYPE.get();
            this.quickCheck = RecipeManager.createCheck(generatorRecipeRecipeType);
        }
        return quickCheck.getRecipeFor(new GeneratorRecipeInput(generator, fuel), level);
    }

    @Override
    public boolean isInChargingMode(ItemStack stack) {
        if (stack.getTag() != null) {
            if (stack.getTag().contains("charging")) {
                return stack.getTag().getBoolean("charging");
            }
        }
        return false;
    }

    @Override
    public void changeMode(ItemStack stack, IInventoryGenerator inventoryGenerator, Player player) {
        if (stack.getTag() != null) {
            if (stack.getTag().contains("charging")) {
                boolean isCharging = stack.getTag().getBoolean("charging");
                stack.getTag().putBoolean("charging", !isCharging);
            }
        } else {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putBoolean("charging", true);
            stack.setTag(compoundTag);
        }
    }

    @Override
    public boolean isOn(ItemStack stack) {
        if (stack.getTag() != null) {
            if (stack.getTag().contains("on")) {
                return stack.getTag().getBoolean("on");
            }
        }
        return false;
    }

    @Override
    public void turnOn(ItemStack stack) {
        if (stack.getTag() != null) {
            if (stack.getTag().contains("on")) {
                boolean isOn = stack.getTag().getBoolean("on");
                stack.getTag().putBoolean("on", !isOn);
            }
        } else {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putBoolean("on", true);
            stack.setTag(compoundTag);
        }
    }

    @Override
    public int getBurnTime(ItemStack stack) {
        if (stack.getTag() != null) {
            if (stack.getTag().contains("burnTime")) {
                return stack.getTag().getInt("burnTime");
            }
        }
        return 0;
    }

    @Override
    public void setBurnTime(ItemStack stack, int burnTime) {
        if (stack.getTag() != null) {
            if (stack.getTag().contains("burnTime")) {
                stack.getTag().putInt("burnTime", burnTime);
            }
        } else {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putInt("burnTime", burnTime);
            stack.setTag(compoundTag);
        }
    }

    public ItemStack getCurrentFuel(ItemStack stack) {
        return stack.getCapability(ForgeCapabilities.ITEM_HANDLER)
                .map(iItemHandler -> iItemHandler.getStackInSlot(4).copy())
                .orElse(ItemStack.EMPTY);
    }

    public void setCurrentFuel(ItemStack stack, ItemStack fuel) {
        LazyOptional<IItemHandler> lazyOptional = stack.getCapability(ForgeCapabilities.ITEM_HANDLER);
        lazyOptional.ifPresent(iItemHandler -> {
            if (iItemHandler instanceof IItemHandlerModifiable modifiable) {
                modifiable.setStackInSlot(4, fuel);
            }
        });
    }

    @Override
    public ItemStack getFuelSlotStack(ItemStack stack, Level level) {
        return stack.getCapability(ForgeCapabilities.ITEM_HANDLER)
                .map(iItemHandler -> {
                    ItemStack fuel = iItemHandler.getStackInSlot(0).copy();
                    return isItemValid(stack, fuel, level) ? fuel : ItemStack.EMPTY;
                })
                .orElse(ItemStack.EMPTY);
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return getInternalEnergyStored(stack) < getMaxEnergyStored(stack);
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        return (int) (13.0F * ((float) getInternalEnergyStored(stack) / (float) getMaxEnergyStored(stack)));
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return 0x00FF00; // Green color
    }

    @Override
    public long receiveEnergyL(ItemStack itemStack, long l, boolean b) {
        return 0;
    }

    @Override
    public long extractEnergyL(ItemStack itemStack, long l, boolean b) {
        return this.extractEnergy(itemStack, (int) l, b);
    }

    public int getMaxEnergyStored(ItemStack container) {
        return 0;
    }


    public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored(ItemStack container) {
        return this.getInternalEnergyStored(container);
    }

    @Override
    public long getEnergyStoredL(ItemStack itemStack) {
        return this.getEnergyStored(itemStack);
    }

    @Override
    public long getMaxEnergyStoredL(ItemStack itemStack) {
        return this.getMaxEnergyStored(itemStack);
    }

    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
        int energyExtracted = Math.min(this.getInternalEnergyStored(container), Math.min(this.getMaxEnergyStored(container), maxExtract));
        if (!simulate) {
            int stored = this.getInternalEnergyStored(container);
            stored -= energyExtracted;
            CompoundTag tag = container.getTag();
            if (tag != null) {
                tag.putInt("energy", stored);
            } else {
                tag = new CompoundTag();
                tag.putInt("energy", stored);
                container.setTag(tag);
            }
        }
        return energyExtracted;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && (slotChanged
                || getInternalEnergyStored(oldStack) > 0 != getInternalEnergyStored(newStack) > 0);
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public boolean isFoil(@NotNull ItemStack pStack) {
        super.isFoil(pStack);
        return isInChargingMode(pStack);
    }
}
