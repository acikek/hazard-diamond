package com.acikek.hdiamond.api.impl;

import com.acikek.hdiamond.client.screen.HazardScreen;
import com.acikek.hdiamond.core.HazardData;
import com.acikek.hdiamond.load.HazardDataLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class HazardDiamondAPIImpl {

    public static HazardData getData(Identifier id) {
        return HazardDataLoader.hazardData.get(id);
    }

    public static void setScreen(HazardScreen screen) {
        MinecraftClient.getInstance().setScreen(screen);
    }
}
