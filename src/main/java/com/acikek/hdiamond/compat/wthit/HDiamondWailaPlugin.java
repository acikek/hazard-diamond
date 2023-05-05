package com.acikek.hdiamond.compat.wthit;

import com.acikek.hdiamond.HDiamond;
import com.acikek.hdiamond.entity.PanelEntity;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import net.minecraft.util.Identifier;

public class HDiamondWailaPlugin implements IWailaPlugin {

    public static final Identifier PANEL_INFO = HDiamond.id("panel_info");

    @Override
    public void register(IRegistrar registrar) {
        registrar.addConfig(PANEL_INFO, true);
        registrar.addComponent(PanelEntityProvider.INSTANCE, TooltipPosition.BODY, PanelEntity.class);
        registrar.addEntityData(PanelEntityProvider.INSTANCE, PanelEntity.class);
    }
}
