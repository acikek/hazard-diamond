package com.acikek.hdiamond.client.screen;

import com.acikek.hdiamond.api.event.HazardScreenEdited;
import com.acikek.hdiamond.api.util.HazardDataHolder;
import com.acikek.hdiamond.core.HazardData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public record HazardScreenData(Identifier id, HazardData data) implements HazardDataHolder {

    public static HazardScreenData immutable(HazardData data) {
        return new HazardScreenData(null, data);
    }

    @Override
    public @NotNull HazardData getHazardData() {
        return data;
    }

    @Override
    public boolean isEditable() {
        return id != null;
    }

    @Override
    public void updateHazardData(HazardData data) {
        HazardScreenEdited.EVENT.invoker().onEdit(MinecraftClient.getInstance().player, this.data, data, id);
    }

    @Override
    public void setHazardData(HazardData data) {
        // No-op
    }
}
