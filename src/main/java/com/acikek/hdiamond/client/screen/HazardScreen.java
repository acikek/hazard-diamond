package com.acikek.hdiamond.client.screen;

import com.acikek.hdiamond.HDiamond;
import com.acikek.hdiamond.core.HazardData;
import com.acikek.hdiamond.core.quadrant.QuadrantValue;
import com.acikek.hdiamond.core.section.DiamondSection;
import com.acikek.hdiamond.core.pictogram.Pictogram;
import com.acikek.hdiamond.core.quadrant.SpecificHazard;
import com.acikek.hdiamond.entity.PanelEntity;
import com.acikek.hdiamond.network.HDNetworking;
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

    public PanelEntity entity;
    public boolean isEditable;
    public HazardData data;

    HazardScreen(PanelEntity entity, boolean isEditable, HazardData data) {
        super(Text.translatable("gui.hdiamond.hazard_screen.title"));
        this.entity = entity;
        this.isEditable = isEditable;
        this.data = data;
    }

    public HazardScreen(PanelEntity entity) {
        this(entity, !entity.isWaxed(), entity.getHazardData().copy());
    }

    public HazardScreen(HazardData data) {
        this(null, false, data);
    }

    public void addQuadrant(QuadrantValue<?> quadrant, int halfX, int halfY) {
        addDrawableChild(new DiamondWidget.Quadrant(this, quadrant, halfX, halfY, 62));
    }

    public void addPictogram(Pictogram pictogram, int halfX, int halfY) {
        addDrawableChild(new DiamondWidget.PictogramLabel(this, pictogram, halfX, halfY, 66));
    }

    @Override
    protected void init() {
        this.x = (this.width - 128) / 4;
        this.y = (this.height) / 4 - 2;
        addQuadrant(data.diamond().fire(), x + 16, y - 50);
        addQuadrant(data.diamond().health(), x, y - 34);
        addQuadrant(data.diamond().reactivity(), x + 32, y - 34);
        addQuadrant(data.diamond().specific(), x + 16, y - 18);
        for (int i = 0; i < Pictogram.values().length; i++) {
            Pictogram pictogram = Pictogram.values()[i];
            addPictogram(pictogram, x - 57 + i * 18, y + 3 + (i % 2 == 0 ? 18 : 0));
        }
    }

    public static void setTexture() {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);
    }

    public void renderPanel(MatrixStack matrices) {
        drawTexture(matrices, x, y - 50, 0, 0, 64, 64);
    }

    public void renderElement(MatrixStack matrices, DiamondSection<?> element, int x, int y) {
        var texture = element.getTexture();
        drawTexture(matrices, x, y, texture.u(), texture.v(), texture.width(), texture.height());
    }

    public void renderQuadrants(MatrixStack matrices) {
        renderElement(matrices, data.diamond().fire().get(), x + 26, y - 41);
        renderElement(matrices, data.diamond().health().get(), x + 10, y - 25);
        renderElement(matrices, data.diamond().reactivity().get(), x + 42, y - 25);
        SpecificHazard specific = data.diamond().specific().get();
        if (specific != SpecificHazard.NONE) {
            var rad = specific == SpecificHazard.RADIOACTIVE;
            renderElement(matrices, specific, x + 23 - (rad ? 1 : 0), y - 9 - (rad ? 2 : 0));
        }
    }

    public void renderPictogram(MatrixStack matrices, Pictogram pictogram, int x, int y) {
        float color = data.pictograms().contains(pictogram) ? 1.0f : 0.5f;
        RenderSystem.setShaderColor(color, color, color, 1.0f);
        renderElement(matrices, pictogram, x, y);
    }

    public void renderPictograms(MatrixStack matrices) {
        for (int i = 0; i < Pictogram.values().length; i++) {
            Pictogram pictogram = Pictogram.values()[i];
            renderPictogram(matrices, pictogram, x - 56 + i * 18, y + 4 + (i % 2 == 0 ? 18 : 0));
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        setTexture();
        matrices.push();
        matrices.scale(2.0f, 2.0f, 1.0f);
        renderPanel(matrices);
        renderQuadrants(matrices);
        renderPictograms(matrices);
        matrices.pop();
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void close() {
        if (entity != null) {
            HDNetworking.c2sUpdatePanelData(entity, data);
        }
        super.close();
    }
}
