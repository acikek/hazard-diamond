package com.acikek.hdiamond.client.render;

import com.acikek.hdiamond.HDiamond;
import com.acikek.hdiamond.client.HDiamondClient;
import com.acikek.hdiamond.core.HazardDiamond;
import com.acikek.hdiamond.core.quadrant.SpecificHazard;
import com.acikek.hdiamond.core.section.DiamondSection;
import com.acikek.hdiamond.entity.PanelEntity;
import net.fabricmc.fabric.api.client.model.BakedModelManagerHelper;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class PanelEntityRenderer extends EntityRenderer<PanelEntity> {

    public static final Identifier PANEL_MODEL = HDiamond.id("block/panel");
    public static final float ICON_RATIO = 64.0f;
    public static final float ICON_SCALE = 1.0f / ICON_RATIO;

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
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180 - entity.getYaw()));

        matrices.push();
        renderPanel(matrices, vertexConsumers, lightFront);
        matrices.pop();

        matrices.push();
        renderIcons(entity.getHazardData().diamond(), matrices, vertexConsumers, lightFront);
        matrices.pop();

        matrices.pop();
    }

    public void renderPanel(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int lightFront) {
        matrices.translate(0, 0, -0.5f + 1.0f / 32.0f);
        matrices.translate(-0.5f, -0.5f, -0.5f);

        modelRenderer.render(
                matrices.peek(), vertexConsumers.getBuffer(RenderLayer.getCutout()),
                null, BakedModelManagerHelper.getModel(modelManager, PANEL_MODEL),
                1.0f, 1.0f, 1.0f, lightFront, OverlayTexture.DEFAULT_UV
        );
    }

    public void renderIcon(VertexConsumer buffer, Matrix4f pos, Vector3f normal, DiamondSection<?> section, int x1, int y1, int light) {
        var texture = section.getTexture();
        int x2 = x1 + texture.width();
        int y2 = y1 + texture.height();
        float u1 = texture.u() / 256.0f;
        float v1 = texture.v() / 256.0f;
        float u2 = (texture.u() + texture.width()) / 256.0f;
        float v2 = (texture.v() + texture.height()) / 256.0f;

        float nx = normal.x();
        float ny = normal.y();
        float nz = normal.z(); // kiwi

        buffer.vertex(pos, x1, y1, 0.0f).color(0xFFFFFFFF).texture(u1, v1).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(nx, ny, nz).next();
        buffer.vertex(pos, x1, y2, 0.0f).color(0xFFFFFFFF).texture(u1, v2).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(nx, ny, nz).next();
        buffer.vertex(pos, x2, y2, 0.0f).color(0xFFFFFFFF).texture(u2, v2).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(nx, ny, nz).next();
        buffer.vertex(pos, x2, y1, 0.0f).color(0xFFFFFFFF).texture(u2, v1).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(nx, ny, nz).next();
    }

    public void renderIcons(HazardDiamond diamond, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int lightFront) {
        matrices.scale(-ICON_SCALE, -ICON_SCALE, -ICON_SCALE);
        matrices.translate(-ICON_RATIO / 2.0f, -ICON_RATIO / 2.0f, -0.75f);

        var entry = matrices.peek();
        Matrix4f pos = entry.getPositionMatrix();
        Matrix3f normal = entry.getNormalMatrix();
        Vector3f vec3f = normal.transform(new Vector3f(0, 1, 0));

        var buffer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(HDiamondClient.WIDGETS));
        renderIcon(buffer, pos, vec3f, diamond.fire().get(), 26, 9, lightFront);
        renderIcon(buffer, pos, vec3f, diamond.health().get(), 10, 25, lightFront);
        renderIcon(buffer, pos, vec3f, diamond.reactivity().get(), 42, 25, lightFront);
        SpecificHazard specific = diamond.specific().get();
        if (specific != SpecificHazard.NONE) {
            var rad = specific == SpecificHazard.RADIOACTIVE;
            renderIcon(buffer, pos, vec3f, specific, 23 - (rad ? 1 : 0), 42 - (rad ? 2 : 0), lightFront);
        }
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
