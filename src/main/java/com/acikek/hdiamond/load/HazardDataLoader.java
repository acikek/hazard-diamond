package com.acikek.hdiamond.load;

import com.acikek.hdiamond.HDiamond;
import com.acikek.hdiamond.core.HazardData;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;

import java.util.HashMap;
import java.util.Map;

public class HazardDataLoader extends JsonDataLoader implements IdentifiableResourceReloadListener {

    public static final String TYPE = "hazard_data";

    public static Map<Identifier, HazardData> hazardData = new HashMap<>();

    public HazardDataLoader() {
        super(new Gson(), TYPE);
    }


    @Override
    public Identifier getFabricId() {
        return HDiamond.id(TYPE);
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
        hazardData.clear();
        int successes = 0;
        for (var pair : prepared.entrySet()) {
            try {
                var obj = JsonHelper.asObject(pair.getValue(), "hazard data");
                var data = HazardData.fromJson(obj);
                hazardData.put(pair.getKey(), data);
                successes++;
            }
            catch (Exception e) {
                HDiamond.LOGGER.error("Failed to load hazard data '" + pair.getKey() + "':", e);
            }
        }
        HDiamond.LOGGER.info("Loaded " + successes + " hazard data object" + (successes == 1 ? "" : "s"));
    }

    public static void register() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new HazardDataLoader());
    }
}
