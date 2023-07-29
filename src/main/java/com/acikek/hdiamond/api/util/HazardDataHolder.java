package com.acikek.hdiamond.api.util;

import com.acikek.hdiamond.core.HazardData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
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

    /**
     * @return whether the hazard data can be edited
     */
    boolean isEditable();

    /**
     * Updates the hazard data server-side from the client.
     * @param data the data to send
     */
    @Environment(EnvType.CLIENT)
    void updateHazardData(HazardData data);

    /**
     * Sets and syncs the contained hazard data.
     * @param data the data to set
     * @param broadcaster the player who triggered this call
     */
    void setHazardData(HazardData data, ServerPlayerEntity broadcaster);

    /**
     * Sets the contained hazard data.
     * @see HazardDataHolder#setHazardData(HazardData, ServerPlayerEntity)
     */
    default void setHazardData(HazardData data) {
        setHazardData(data, null);
    }
}
