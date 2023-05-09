package com.acikek.hdiamond.api.event;

import com.acikek.hdiamond.client.screen.HazardScreen;
import com.acikek.hdiamond.core.HazardData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Identifier;

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

    @Environment(EnvType.CLIENT)
    void onEdit(ClientPlayerEntity player, HazardData original, HazardData updated, Identifier id);
}
