package com.acikek.hdiamond.client.screen;

import com.acikek.hdiamond.core.TexturedElement;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class QuadrantWidget extends ButtonWidget {

    public HazardScreen screen;
    public TexturedElement quadrant;

    public QuadrantWidget(int x, int y, int width, int height, Text message, PressAction onPress) {
        super(x, y, width, height, message, onPress);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        HazardScreen.setTexture();

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY) {

    }
}
