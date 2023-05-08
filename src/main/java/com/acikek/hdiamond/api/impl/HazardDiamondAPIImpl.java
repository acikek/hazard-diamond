package com.acikek.hdiamond.api.impl;

import com.acikek.hdiamond.core.HazardData;
import com.acikek.hdiamond.load.HazardDataLoader;
import net.minecraft.util.Identifier;

public class HazardDiamondAPIImpl {

    public static HazardData getData(Identifier id) {
        return HazardDataLoader.hazardData.get(id);
    }
}
