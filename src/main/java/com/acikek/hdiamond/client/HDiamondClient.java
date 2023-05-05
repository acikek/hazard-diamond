package com.acikek.hdiamond.client;

import com.acikek.hdiamond.client.render.PanelEntityRenderer;
import com.acikek.hdiamond.network.HDNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class HDiamondClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        PanelEntityRenderer.register();
        HDNetworking.registerClient();
    }
}
