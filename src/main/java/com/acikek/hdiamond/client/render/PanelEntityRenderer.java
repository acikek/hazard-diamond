package com.acikek.hdiamond.client.render;

import com.acikek.hdiamond.HDiamond;
import com.acikek.hdiamond.entity.PanelEntity;
import net.fabricmc.fabric.api.client.model.BakedModelManagerHelper;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class PanelEntityRenderer extends EntityRenderer<PanelEntity> {

    public static final Identifier PANEL_MODEL = HDiamond.id("block/panel");

    public BlockModelRenderer modelRenderer;
    public BakedModelManager modelManager;

    protected PanelEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        modelRenderer = ctx.getBlockRenderManager().getModelRenderer();
        modelManager = ctx.getBlockRenderManager().getModels().getModelManager();
    }

    @Override
    public void render(PanelEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);

        if (entity.age == 0) {
            return;
        }

        int lightFront = WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getBlockPos());

        matrices.push();
        //matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(entity.getPitch()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180 - entity.getYaw()));
        matrices.translate(0, 0, -0.5f + 1.0f / 32.0f);
        matrices.translate(-0.5f, -0.5f, -0.5f);

        modelRenderer.render(
                matrices.peek(), vertexConsumers.getBuffer(RenderLayer.getCutout()),
                null, BakedModelManagerHelper.getModel(modelManager, PANEL_MODEL),
                1.0f, 1.0f, 1.0f, lightFront, OverlayTexture.DEFAULT_UV
        );

        matrices.pop();
    }

    @Override
    public Identifier getTexture(PanelEntity entity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }

    public static void register() {
        EntityRendererRegistry.register(PanelEntity.ENTITY_TYPE, PanelEntityRenderer::new);
        ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> out.accept(PANEL_MODEL));
    }
}
