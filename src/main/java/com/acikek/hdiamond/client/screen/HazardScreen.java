package com.acikek.hdiamond.client.screen;

import com.acikek.hdiamond.api.event.HazardScreenEdited;
import com.acikek.hdiamond.client.HDiamondClient;
import com.acikek.hdiamond.core.HazardData;
import com.acikek.hdiamond.core.pictogram.Pictogram;
import com.acikek.hdiamond.core.quadrant.QuadrantValue;
import com.acikek.hdiamond.core.quadrant.SpecificHazard;
import com.acikek.hdiamond.core.section.DiamondSection;
import com.acikek.hdiamond.entity.PanelEntity;
import com.acikek.hdiamond.network.HDNetworking;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class HazardScreen extends Screen {

    public int x;
    public int y;
    public boolean movedCursor = false;

    public PanelEntity entity;
    public boolean isEditable;
    public HazardData originalData;
    public Identifier id;
    public HazardData data;

    HazardScreen(PanelEntity entity, boolean isEditable, HazardData originalData, Identifier id, HazardData data) {
        super(Text.translatable("gui.hdiamond.hazard_screen.title"));
        this.entity = entity;
        this.isEditable = isEditable;
        this.originalData = originalData;
        this.id = id;
        this.data = data;
    }

    public HazardScreen(PanelEntity entity) {
        this(entity, !entity.isWaxed(), null, null, entity.getHazardData().copy());
    }

    public HazardScreen(HazardData data) {
        this(null, false, null, null, data);
    }

    public HazardScreen(HazardData data, Identifier id) {
        this(null, true, data.copy(), id, data);
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

    public void renderPanel(DrawContext context) {
        context.drawTexture(HDiamondClient.WIDGETS, x, y - 50, 0, 0, 64, 64);
    }

    public void renderElement(DrawContext context, DiamondSection<?> element, int x, int y) {
        var texture = element.getTexture();
        context.drawTexture(HDiamondClient.WIDGETS, x, y, texture.u(), texture.v(), texture.width(), texture.height());
    }

    public void renderQuadrants(DrawContext context) {
        renderElement(context, data.diamond().fire().get(), x + 26, y - 41);
        renderElement(context, data.diamond().health().get(), x + 10, y - 25);
        renderElement(context, data.diamond().reactivity().get(), x + 42, y - 25);
        SpecificHazard specific = data.diamond().specific().get();
        if (specific != SpecificHazard.NONE) {
            var rad = specific == SpecificHazard.RADIOACTIVE;
            renderElement(context, specific, x + 23 - (rad ? 1 : 0), y - 9 - (rad ? 2 : 0));
        }
    }

    public void renderPictogram(DrawContext context, Pictogram pictogram, int x, int y) {
        float color = data.pictograms().contains(pictogram) ? 1.0f : 0.5f;
        RenderSystem.setShaderColor(color, color, color, 1.0f);
        renderElement(context, pictogram, x, y);
    }

    public void renderPictograms(DrawContext context) {
        for (int i = 0; i < Pictogram.values().length; i++) {
            Pictogram pictogram = Pictogram.values()[i];
            renderPictogram(context, pictogram, x - 56 + i * 18, y + 4 + (i % 2 == 0 ? 18 : 0));
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        context.getMatrices().push();
        context.getMatrices().scale(2.0f, 2.0f, 1.0f);
        renderPanel(context);
        renderQuadrants(context);
        renderPictograms(context);
        context.getMatrices().pop();
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
        if (!movedCursor) {
            movedCursor = true;
        }
    }

    @Override
    public void close() {
        if (entity != null) {
            HDNetworking.c2sUpdatePanelData(entity, data);
        }
        else if (originalData != null) {
            HazardScreenEdited.EVENT.invoker().onEdit(MinecraftClient.getInstance().player, originalData, data, id);
        }
        super.close();
    }
}
