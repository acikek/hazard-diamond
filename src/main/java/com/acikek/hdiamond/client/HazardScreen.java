package com.acikek.hdiamond.client;

import com.acikek.hdiamond.HDiamond;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class HazardScreen extends Screen {

    public static final Identifier TEXTURE = HDiamond.id("textures/gui/hazards.png");

    public int x;
    public int y;

    protected HazardScreen(Text title) {
        super(title);
    }

    @Override
    protected void init() {
        this.x = (this.width - 128) / 4;
        this.y = (this.height) / 4;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        matrices.scale(2.0f, 2.0f, 1.0f);
        drawTexture(matrices, x, y - 50, 0, 0, 64, 64);
    }
}
