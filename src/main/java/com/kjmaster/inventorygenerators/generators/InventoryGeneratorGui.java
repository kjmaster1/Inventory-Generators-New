package com.kjmaster.inventorygenerators.generators;
;
import com.kjmaster.inventorygenerators.InventoryGenerators;
import com.kjmaster.kjlib.gui.GenericGuiContainer;
import com.kjmaster.kjlib.gui.ManualEntry;
import com.kjmaster.kjlib.gui.Window;
import com.kjmaster.kjlib.gui.layout.HorizontalAlignment;
import com.kjmaster.kjlib.gui.layout.VerticalAlignment;
import com.kjmaster.kjlib.gui.widgets.EnergyBar;
import com.kjmaster.kjlib.gui.widgets.Label;
import com.kjmaster.kjlib.gui.widgets.Panel;
import com.kjmaster.kjlib.gui.widgets.Widgets;
import com.kjmaster.kjlib.tileentity.GenericTileEntity;
import com.kjmaster.kjlib.varia.IEnergyItem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

import static com.kjmaster.inventorygenerators.generators.InventoryGeneratorModule.CONTAINER_INVENTORY_GENERATOR;
import static com.kjmaster.kjlib.gui.widgets.Widgets.positional;

public class InventoryGeneratorGui extends GenericGuiContainer<GenericTileEntity, InventoryGeneratorContainer> {

    private static final ResourceLocation texture = new ResourceLocation(InventoryGenerators.MODID, "textures/gui/container/generator.png");

    private final Component displayName;
    public EnergyBar energyBar;
    private final InventoryGeneratorContainer container;

    public InventoryGeneratorGui(Component textComponent, InventoryGeneratorContainer container, Inventory inventory) {
        super(null, container, inventory, new ManualEntry(new ResourceLocation(InventoryGenerators.MODID, "manual"), new ResourceLocation(InventoryGenerators.MODID, "inventory_generator"), 0));
        displayName = textComponent;
        this.container = container;
    }

    public static void register() {
        MenuScreens.register(CONTAINER_INVENTORY_GENERATOR.get(), InventoryGeneratorGui::createInventoryGeneratorGui);
    }

    @Nonnull
    private static InventoryGeneratorGui createInventoryGeneratorGui(InventoryGeneratorContainer container, Inventory inventory, Component textComponent) {
        return new InventoryGeneratorGui(textComponent, container, inventory);
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
        getWindowManager().closeWindow(sideWindow.getWindow());
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
        drawWindow(graphics);
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
