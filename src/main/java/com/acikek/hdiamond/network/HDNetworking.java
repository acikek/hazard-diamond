package com.acikek.hdiamond.network;

import com.acikek.hdiamond.HDiamond;
import com.acikek.hdiamond.api.HazardDiamondAPI;
import com.acikek.hdiamond.api.util.HazardDataHolder;
import com.acikek.hdiamond.core.HazardData;
import com.acikek.hdiamond.entity.PanelEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Collection;

public class HDNetworking {

    public static final Identifier UPDATE_PANEL = HDiamond.id("update_panel");
    public static final Identifier OPEN_SCREEN = HDiamond.id("open_screen");

    @Environment(EnvType.CLIENT)
    public static void c2sUpdatePanelData(PanelEntity entity, HazardData data) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(entity.getId());
        data.write(buf);
        ClientPlayNetworking.send(UPDATE_PANEL, buf);
    }

    public static void s2cOpenScreen(Collection<ServerPlayerEntity> players, HazardData data) {
        PacketByteBuf buf = PacketByteBufs.create();
        data.write(buf);
        for (var player : players) {
            ServerPlayNetworking.send(player, OPEN_SCREEN, buf);
        }
    }

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(UPDATE_PANEL, (server, player, handler, buf, responseSender) -> {
            int id = buf.readInt();
            HazardData data = HazardData.read(buf);
            server.execute(() -> {
                Entity entity = player.getWorld().getEntityById(id);
                if (entity instanceof PanelEntity panelEntity) {
                    panelEntity.setHazardData(data);
                }
            });
        });
    }

    @Environment(EnvType.CLIENT)
    public static void registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(OPEN_SCREEN, (client, handler, buf, responseSender) -> {
            HazardData data = HazardData.read(buf);
            client.execute(() -> HazardDiamondAPI.open(data));
        });
    }
}
