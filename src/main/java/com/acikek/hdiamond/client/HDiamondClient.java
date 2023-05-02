package com.acikek.hdiamond.client;

import com.acikek.hdiamond.command.HDiamondCommand;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class HDiamondClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(HDiamondCommand.CHANNEL, (client, handler, buf, responseSender) -> {
            client.execute(() -> client.setScreen(new HazardScreen(Text.empty())));
        });
    }
}
