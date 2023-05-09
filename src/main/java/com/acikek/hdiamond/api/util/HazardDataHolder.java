package com.acikek.hdiamond.api.util;

import com.acikek.hdiamond.core.HazardData;
import org.jetbrains.annotations.NotNull;

/**
 * An object that contains a {@link HazardData} object.
 * The data itself is an implementor of this interface.
 */
public interface HazardDataHolder {

    /**
     * @return the contained hazard data
     */
    @NotNull HazardData getHazardData();
}
