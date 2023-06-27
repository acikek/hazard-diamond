package com.acikek.hdiamond.client.screen;

import com.acikek.hdiamond.core.pictogram.Pictogram;
import com.acikek.hdiamond.core.quadrant.QuadrantValue;
import com.acikek.hdiamond.core.section.DiamondSection;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class DiamondWidget extends ButtonWidget {

    public HazardScreen screen;
    public Polygon diamond;

    public boolean wasHovered;

    public DiamondWidget(HazardScreen screen, int x, int y, int width, int height, Text message, PressAction action) {
        super(x, y, width, height, message, action, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
        this.screen = screen;
        this.diamond = new Polygon(
                new int[] { x, x + width / 2, x + width, x + width / 2 },
                new int[] { y + height / 2, y + height, y + height / 2, y },
                4
        );
    }

    public DiamondWidget(HazardScreen screen, int halfX, int halfY, int size, Text message, PressAction action) {
        this(screen, (halfX * 2) + 1, (halfY * 2) + 1, size, size, message, action);
    }

    public void renderTooltip(DrawContext context, int x, int y) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        hovered = isMouseOver(mouseX, mouseY);
        if (isFocused()) {
            setActive(screen.nav);
            if (screen.mouse.isEmpty()) {
                renderTooltip(context, 10, 30);
            }
        }
        if (isHovered()) {
            if (screen.holder.isEditable()) {
                setActive(screen.mouse);
            }
            if (screen.movedCursor) {
                renderTooltip(context, mouseX, mouseY);
            }
            wasHovered = true;
        }
        else if (wasHovered) {
            setInactive(screen.mouse);
            setInactive(screen.nav);
            setFocused(false);
            wasHovered = false;
        }
    }

    public void renderDiamondTooltip(DiamondSection<?> section, DrawContext context, int mouseX, int mouseY, boolean off) {
        List<Text> tooltip = new ArrayList<>();
        tooltip.add(section.getTitle().styled(style -> off ? style.withFormatting(Formatting.GRAY) : style));
        tooltip.add(section.getDescription().formatted(off ? Formatting.DARK_GRAY : Formatting.GRAY));
        context.drawTooltip(MinecraftClient.getInstance().textRenderer, tooltip, mouseX, mouseY);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return diamond.contains(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (hovered) {
            super.onClick(mouseX, mouseY);
        }
        return hovered;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        // Empty
    }

    public static void playSound() {
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
    }

    public abstract void setActive(HazardScreen.ActiveSection section);

    public abstract void setInactive(HazardScreen.ActiveSection section);

    public static class Quadrant extends DiamondWidget {

        public QuadrantValue<?> quadrant;

        public Quadrant(HazardScreen screen, QuadrantValue<?> quadrant, int halfX, int halfY, int size) {
            super(screen, halfX, halfY, size, quadrant.get().getTitle(), button -> {
                if (screen.holder.isEditable()) {
                    quadrant.scroll();
                    playSound();
                }
            });
            this.quadrant = quadrant;
        }

        @Override
        public void renderTooltip(DrawContext context, int x, int y) {
            super.renderTooltip(context, x, y);
            renderDiamondTooltip(quadrant.get(), context, x, y, false);
        }

        @Override
        public void setActive(HazardScreen.ActiveSection section) {
            section.setQuadrant(quadrant);
        }

        @Override
        public void setInactive(HazardScreen.ActiveSection section) {
            section.setQuadrant(null);
        }
    }

    public static class PictogramLabel extends DiamondWidget {

        public Pictogram pictogram;

        public PictogramLabel(HazardScreen screen, Pictogram pictogram, int halfX, int halfY, int size) {
            super(screen, halfX, halfY, size, pictogram.getTitle(), button -> {
                if (!screen.holder.isEditable()) {
                    return;
                }
                var pictograms = screen.data.pictograms();
                if (pictograms.contains(pictogram)) {
                    pictograms.remove(pictogram);
                }
                else {
                    pictograms.add(pictogram);
                }
                playSound();
            });
            this.pictogram = pictogram;
        }

        @Override
        public void renderTooltip(DrawContext context, int x, int y) {
            super.renderTooltip(context, x, y);
            renderDiamondTooltip(pictogram, context, x, y, !screen.data.pictograms().contains(pictogram));
        }

        @Override
        public void setActive(HazardScreen.ActiveSection section) {
            section.setPictogram(pictogram);
        }

        @Override
        public void setInactive(HazardScreen.ActiveSection section) {
            section.setPictogram(null);
        }
    }
}
