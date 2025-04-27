package com.kjmaster.inventorygenerators.generators;

import com.kjmaster.inventorygenerators.InventoryGenerators;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.ManualEntry;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.layout.VerticalAlignment;
import mcjty.lib.gui.widgets.EnergyBar;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.Widgets;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.IEnergyItem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

import static com.kjmaster.inventorygenerators.generators.InventoryGeneratorModule.CONTAINER_INVENTORY_GENERATOR;
import static mcjty.lib.gui.widgets.Widgets.positional;

public class InventoryGeneratorGui extends GenericGuiContainer<GenericTileEntity, InventoryGeneratorContainer> {

    private static final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(InventoryGenerators.MODID, "textures/gui/container/generator.png");

    private final Component displayName;
    public final InventoryGeneratorContainer container;
    public EnergyBar energyBar;

    public InventoryGeneratorGui(Component textComponent, InventoryGeneratorContainer container, Inventory inventory) {
        super(container, inventory, textComponent, new ManualEntry(ResourceLocation.fromNamespaceAndPath(InventoryGenerators.MODID, "manual"), ResourceLocation.fromNamespaceAndPath(InventoryGenerators.MODID, "inventory_generator"), 0));
        displayName = textComponent;
        this.container = container;
    }

    @Nonnull
    static InventoryGeneratorGui createInventoryGeneratorGui(InventoryGeneratorContainer container, Inventory inventory, Component textComponent) {
        return new InventoryGeneratorGui(textComponent, container, inventory);
    }

    public static void register(RegisterMenuScreensEvent event) {
        event.register(CONTAINER_INVENTORY_GENERATOR.get(), InventoryGeneratorGui::createInventoryGeneratorGui);
    }

    @Override
    public void init() {
        super.init();

        energyBar = new EnergyBar().name("energybar").vertical().hint(imageWidth - 10, imageHeight / 7, 8, 54).showText(false).showRfPerTick(false);

        int titleWidth = font.width(displayName.getString());
        int x = getXSize() / 2 - titleWidth / 2;

        Label name = Widgets.label(x, 15, titleWidth, font.lineHeight, displayName.getString()).horizontalAlignment(HorizontalAlignment.ALIGN_CENTER).verticalAlignment(VerticalAlignment.ALIGN_TOP);

        Panel panel = positional().background(texture).children(name, energyBar);
        panel.bounds(leftPos, topPos, imageWidth, imageHeight);
        window = new Window(this, panel);
        initializeFields();
    }

    private void initializeFields() {
        energyBar = window.findChild("energybar");
    }

    private void updateFields() {
        if (window == null) {
            return;
        }
        updateEnergyBar(energyBar);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int x, int y) {
        updateFields();
        drawWindow(graphics, partialTicks, x, y);
    }

    @Override
    protected void updateEnergyBar(EnergyBar energyBar) {
        ItemStack stack = container.getStack();
        Item item = stack.getItem();
        if (item instanceof IEnergyItem iEnergyItem) {
            energyBar.maxValue(iEnergyItem.getMaxEnergyStored(stack));
        }
    }
}
