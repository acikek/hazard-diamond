package com.acikek.hdiamond;

import com.acikek.hdiamond.command.HDiamondCommand;
import com.acikek.hdiamond.core.HazardData;
import com.acikek.hdiamond.entity.PanelEntity;
import com.acikek.hdiamond.item.PanelItem;
import com.acikek.hdiamond.network.HDNetworking;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class HDiamond implements ModInitializer {

    public static final String ID = "hdiamond";

    public static Identifier id(String path) {
        return new Identifier(ID, path);
    }

    @Override
    public void onInitialize() {
        HazardData.register();
        PanelEntity.register();
        PanelItem.register();
        HDNetworking.register();
        HDiamondCommand.register();
    }
}
