package com.kjmaster.inventorygenerators.generators;

import com.kjmaster.inventorygenerators.InventoryGenerators;
import com.kjmaster.inventorygenerators.curios.CuriosIntegration;
import com.kjmaster.inventorygenerators.network.PacketSyncGeneratorEnergy;
import com.kjmaster.inventorygenerators.recipe.GeneratorRecipe;
import com.kjmaster.inventorygenerators.recipe.GeneratorRecipeInput;
import com.kjmaster.inventorygenerators.setup.InvGensDataComponents;
import com.kjmaster.inventorygenerators.setup.InventoryGeneratorsBaseMessages;
import com.kjmaster.inventorygenerators.setup.Registration;
import com.kjmaster.inventorygenerators.utils.StringHelper;
import mcjty.lib.items.BaseItem;
import mcjty.lib.varia.ComponentFactory;
import mcjty.lib.varia.IEnergyItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.ComponentItemHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.PlayerInvWrapper;
import org.jetbrains.annotations.NotNull;

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

    public static void initOverrides(InventoryGeneratorItem item) {
        ItemProperties.register(item, ResourceLocation.fromNamespaceAndPath(InventoryGenerators.MODID, "on"), (stack, worldIn, entityIn, integer) -> {
            if (stack.getItem() instanceof IInventoryGenerator inventoryGenerator) {
                return inventoryGenerator.isOn(stack) && (inventoryGenerator.getBurnTime(stack) > 0) ? 1 : 0;
            }
            return 0;
        });
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(Component.translatable("info.invgens." + generatorName).withStyle(ChatFormatting.BOLD, ChatFormatting.GOLD));

        addOnOffTooltips(stack, tooltip);
        addModeTooltips(stack, tooltip);
        addEnergyAndBurnTimeTooltips(stack, tooltip);
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
        if (isInChargingMode(stack)) {
            tooltip.add(StringHelper.getNoticeText("info.invgens.modeOn"));
        } else {
            tooltip.add(StringHelper.getNoticeText("info.invgens.modeOff"));
        }
    }

    private void addEnergyAndBurnTimeTooltips(ItemStack stack, List<Component> tooltip) {
        MutableComponent energyComponent = Component.literal(StringHelper.localize("info.invgens.charge") + ": " + StringHelper.getScaledNumber(getInternalEnergyStored(stack)))
                .append(" / " + StringHelper.getScaledNumber(getMaxEnergyStored(stack)) + " RF");
        tooltip.add(energyComponent);

        MutableComponent burnTimeComponent = Component.literal(StringHelper.localize("info.invgens.burnTimeLeft") + ": " + StringHelper.format(getBurnTime(stack)));
        tooltip.add(burnTimeComponent);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level pLevel, @NotNull Entity entity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(stack, pLevel, entity, pSlotId, pIsSelected);

        if (entity instanceof Player player && !pLevel.isClientSide) {

            giveDataComponents(stack);

            if (getBurnTime(stack) < 0) {
                setBurnTime(stack, 0);
            }

            if (getBurnTime(stack) == 0) {
                setCurrentFuel(stack, ItemStack.EMPTY);
            }

            var itemHandler = stack.getCapability(Capabilities.ItemHandler.ITEM);

            if (itemHandler != null) {
                ItemStack speedUpgradeStack = itemHandler.getStackInSlot(1);
                int numSpeedUpgrades = speedUpgradeStack.getCount();
                if (isOn(stack)) {
                    handleFuel(stack, itemHandler, pLevel);
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
            }
        }
    }

    private boolean shouldGiveSideEffect(IItemHandler inv, ItemStack stack) {
        return inv.getStackInSlot(3).isEmpty() && hasSideEffect() && doSideEffects && getBurnTime(stack) > 0 && getInternalEnergyStored(stack) < getMaxEnergyStored(stack);
    }

    private void handleFuel(ItemStack stack, IItemHandler inv, Level level) {
        if (getBurnTime(stack) <= 0 && !getFuel(stack, level).isEmpty()
                && getInternalEnergyStored(stack) < getMaxEnergyStored(stack)) {
            ItemStack fuel = getFuel(stack, level);
            setBurnTime(stack, calculateTime(stack, fuel, level));
            setCurrentFuel(stack, fuel);
            consumeFuel(fuel, inv);
        }
    }

    private void consumeFuel(ItemStack fuel, IItemHandler inv) {
        if (inv instanceof ComponentItemHandler componentItemHandler) {
            if (fuel.getItem() instanceof PotionItem && fuel.getCount() == 1) {
                componentItemHandler.setStackInSlot(0, new ItemStack(Items.GLASS_BOTTLE));
            } else if (fuel.getItem() == Items.LAVA_BUCKET || fuel.getItem() == Items.WATER_BUCKET || fuel.getItem() == Items.POWDER_SNOW_BUCKET) {
                componentItemHandler.setStackInSlot(0, new ItemStack(Items.BUCKET));
            } else {
                fuel.shrink(1);
                componentItemHandler.setStackInSlot(0, fuel);
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
    public void giveDataComponents(ItemStack stack) {
        stack.getOrDefault(InvGensDataComponents.FORGE_ENERGY, 0);
        stack.getOrDefault(InvGensDataComponents.GENERATOR_ON, false);
        stack.getOrDefault(InvGensDataComponents.GENERATOR_CHARGING, false);
        stack.getOrDefault(InvGensDataComponents.GENERATOR_BURN_TIME, 0);
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
            var energy = invStack.getCapability(Capabilities.EnergyStorage.ITEM);
            if (energy != null) {
                if (energy.canReceive() && (energy.getEnergyStored() < energy.getMaxEnergyStored())) {
                    chargeables.add(invStack);
                }
            }
        }
        return chargeables;
    }

    @Override
    public void giveEnergyToChargeables(ArrayList<ItemStack> chargeables, ItemStack stack) {
        for (ItemStack chargeableStack : chargeables) {
            var energy = chargeableStack.getCapability(Capabilities.EnergyStorage.ITEM);
            if (energy != null) {
                int energySent = energy.receiveEnergy(getInternalEnergyStored(stack) / chargeables.size(), false);
                extractEnergy(stack, energySent, false);
            }
        }
        chargeables.clear();
    }

    @Override
    public void receiveInternalEnergy(ItemStack stack, int energy) {
        stack.set(InvGensDataComponents.FORGE_ENERGY, getInternalEnergyStored(stack) + energy);
    }

    @Override
    public int getInternalEnergyStored(ItemStack stack) {
        return stack.getOrDefault(InvGensDataComponents.FORGE_ENERGY, 0);
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
        return Math.min(getMaxEnergyStored(generator) - getInternalEnergyStored(generator), minSend);
    }

    private int getCalculatedValue(ItemStack generator, ItemStack fuel, Level level, int valueIndex) {
        Optional<? extends RecipeHolder<? extends GeneratorRecipe>> recipeHolder = getRecipeHolder(generator, fuel, level);
        if (recipeHolder.isPresent()) {
            GeneratorRecipe generatorRecipe = recipeHolder.get().value();
            if (valueIndex == 0) {
                return generatorRecipe.burnTime();
            } else if (valueIndex == 1) {
                return generatorRecipe.RF();
            }
        }
        return 0;
    }

    private Optional<? extends RecipeHolder<? extends GeneratorRecipe>> getRecipeHolder(ItemStack generator, ItemStack fuel, Level level) {
        if (quickCheck == null) {
            RecipeType<GeneratorRecipe> generatorRecipeRecipeType = (RecipeType<GeneratorRecipe>) Registration.GENERATOR_RECIPE_TYPE.get();
            this.quickCheck = RecipeManager.createCheck(generatorRecipeRecipeType);
        }
        return quickCheck.getRecipeFor(new GeneratorRecipeInput(generator, fuel), level);
    }

    @Override
    public boolean isInChargingMode(ItemStack stack) {
        return stack.getOrDefault(InvGensDataComponents.GENERATOR_CHARGING, false);
    }

    @Override
    public void changeMode(ItemStack stack, IInventoryGenerator inventoryGenerator, Player player) {
        boolean isCharging = stack.getOrDefault(InvGensDataComponents.GENERATOR_CHARGING, false);
        if (isCharging) {
            stack.set(InvGensDataComponents.GENERATOR_CHARGING, false);
        } else {
            stack.set(InvGensDataComponents.GENERATOR_CHARGING, true);
        }
    }

    @Override
    public boolean isOn(ItemStack stack) {
        return stack.getOrDefault(InvGensDataComponents.GENERATOR_ON, false);
    }

    @Override
    public void turnOn(ItemStack stack) {
        boolean isOn = stack.getOrDefault(InvGensDataComponents.GENERATOR_ON, false);
        if (isOn) {
            stack.set(InvGensDataComponents.GENERATOR_ON, false);
        } else {
            stack.set(InvGensDataComponents.GENERATOR_ON, true);
        }
    }

    @Override
    public int getBurnTime(ItemStack stack) {
        return stack.getOrDefault(InvGensDataComponents.GENERATOR_BURN_TIME, 0);
    }

    @Override
    public void setBurnTime(ItemStack stack, int burnTime) {
        stack.set(InvGensDataComponents.GENERATOR_BURN_TIME, burnTime);
    }

    public ItemStack getCurrentFuel(ItemStack stack) {
        var itemHandler = stack.getCapability(Capabilities.ItemHandler.ITEM);
        if (itemHandler != null) {
            return itemHandler.getStackInSlot(4);
        }
        return ItemStack.EMPTY;
    }

    public void setCurrentFuel(ItemStack stack, ItemStack fuel) {
        var itemHandler = stack.getCapability(Capabilities.ItemHandler.ITEM);
        if (itemHandler instanceof ComponentItemHandler componentItemHandler) {
            componentItemHandler.setStackInSlot(4, fuel);
        }
    }

    @Override
    public ItemStack getFuel(ItemStack stack, Level level) {
        var itemHandler = stack.getCapability(Capabilities.ItemHandler.ITEM);
        if (itemHandler != null) {
            ItemStack fuelStack = itemHandler.getStackInSlot(0);
            return isItemValid(stack, fuelStack, level) ? fuelStack : ItemStack.EMPTY;
        }
        return ItemStack.EMPTY;
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
            container.set(InvGensDataComponents.FORGE_ENERGY, stored);
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
