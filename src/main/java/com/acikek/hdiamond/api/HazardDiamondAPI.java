package com.acikek.hdiamond.api;

import com.acikek.hdiamond.core.HazardData;
import com.acikek.hdiamond.load.HazardDataLoader;
import net.minecraft.util.Identifier;

public class HazardDiamondAPI {

    /**
     * Retrieves loaded hazard data from the {@code hazard_data} data pack source.
     * @param id the ID of the hazard data object
     * @return the data object, if any
     * @throws IllegalStateException if the data object does not exist
     */
    public static HazardData getData(Identifier id) {
        var result = HazardDataLoader.hazardData.get(id);
        if (result == null) {
            throw new IllegalStateException("hazard data '" + id + "' does not exist");
        }
        return result;
    }
}
