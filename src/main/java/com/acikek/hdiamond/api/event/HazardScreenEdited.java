package com.acikek.hdiamond.api.event;

import com.acikek.hdiamond.core.HazardData;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Identifier;

@FunctionalInterface
public interface HazardScreenEdited {

    void onEdit(ClientPlayerEntity player, HazardData original, HazardData updated, Identifier id);
}
