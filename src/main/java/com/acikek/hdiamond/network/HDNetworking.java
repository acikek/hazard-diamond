package com.acikek.hdiamond.network;

import com.acikek.hdiamond.HDiamond;
import com.acikek.hdiamond.api.HazardDiamondAPI;
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

    private static PacketByteBuf createUpdatePacket(PanelEntity entity, HazardData data) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(entity.getId());
        data.write(buf);
        return buf;
    }

    @Environment(EnvType.CLIENT)
    public static void c2sUpdatePanelData(PanelEntity entity, HazardData data) {
        ClientPlayNetworking.send(UPDATE_PANEL, createUpdatePacket(entity, data));
    }

    public static void s2cUpdatePanelData(Collection<ServerPlayerEntity> players, ServerPlayerEntity broadcaster, PanelEntity entity, HazardData data) {
        var buf = createUpdatePacket(entity, data);
        for (var player : players) {
            if (player != broadcaster) {
                ServerPlayNetworking.send(player, UPDATE_PANEL, buf);
            }
        }
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
            final int id = buf.readInt();
            final HazardData data = HazardData.read(buf);
            server.execute(() -> {
                Entity entity = player.getWorld().getEntityById(id);
                if (entity instanceof PanelEntity panelEntity) {
                    panelEntity.setHazardData(data, player);
                }
            });
        });
    }

    @Environment(EnvType.CLIENT)
    public static void registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(UPDATE_PANEL, (client, handler, buf, responseSender) -> {
            final int id = buf.readInt();
            final HazardData data = HazardData.read(buf);
            client.execute(() -> {
                Entity entity = client.world.getEntityById(id);
                if (entity instanceof PanelEntity panelEntity) {
                    panelEntity.setHazardData(data);
                }
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(OPEN_SCREEN, (client, handler, buf, responseSender) -> {
            final HazardData data = HazardData.read(buf);
            client.execute(() -> HazardDiamondAPI.open(data));
        });
    }
}
