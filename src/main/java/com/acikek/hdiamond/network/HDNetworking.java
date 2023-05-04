package com.acikek.hdiamond.network;

import com.acikek.hdiamond.HDiamond;
import com.acikek.hdiamond.core.HazardData;
import com.acikek.hdiamond.entity.PanelEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class HDNetworking {

    public static final Identifier UPDATE_PANEL = HDiamond.id("update_panel");

    public static void c2sUpdatePanelData(PanelEntity entity, HazardData data) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(entity.getId());
        data.write(buf);
        ClientPlayNetworking.send(UPDATE_PANEL, buf);
    }

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(UPDATE_PANEL, (server, player, handler, buf, responseSender) -> {
            int id = buf.readInt();
            HazardData data = HazardData.read(buf);
            server.execute(() -> {
                Entity entity = player.getWorld().getEntityById(id);
                if (entity instanceof PanelEntity panelEntity) {
                    panelEntity.updateHazardData(data);
                }
            });
        });
    }
}
