package com.acikek.hdiamond.client.screen;

import com.acikek.hdiamond.api.util.HazardDataHolder;
import com.acikek.hdiamond.client.HDiamondClient;
import com.acikek.hdiamond.core.HazardData;
import com.acikek.hdiamond.core.pictogram.Pictogram;
import com.acikek.hdiamond.core.quadrant.QuadrantValue;
import com.acikek.hdiamond.core.quadrant.SpecificHazard;
import com.acikek.hdiamond.core.section.DiamondSection;
import com.acikek.hdiamond.core.section.QuadrantSection;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.Objects;

public class HazardScreen extends Screen {

    public int x;
    public int y;
    public boolean movedCursor = false;

    public HazardDataHolder holder;
    public HazardData data;

    public DiamondSection<?> activeSection;

    public HazardScreen(HazardDataHolder holder) {
        super(Text.translatable("gui.hdiamond.hazard_screen.title"));
        this.holder = holder;
        Objects.requireNonNull(holder.getHazardData());
        data = holder.getHazardData().copy();
    }

    public void addQuadrant(QuadrantValue<?> quadrant) {
        var offsets = quadrant.get().getScreenOffsets();
        addDrawableChild(new DiamondWidget.Quadrant(this, quadrant, x + offsets.getLeft(), y + offsets.getRight(), 62));
    }

    public void addPictogram(Pictogram pictogram, int halfX, int halfY) {
        addDrawableChild(new DiamondWidget.PictogramLabel(this, pictogram, halfX, halfY, 66));
    }

    @Override
    protected void init() {
        this.x = (this.width - 128) / 4;
        this.y = (this.height) / 4 - 2;
        addQuadrant(data.diamond().fire());
        addQuadrant(data.diamond().health());
        addQuadrant(data.diamond().reactivity());
        addQuadrant(data.diamond().specific());
        for (int i = 0; i < Pictogram.values().length; i++) {
            Pictogram pictogram = Pictogram.values()[i];
            addPictogram(pictogram, x - 57 + i * 18, y + 3 + (i % 2 == 0 ? 18 : 0));
        }
    }

    public void renderPanel(DrawContext context) {
        context.drawTexture(HDiamondClient.WIDGETS, x, y - 50, 0, 0, 64, 64);
    }

    public void renderSelection(DrawContext context, int x, int y) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        context.drawTexture(HDiamondClient.WIDGETS, x, y, 0, 128, 32, 32);
    }

    public void renderElement(DrawContext context, DiamondSection<?> element, int x, int y) {
        var texture = element.getTexture();
        context.drawTexture(HDiamondClient.WIDGETS, x, y, texture.u(), texture.v(), texture.width(), texture.height());
    }

    public void renderQuadrant(DrawContext context, QuadrantSection<?> quadrant, int x, int y, boolean inside) {
        if (inside) {
            renderElement(context, quadrant, x, y);
        }
        if (quadrant == activeSection) {
            var offsets = quadrant.getScreenOffsets();
            renderSelection(context, this.x + offsets.getLeft(), this.y + offsets.getRight());
        }
    }

    public void renderQuadrants(DrawContext context) {
        renderQuadrant(context, data.diamond().fire().get(), x + 26, y - 41, true);
        renderQuadrant(context, data.diamond().health().get(), x + 10, y - 25, true);
        renderQuadrant(context, data.diamond().reactivity().get(), x + 42, y - 25, true);
        SpecificHazard specific = data.diamond().specific().get();
        var rad = specific == SpecificHazard.RADIOACTIVE;
        renderQuadrant(context, specific, x + 23 - (rad ? 1 : 0), y - 9 - (rad ? 2 : 0), specific != SpecificHazard.NONE);
    }

    public void renderPictogram(DrawContext context, Pictogram pictogram, int x, int y) {
        float color = data.pictograms().contains(pictogram) ? 1.0f : 0.5f;
        RenderSystem.setShaderColor(color, color, color, 1.0f);
        renderElement(context, pictogram, x, y);
        if (pictogram == activeSection) {
            renderSelection(context, x, y);
        }
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
        if (holder.isEditable()) {
            holder.updateHazardData(data);
        }
        super.close();
    }
}
