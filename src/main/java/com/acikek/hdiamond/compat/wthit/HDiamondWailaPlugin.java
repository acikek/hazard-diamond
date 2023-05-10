package com.acikek.hdiamond.compat.wthit;

import com.acikek.hdiamond.HDiamond;
import mcp.mobius.waila.api.*;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class HDiamondWailaPlugin implements IWailaPlugin {

    public static final Identifier ENTITY_INFO = HDiamond.id("entity_info");
    public static final Identifier BLOCK_INFO = HDiamond.id("block_info");

    @Override
    public void register(IRegistrar registrar) {
        registrar.addConfig(ENTITY_INFO, true);
        registrar.addConfig(BLOCK_INFO, true);
        registrar.addComponent((IEntityComponentProvider) HazardDataHolderProvider.ENTITY, TooltipPosition.BODY, Entity.class);
        registrar.addEntityData(HazardDataHolderProvider.ENTITY, Entity.class);
        registrar.addComponent((IBlockComponentProvider) HazardDataHolderProvider.BLOCK, TooltipPosition.BODY, Block.class);
    }
}
