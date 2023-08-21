package com.kjmaster.inventorygenerators.generators;

import com.kjmaster.inventorygenerators.InventoryGenerators;
import com.kjmaster.inventorygenerators.curios.CuriosIntegration;
import com.kjmaster.inventorygenerators.data.InventoryGeneratorManager;
import com.kjmaster.inventorygenerators.keys.KeyBindings;
import com.kjmaster.inventorygenerators.network.PacketSyncGeneratorEnergy;
import com.kjmaster.inventorygenerators.setup.InventoryGeneratorsBaseMessages;
import com.kjmaster.kjlib.container.GenericItemHandler;
import com.kjmaster.kjlib.items.BaseItem;
import com.kjmaster.kjlib.util.helpers.StringHelper;
import com.kjmaster.kjlib.varia.ComponentFactory;
import com.kjmaster.kjlib.varia.IEnergyItem;
import com.kjmaster.kjlib.varia.ItemCapabilityProvider;
import io.netty.buffer.Unpooled;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
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
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static com.kjmaster.inventorygenerators.setup.Config.*;

public abstract class InventoryGeneratorItem extends BaseItem implements IInventoryGenerator, IEnergyItem {

    final String generatorName;

    public InventoryGeneratorItem(String generatorName) {
        super(new Item.Properties()
                .stacksTo(1));
        this.generatorName = generatorName;
    }

    @Override
    protected void tooltipDelegate(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {

        tooltip.add(Component.translatable("info.invgens." + generatorName).withStyle(ChatFormatting.BOLD, ChatFormatting.GOLD));

        if (isOn(stack)) {
            tooltip.add(StringHelper.getNoticeText("info.invgens.1"));
            tooltip.add(StringHelper.getDeactivationText("info.invgens.2"));
        } else {
            tooltip.add(StringHelper.getActivationText("info.invgens.3"));
        }

        if (isInChargingMode(stack)) {
            tooltip.add(StringHelper.getNoticeText("info.invgens.modeOn"));
        } else {
            tooltip.add(StringHelper.getNoticeText("info.invgens.modeOff"));
        }

        AtomicInteger numSpeedUpgrades = new AtomicInteger();
        stack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent((inv) -> {
            ItemStack speedUpgradesStack = inv.getStackInSlot(1);
            numSpeedUpgrades.set(speedUpgradesStack.getCount());
        });

        MutableComponent mutableComponent = Component.literal(StringHelper.localize("info.invgens.mode", KeyBindings.changeMode.getKey().getDisplayName().getString()));
        tooltip.add(mutableComponent);
        MutableComponent mutableComponent1 = Component.literal(StringHelper.localize("info.invgens.charge") + ": " + StringHelper.getScaledNumber(getInternalEnergyStored(stack)));
        mutableComponent1.append(" / " + StringHelper.getScaledNumber(getMaxEnergyStored(stack)) + " RF");
        tooltip.add(mutableComponent1);
        MutableComponent mutableComponent3 = Component.literal(StringHelper.localize("info.invgens.burnTimeLeft") + ": " + StringHelper.format(getBurnTime(stack)));
        tooltip.add(mutableComponent3);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level pLevel, @NotNull Entity entity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(stack, pLevel, entity, pSlotId, pIsSelected);

        IInventoryGenerator inventoryGenerator = (IInventoryGenerator) stack.getItem();
        if (entity instanceof Player player && !pLevel.isClientSide) {
            if (!stack.hasTag()) {
                giveTagCompound(stack);
            }

            if (getBurnTime(stack) < 0) {
                setBurnTime(stack, 0);
            }

            if (getBurnTime(stack) == 0) {
                setCurrentFuel(stack, ItemStack.EMPTY);
            }

            stack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent((inv) -> {
                ItemStack speedUpgradeStack = inv.getStackInSlot(1);
                int numSpeedUpgrades = speedUpgradeStack.getCount();

                if (isOn(stack)) {

                    handleFuel(stack, inv);

                    for (int i = 0; i <= numSpeedUpgrades; i++) {
                         handleEnergy(stack, inventoryGenerator, player);
                    }

                    if (inv.getStackInSlot(3).isEmpty() && hasSideEffect() && doSideEffects && getBurnTime(stack) > 0 && !(getInternalEnergyStored(stack) == getMaxEnergyStored(stack))) {
                        giveSideEffect(player);
                    }
                }
                if (isInChargingMode(stack) && doSendEnergy) {
                    handleCharging(stack, numSpeedUpgrades, inventoryGenerator, player);
                }
            });
        }
    }

    private void handleFuel(ItemStack stack, IItemHandler inv) {
        if (getBurnTime(stack) <= 0 && !getFuel(stack).isEmpty()
                && !(getInternalEnergyStored(stack) == getMaxEnergyStored(stack))) {
            ItemStack fuel = getFuel(stack);
            setBurnTime(stack, calculateTime(fuel));
            setCurrentFuel(stack, fuel);
            fuel.shrink(1);
            if (fuel.getItem() instanceof PotionItem && fuel.getCount() == 1) {
                inv.insertItem(0, new ItemStack(Items.GLASS_BOTTLE, 1), false);
            }
            if (fuel.getItem().equals(Items.LAVA_BUCKET) || fuel.getItem().equals(Items.WATER_BUCKET)) {
                inv.insertItem(0, new ItemStack(Items.BUCKET, 1), false);
            }
        }
    }

    private void handleEnergy(ItemStack stack, IInventoryGenerator inventoryGenerator, Player player) {
        if (!(getInternalEnergyStored(stack) == getMaxEnergyStored(stack)) && getBurnTime(stack) > 0) {
            setBurnTime(stack, getBurnTime(stack) - 1);
            int rfToGive = calculatePower(stack);
            receiveInternalEnergy(stack, rfToGive);
            sendSyncPacket(stack, inventoryGenerator, player);
        }
    }

    private void handleCharging(ItemStack stack, int numSpeedUpgrades, IInventoryGenerator inventoryGenerator, Player player) {
        for (int i = 0; i <= numSpeedUpgrades; i++) {
            ArrayList<ItemStack> chargeables = getChargeables(player);
            giveEnergyToChargeables(chargeables, stack);
            sendSyncPacket(stack, inventoryGenerator, player);
        }
    }

    protected void openGui(Player player, String key, ItemStack stack) {

        NetworkHooks.openScreen((ServerPlayer)player, new MenuProvider() {
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

        if (stack.getItem() instanceof IInventoryGenerator inventoryGenerator) {
            sendSyncPacket(stack, inventoryGenerator, player);
        }
    }

    protected void sendSyncPacket(@NotNull ItemStack stack, IInventoryGenerator inventoryGenerator, Player player) {
        FriendlyByteBuf friendlyByteBuf = new FriendlyByteBuf(Unpooled.buffer());
        friendlyByteBuf.writeInt(getInternalEnergyStored(stack));
        if (player instanceof ServerPlayer serverPlayer) {
            InventoryGeneratorsBaseMessages.INSTANCE.sendTo(new PacketSyncGeneratorEnergy(friendlyByteBuf), serverPlayer.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public void giveSideEffect(Player player) {
    }

    public boolean hasSideEffect() {
        return false;
    }

    @Override
    public void giveTagCompound(ItemStack stack) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putBoolean("On", false);
        compoundTag.putBoolean("Charging", false);
        compoundTag.putInt("BurnTime", 0);
        stack.setTag(compoundTag);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
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
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        InvEnergyProvider invEnergyProvider = new InvEnergyProvider(stack, this);
        invEnergyProvider.deserializeNBT(stack.getOrCreateTag());
        return invEnergyProvider;
    }

    class InvEnergyProvider extends ItemCapabilityProvider implements ICapabilitySerializable<Tag> {

        public final LazyOptional<GenericItemHandler> inv = LazyOptional.of(this::createInventory);
        public final ItemStack stack;

        private <T> @NotNull GenericItemHandler createInventory() {
            return GenericItemHandler.create(null, InventoryGeneratorContainer.CONTAINER_FACTORY).build();
        }

        public InvEnergyProvider(ItemStack itemStack, IEnergyItem item) {
            super(itemStack, item);
            this.stack = itemStack;
        }

        @NotNull
        @Override
        public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return this.getCapability(cap);
        }

        @NotNull
        @Override
        public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability) {
            if (capability == ForgeCapabilities.ITEM_HANDLER) {
                return this.inv.cast();
            }
            if (capability == ForgeCapabilities.ENERGY) {
                return this.energy.cast();
            }
            return LazyOptional.empty();
        }

        @Override
        public Tag serializeNBT() {
            GenericItemHandler handler = inv.orElse(createInventory());
            stack.getOrCreateTag().put("Inventory", handler.serializeNBT());
            return handler.serializeNBT();
        }

        @Override
        public void deserializeNBT(Tag nbt) {
            GenericItemHandler handler = inv.orElse(createInventory());
            CompoundTag tag = stack.getOrCreateTag();
            nbt = tag.get("Inventory");
            if (nbt instanceof ListTag listTag) {
                handler.deserializeNBT(listTag);
            }
        }
    }

    @Override
    public ArrayList<ItemStack> getChargeables(Player player) {
        ArrayList<ItemStack> chargeables = new ArrayList<>();
        IItemHandlerModifiable inventory = CuriosIntegration.hasMod() ? CuriosIntegration.getFullInventory(player) : new PlayerInvWrapper(player.getInventory());
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack invStack = inventory.getStackInSlot(i);
            LazyOptional<IEnergyStorage> energyStorageLazyOptional = invStack.getCapability(ForgeCapabilities.ENERGY);
            energyStorageLazyOptional.ifPresent((energyStorage) -> {
                if (energyStorage.canReceive() &&  (energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored())) {
                    chargeables.add(invStack);
                }
            });
        }
        return chargeables;
    }

    @Override
    public void giveEnergyToChargeables(ArrayList<ItemStack> chargeables, ItemStack stack) {
        for (ItemStack chargeableStack : chargeables) {
            LazyOptional<IEnergyStorage> energyStorageLazyOptional = chargeableStack.getCapability(ForgeCapabilities.ENERGY);
            energyStorageLazyOptional.ifPresent((energyStorage) -> {
                int energySent = energyStorage.receiveEnergy(getInternalEnergyStored(stack) / chargeables.size(), false);
                extractEnergy(stack, energySent, false);
            });
        }
        chargeables.clear();
    }

    @Override
    public void receiveInternalEnergy(ItemStack stack, int energy) {
        CompoundTag compoundTag = stack.getOrCreateTag();
        compoundTag.putInt("Energy", getInternalEnergyStored(stack) + energy);
    }

    @Override
    public int getInternalEnergyStored(ItemStack stack) {
        CompoundTag compoundTag = stack.getOrCreateTag();
        if (compoundTag.contains("Energy"))
            return compoundTag.getInt("Energy");
        else {
            compoundTag.putInt("Energy", 0);
            stack.setTag(compoundTag);
        }
        return 0;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        if (InventoryGeneratorManager.INSTANCE.generatorToAllowedItems.containsKey(generatorName)) {
            return InventoryGeneratorManager.INSTANCE.generatorToAllowedItems.get(generatorName).contains(stack.getItem());
        }
        return false;
    }

    @Override
    public int calculateTime(ItemStack stack) {
        if (!stack.isEmpty()) {
            Item item = stack.getItem();
            if (InventoryGeneratorManager.INSTANCE.generatorToItemConfiguration.containsKey(generatorName)) {
                Map<Item, List<Integer>> map = InventoryGeneratorManager.INSTANCE.generatorToItemConfiguration.get(generatorName);
                if (map.containsKey(item)) {
                    List<Integer> timeAndRF = map.get(item);
                    return timeAndRF.get(0);
                }
            }
        }
        return 0;
    }

    @Override
    public int calculatePower(ItemStack stack) {
        int minSend = 0;
        Item fuel = getCurrentFuel(stack).getItem();

        if (InventoryGeneratorManager.INSTANCE.generatorToItemConfiguration.containsKey(generatorName)) {
            Map<Item, List<Integer>> map = InventoryGeneratorManager.INSTANCE.generatorToItemConfiguration.get(generatorName);
            if (map.containsKey(fuel)) {
                List<Integer> timeAndRF = map.get(fuel);
                minSend = timeAndRF.get(1);
            }
        }
        return Math.min(getMaxEnergyStored(stack) - getInternalEnergyStored(stack),  minSend);
    }

    @Override
    public boolean isInChargingMode(ItemStack stack) {
        CompoundTag compoundTag = stack.getOrCreateTag();
        if (compoundTag.contains("Charging"))
            return compoundTag.getBoolean("Charging");
        else {
            compoundTag.putBoolean("Charging", false);
            stack.setTag(compoundTag);
        }
        return false;
    }

    @Override
    public void changeMode(ItemStack stack, IInventoryGenerator inventoryGenerator, Player player) {
        if (isInChargingMode(stack) && stack.hasTag()) {
            CompoundTag compoundTag = stack.getTag();
            assert compoundTag != null;
            compoundTag.putBoolean("Charging", false);
        } else {
            if (stack.hasTag()) {
                CompoundTag tagCompound = stack.getTag();
                assert tagCompound != null;
                tagCompound.putBoolean("Charging", true);
            } else {
                CompoundTag tagCompound = new CompoundTag();
                tagCompound.putBoolean("Charging", false);
                stack.setTag(tagCompound);
            }
        }
    }

    @Override
    public boolean isOn(ItemStack stack) {
        if (stack.hasTag()) {
            CompoundTag compoundTag = stack.getTag();
            assert compoundTag != null;
            if (compoundTag.contains("On")) {
                return compoundTag.getBoolean("On");
            } else {
                compoundTag.putBoolean("On", false);
            }
        } else {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putBoolean("On", false);
            stack.setTag(compoundTag);
        }
        return false;
    }

    @Override
    public void turnOn(ItemStack stack) {
        if (isOn(stack) && stack.hasTag()) {
            CompoundTag nbtTagCompound = stack.getTag();
            assert nbtTagCompound != null;
            nbtTagCompound.putBoolean("On", false);
        } else {
            if (stack.hasTag()) {
                CompoundTag tagCompound = stack.getTag();
                assert tagCompound != null;
                tagCompound.putBoolean("On", true);
            } else {
                CompoundTag nbtTagCompound = new CompoundTag();
                nbtTagCompound.putBoolean("On", false);
                stack.setTag(nbtTagCompound);
            }
        }
    }

    @Override
    public int getBurnTime(ItemStack stack) {
        CompoundTag compoundTag = stack.getOrCreateTag();
        if (compoundTag.contains("BurnTime"))
            return compoundTag.getInt("BurnTime");
        else {
            compoundTag.putInt("BurnTime", 0);
            stack.setTag(compoundTag);
        }
        return 0;
    }

    @Override
    public void setBurnTime(ItemStack stack, int burnTime) {
        if (stack.hasTag()) {
            CompoundTag nbtTagCompound = stack.getTag();
            assert nbtTagCompound != null;
            if (nbtTagCompound.contains("BurnTime")) {
                nbtTagCompound.putInt("BurnTime", burnTime);
            } else {
                nbtTagCompound.putInt("BurnTime", 0);
            }
        } else {
            CompoundTag nbtTagCompound = new CompoundTag();
            nbtTagCompound.putInt("BurnTime", 0);
            stack.setTag(nbtTagCompound);
        }
    }


    public ItemStack getCurrentFuel(ItemStack stack) {
        if (stack.hasTag()) {
            CompoundTag nbtTagCompound = stack.getTag();
            assert nbtTagCompound != null;
            if (nbtTagCompound.contains("CurrentFuel")) {
                return ItemStack.of(nbtTagCompound.getCompound("CurrentFuel"));
            }
        }
        return ItemStack.EMPTY;
    }


    public void setCurrentFuel(ItemStack stack, ItemStack fuel) {
        if (stack.hasTag()) {
            CompoundTag nbtTagCompound = stack.getTag();
            assert nbtTagCompound != null;
            nbtTagCompound.put("CurrentFuel", fuel.save(new CompoundTag()));
        } else {
            CompoundTag nbtTagCompound = new CompoundTag();
            nbtTagCompound.put("CurrentFuel", fuel.save(new CompoundTag()));
            stack.setTag(nbtTagCompound);
        }
    }

    @Override
    public ItemStack getFuel(ItemStack stack) {
        LazyOptional<IItemHandler> itemHandlerLazyOptional = stack.getCapability(ForgeCapabilities.ITEM_HANDLER);

        if (itemHandlerLazyOptional.isPresent()) {
            IItemHandler itemHandler = itemHandlerLazyOptional.orElseThrow(RuntimeException::new);
            ItemStack fuelStack = itemHandler.getStackInSlot(0);
            return isItemValid(fuelStack) ? fuelStack : ItemStack.EMPTY;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public long receiveEnergyL(ItemStack itemStack, long l, boolean b) {
        return 0;
    }

    @Override
    public long extractEnergyL(ItemStack itemStack, long l, boolean b) {
        return this.extractEnergy(itemStack, (int)l, b);
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
        if (!container.hasTag()) {
            CompoundTag newTagCompound = new CompoundTag();
            newTagCompound.putInt("Energy", 0);
            container.setTag(newTagCompound);
        }
        int energyExtracted = Math.min(this.getInternalEnergyStored(container), Math.min(this.getMaxEnergyStored(container), maxExtract));
        if (!simulate) {
            int stored = this.getInternalEnergyStored(container);
            stored -= energyExtracted;
            assert container.getTag() != null;
            container.getTag().putInt("Energy", stored);
        }

        return energyExtracted;
    }

    public int getReceive() {
        return 0;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && (slotChanged
                || getInternalEnergyStored(oldStack) > 0 != getInternalEnergyStored(newStack) > 0);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        super.isFoil(pStack);
        return isInChargingMode(pStack);
    }

    public static void initOverrides(InventoryGeneratorItem item) {
        ItemProperties.register(item, new ResourceLocation(InventoryGenerators.MODID, "on"), (stack, worldIn, entityIn, integer) -> {
            if (stack.getItem() instanceof IInventoryGenerator inventoryGenerator) {
                return inventoryGenerator.isOn(stack) && (inventoryGenerator.getBurnTime(stack) > 0) ? 1 : 0;
            }
            return 0;
        });
    }
}
