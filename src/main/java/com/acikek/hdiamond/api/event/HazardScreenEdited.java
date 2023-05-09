package com.acikek.hdiamond.api.event;

import com.acikek.hdiamond.api.HazardDiamondAPI;
import com.acikek.hdiamond.api.util.HazardDataHolder;
import com.acikek.hdiamond.client.screen.HazardScreen;
import com.acikek.hdiamond.core.HazardData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * A client-side event that fires after a mutable {@link HazardScreen} has been closed by a client.<br>
 * To open a mutable hazard screen, call {@link HazardDiamondAPI#openMutable(HazardDataHolder, Identifier)} on the client.
 */
@FunctionalInterface
@Environment(EnvType.CLIENT)
public interface HazardScreenEdited {

    /**
     * Fired when a mutable {@link HazardScreen} has been closed by the client.
     */
    @Environment(EnvType.CLIENT)
    Event<HazardScreenEdited> EVENT = EventFactory.createArrayBacked(HazardScreenEdited.class,
            handlers -> (player, original, updated, id) -> {
                for (var handler : handlers) {
                    handler.onEdit(player, original, updated, id);
                }
            });

    /**
     * A callback for the event.
     * @param player the client player
     * @param original the original unmodified hazard data
     * @param updated the updated hazard data. You can check if any edits have actually been made with
     *                the {@link HazardData#equals(Object)} implementation.
     * @param id the identifier used in the {@link HazardDiamondAPI#openMutable(HazardDataHolder, Identifier)} call
     */
    @Environment(EnvType.CLIENT)
    void onEdit(ClientPlayerEntity player, HazardData original, HazardData updated, Identifier id);
}
