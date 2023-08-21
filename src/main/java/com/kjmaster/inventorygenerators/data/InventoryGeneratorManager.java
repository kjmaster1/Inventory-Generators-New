package com.kjmaster.inventorygenerators.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class InventoryGeneratorManager extends SimpleJsonResourceReloadListener {

    private static final Gson GSON =
            (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    public static final InventoryGeneratorManager INSTANCE = new InventoryGeneratorManager();

    public InventoryGeneratorManager() {
        super(GSON, "inventorygenerators/generators");
    }

    public Map<String, Map<Item, List<Integer>>> generatorToItemConfiguration = new HashMap<>();
    public Map<String, ArrayList<Item>> generatorToAllowedItems = new HashMap<>();

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, @NotNull ResourceManager pResourceManager, @NotNull ProfilerFiller pProfiler) {

        for (Map.Entry<ResourceLocation, JsonElement> entry : pObject.entrySet()) {
            ResourceLocation resourceLocation = entry.getKey();

            if (resourceLocation.getPath().startsWith("_")) {
                continue;
            }

            String path = resourceLocation.getPath();

            JsonObject jsonObject = entry.getValue().getAsJsonObject();

            boolean replace = jsonObject.getAsJsonPrimitive("replace").getAsBoolean();

            Map<String, List<Integer>> map = new Gson().fromJson(jsonObject.get("values"), new TypeToken<HashMap<String, List<Integer>>>(){}.getType());

            ArrayList<Item> allowedItems = new ArrayList<>();
            Map<Item, List<Integer>> itemToTimeAndRF = new HashMap<>();

            map.forEach((resourceLocationString, timeAndRFPerTick) -> {
                ResourceLocation resourceLocationItem = new ResourceLocation(resourceLocationString);
                Optional<Holder<Item>> itemHolder = ForgeRegistries.ITEMS.getHolder(resourceLocationItem);
                itemHolder.ifPresent((itemHolder1 -> {
                    Item item = itemHolder1.get();
                    allowedItems.add(item);
                    itemToTimeAndRF.put(item, timeAndRFPerTick);
                }));
            });

            if (replace) {
                generatorToAllowedItems.put(path, allowedItems);
                generatorToItemConfiguration.put(path, itemToTimeAndRF);
            } else {
                Map<String, Map<Item, List<Integer>>> generatorToItemConfigurationCopy = generatorToItemConfiguration;
                Map<String, ArrayList<Item>> generatorToAllowedItemsCopy = generatorToAllowedItems;
                Map<Item, List<Integer>> itemToTimeAndRfCopy = generatorToItemConfigurationCopy.get(path);
                ArrayList<Item> allowedItemsCopy = generatorToAllowedItemsCopy.get(path);
                if (allowedItemsCopy != null) {
                    allowedItemsCopy.addAll(allowedItems);
                    generatorToAllowedItems.put(path, allowedItemsCopy);
                } else {
                    generatorToAllowedItems.put(path, allowedItems);
                }
                if (itemToTimeAndRfCopy != null) {
                    itemToTimeAndRfCopy.putAll(itemToTimeAndRF);
                    generatorToItemConfiguration.put(path, itemToTimeAndRfCopy);
                } else {
                    generatorToItemConfiguration.put(path, itemToTimeAndRF);
                }
            }
        }
    }
}
