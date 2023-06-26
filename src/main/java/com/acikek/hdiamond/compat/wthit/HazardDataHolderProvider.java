package com.acikek.hdiamond.compat.wthit;

import com.acikek.hdiamond.api.HazardDiamondAPI;
import com.acikek.hdiamond.api.util.HazardDataHolder;
import mcp.mobius.waila.api.*;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class HazardDataHolderProvider<T> implements IEntityComponentProvider, IBlockComponentProvider, IDataProvider<T> {

    public static final HazardDataHolderProvider<Entity> ENTITY = new HazardDataHolderProvider<>(HDiamondWailaPlugin.ENTITY_INFO);
    public static final HazardDataHolderProvider<Block> BLOCK = new HazardDataHolderProvider<>(HDiamondWailaPlugin.BLOCK_INFO);

    public Identifier option;

    public HazardDataHolderProvider(Identifier option) {
        this.option = option;
    }

    @Override
    public void appendBody(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(HDiamondWailaPlugin.ENTITY_INFO)) {
            HazardDiamondAPI.appendWailaTooltip(accessor.getData().raw(), tooltip::addLine);
        }
    }

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(HDiamondWailaPlugin.BLOCK_INFO)) {
            HazardDiamondAPI.appendWailaTooltip(accessor.getData().raw(), tooltip::addLine);
        }
    }

    @Override
    public void appendData(IDataWriter data, IServerAccessor<T> accessor, IPluginConfig config) {
        if (!config.getBoolean(option)) {
            return;
        }
        if (accessor.getTarget() instanceof HazardDataHolder holder) {
            var hazardData = holder.getHazardData();
            if (hazardData.isEmpty()) {
                return;
            }
            HazardDiamondAPI.appendWailaServerData(data.raw(), hazardData);
        }
    }
}
