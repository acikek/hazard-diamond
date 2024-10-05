package com.acikek.hdiamond.network;

import com.acikek.hdiamond.HDiamond;
import com.acikek.hdiamond.api.HazardDiamondAPI;
import com.acikek.hdiamond.core.HazardData;
import com.acikek.hdiamond.entity.PanelEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import java.util.Collection;

public class HDNetworking {

    public record UpdatePanel(int entityId, HazardData data) implements CustomPayload {

        public static final Id<UpdatePanel> PACKET_ID = new Id<>(HDiamond.id("update_panel"));
        public static final PacketCodec<RegistryByteBuf, UpdatePanel> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, UpdatePanel::entityId, HazardData.PACKET_CODEC, UpdatePanel::data, UpdatePanel::new);

        public static void receive(UpdatePanel payload, World world, ServerPlayerEntity player) {
            Entity entity = world.getEntityById(payload.entityId());
            if (entity instanceof PanelEntity panelEntity) {
                panelEntity.setHazardData(payload.data(), player);
            }
        }

        public static void receiveServer(UpdatePanel payload, ServerPlayNetworking.Context context) {
            receive(payload, context.player().getWorld(), context.player());
        }

        @Environment(EnvType.CLIENT)
        public static void receiveClient(UpdatePanel payload, ClientPlayNetworking.Context context) {
            receive(payload, context.player().getWorld(), null);
        }

        @Override
        public Id<? extends CustomPayload> getId() {
            return PACKET_ID;
        }
    }

    public record OpenScreen(HazardData data) implements CustomPayload {

        public static final Id<OpenScreen> PACKET_ID = new Id<>(HDiamond.id("open_screen"));
        public static final PacketCodec<RegistryByteBuf, OpenScreen> PACKET_CODEC = HazardData.PACKET_CODEC.xmap(OpenScreen::new, OpenScreen::data).cast();

        @Environment(EnvType.CLIENT)
        public static void receive(OpenScreen payload, ClientPlayNetworking.Context context) {
            HazardDiamondAPI.open(payload.data());
        }

        @Override
        public Id<? extends CustomPayload> getId() {
            return PACKET_ID;
        }
    }

    @Environment(EnvType.CLIENT)
    public static void c2sUpdatePanelData(PanelEntity entity, HazardData data) {
        ClientPlayNetworking.send(new UpdatePanel(entity.getId(), data));
    }

    public static void s2cUpdatePanelData(Collection<ServerPlayerEntity> players, PanelEntity entity) {
        var payload = new UpdatePanel(entity.getId(), entity.getHazardData());
        players.forEach(player -> ServerPlayNetworking.send(player, payload));
    }

    public static void s2cOpenScreen(Collection<ServerPlayerEntity> players, HazardData data) {
        var payload = new OpenScreen(data);
        players.forEach(player -> ServerPlayNetworking.send(player, payload));

    }

    public static void register() {
        PayloadTypeRegistry.playS2C().register(OpenScreen.PACKET_ID, OpenScreen.PACKET_CODEC);
        if (HDiamond.config.enableContent) {
            PayloadTypeRegistry.playS2C().register(UpdatePanel.PACKET_ID, UpdatePanel.PACKET_CODEC);
            PayloadTypeRegistry.playC2S().register(UpdatePanel.PACKET_ID, UpdatePanel.PACKET_CODEC);
            ServerPlayNetworking.registerGlobalReceiver(UpdatePanel.PACKET_ID, UpdatePanel::receiveServer);
        }
    }

    @Environment(EnvType.CLIENT)
    public static void registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(OpenScreen.PACKET_ID, OpenScreen::receive);
        if (HDiamond.config.enableContent) {
            ClientPlayNetworking.registerGlobalReceiver(UpdatePanel.PACKET_ID, UpdatePanel::receiveClient);
        }
    }
}
