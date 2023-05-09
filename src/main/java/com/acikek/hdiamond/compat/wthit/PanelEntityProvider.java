package com.acikek.hdiamond.compat.wthit;

import com.acikek.hdiamond.api.HazardDiamondAPI;
import com.acikek.hdiamond.entity.PanelEntity;
import mcp.mobius.waila.api.*;
import net.minecraft.nbt.NbtCompound;

public class PanelEntityProvider implements IEntityComponentProvider, IServerDataProvider<PanelEntity> {

    public static final PanelEntityProvider INSTANCE = new PanelEntityProvider();

    @Override
    public void appendBody(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(HDiamondWailaPlugin.PANEL_INFO)) {
            HazardDiamondAPI.appendWailaTooltip(accessor.getServerData(), tooltip::addLine);
        }
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
        HazardDiamondAPI.appendWailaServerData(data, hazardData);
    }
}
