package com.acikek.hdiamond.client;

import com.acikek.hdiamond.HDiamond;
import com.acikek.hdiamond.client.config.HDiamondConfig;
import com.acikek.hdiamond.client.render.PanelEntityRenderer;
import com.acikek.hdiamond.network.HDNetworking;
import mc.microconfig.MicroConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class HDiamondClient implements ClientModInitializer {

    public static HDiamondConfig config;

    public static final Identifier WIDGETS = HDiamond.id("textures/gui/hazards.png");

    @Override
    public void onInitializeClient() {
        PanelEntityRenderer.register();
        HDNetworking.registerClient();
        config = MicroConfig.getOrCreate("hdiamond", new HDiamondConfig());
    }
}
