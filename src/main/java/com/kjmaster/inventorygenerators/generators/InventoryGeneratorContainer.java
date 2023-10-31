package com.kjmaster.inventorygenerators.generators;

import com.kjmaster.kjlib.container.ContainerFactory;
import com.kjmaster.kjlib.container.GenericContainer;
import com.kjmaster.kjlib.container.SlotDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;

import static com.kjmaster.inventorygenerators.generators.InventoryGeneratorModule.CONTAINER_INVENTORY_GENERATOR;
import static com.kjmaster.inventorygenerators.upgrades.UpgradesModule.*;
import static com.kjmaster.kjlib.container.ContainerFactory.CONTAINER_CONTAINER;

public class InventoryGeneratorContainer extends GenericContainer {

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(4)
            .slot(SlotDefinition.specific((stack1 -> !(stack1.getItem() instanceof IInventoryGenerator))), 0, 80, 35)
            .slot(SlotDefinition.specific(SPEED_UPGRADE.get()), 1, 8, 53)
            .slot(SlotDefinition.specific(AUTO_PULL_UPGRADE.get()), 2, 26, 53)
            .slot(SlotDefinition.specific(NO_EFFECT_UPGRADE.get()), 3, 44, 53)
            .playerSlots(8, 84));
    private final ItemStack stack;

    public InventoryGeneratorContainer(int id, BlockPos pos, @NotNull Player player, ItemStack stack) {
        super(CONTAINER_INVENTORY_GENERATOR.get(), id, CONTAINER_FACTORY.get(), pos, null, player);
        this.stack = stack;

    }

    @Override
    public void setupInventories(IItemHandler itemHandler, Inventory inventory) {
        addInventory(CONTAINER_CONTAINER, itemHandler);
        addInventory(ContainerFactory.CONTAINER_PLAYER, new InvWrapper(inventory));
        generateSlots(inventory.player);
    }

    @Override
    public void addIntegerListener(DataSlot holder) {
        super.addIntegerListener(holder);
    }

    public ItemStack getStack() {
        return stack;
    }
}
