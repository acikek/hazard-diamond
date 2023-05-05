package com.acikek.hdiamond.client.screen;

import com.acikek.hdiamond.core.pictogram.Pictogram;
import com.acikek.hdiamond.core.quadrant.QuadrantValue;
import com.acikek.hdiamond.core.section.DiamondSection;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class DiamondWidget extends ButtonWidget {

    public HazardScreen screen;
    public Polygon diamond;

    public DiamondWidget(HazardScreen screen, int x, int y, int width, int height, Text message, PressAction action) {
        super(x, y, width, height, message, action);
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

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        hovered = isMouseOver(mouseX, mouseY);
        if (hovered) {
            renderTooltip(matrices, mouseX, mouseY);
        }
    }

    public void renderDiamondTooltip(DiamondSection<?> section, MatrixStack matrices, int mouseX, int mouseY, boolean off) {
        List<Text> tooltip = new ArrayList<>();
        tooltip.add(section.getTitle().styled(style -> off ? style.withFormatting(Formatting.GRAY) : style));
        tooltip.add(section.getDescription().formatted(off ? Formatting.DARK_GRAY : Formatting.GRAY));
        screen.renderTooltip(matrices, tooltip, mouseX, mouseY);
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

    public static class Quadrant extends DiamondWidget {

        public QuadrantValue<?> quadrant;

        public Quadrant(HazardScreen screen, QuadrantValue<?> quadrant, int halfX, int halfY, int size) {
            super(screen, halfX, halfY, size, quadrant.get().getTitle(), button -> {
                if (screen.isEditable) {
                    quadrant.scroll();
                }
            });
            this.quadrant = quadrant;
        }

        @Override
        public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY) {
            renderDiamondTooltip(quadrant.get(), matrices, mouseX, mouseY, false);
        }
    }

    public static class PictogramLabel extends DiamondWidget {

        public Pictogram pictogram;

        public PictogramLabel(HazardScreen screen, Pictogram pictogram, int halfX, int halfY, int size) {
            super(screen, halfX, halfY, size, pictogram.getTitle(), button -> {
                if (!screen.isEditable) {
                    return;
                }
                var pictograms = screen.data.pictograms();
                if (pictograms.contains(pictogram)) {
                    pictograms.remove(pictogram);
                }
                else {
                    pictograms.add(pictogram);
                }
            });
            this.pictogram = pictogram;
        }

        @Override
        public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY) {
            renderDiamondTooltip(pictogram, matrices, mouseX, mouseY, !screen.data.pictograms().contains(pictogram));
        }
    }
}
