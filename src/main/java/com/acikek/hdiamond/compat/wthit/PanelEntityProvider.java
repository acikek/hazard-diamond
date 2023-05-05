package com.acikek.hdiamond.compat.wthit;

import com.acikek.hdiamond.entity.PanelEntity;
import mcp.mobius.waila.api.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

public class PanelEntityProvider implements IEntityComponentProvider, IServerDataProvider<PanelEntity> {

    public static final PanelEntityProvider INSTANCE = new PanelEntityProvider();

    @Override
    public void appendBody(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        if (!config.getBoolean(HDiamondWailaPlugin.PANEL_INFO)) {
            return;
        }
        NbtCompound data = accessor.getServerData();
        if (!data.contains("WNumerals")) {
            return;
        }
        tooltip.addLine(Text.Serializer.fromJson(data.getString("WNumerals")));
        tooltip.addLine(Text.Serializer.fromJson(data.getString("WPictograms")));
    }

    @Override
    public void appendServerData(NbtCompound data, IServerAccessor<PanelEntity> accessor, IPluginConfig config) {
        if (!config.getBoolean(HDiamondWailaPlugin.PANEL_INFO)) {
            return;
        }
        var hazardData = accessor.getTarget().getHazardData();
        if (hazardData.isEmpty()) {
            return;
        }
        var tooltips = hazardData.getTooltip().stream()
                .map(Text.Serializer::toJson)
                .toList();
        data.putString("WNumerals", tooltips.get(0));
        data.putString("WPictograms", tooltips.get(1));
    }
}
