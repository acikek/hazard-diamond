package com.acikek.hdiamond.core.section;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public interface DiamondSection<E extends Enum<E>> {

    record Texture(int u, int v, int width, int height) {

        public static Texture numeral(int row, int index) {
            return new Texture(64 + index * 12, 14 * row, 12, 14);
        }
    }

    E getValue();

    Texture getTexture();

    String getType();

    default MutableText getText(String suffix) {
        return Text.translatable(getType() + "." + suffix);
    }

    default MutableText getSpecificText(String suffix) {
        return getText(getValue().name().toLowerCase() + "." + suffix);
    }

    default MutableText getTitle() {
        return getSpecificText("name");
    }

    default MutableText getDescription() {
        return getSpecificText("description");
    }
}
