package com.acikek.hdiamond.core.section;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public interface QuadrantSection<E extends Enum<E>> extends DiamondSection<E> {

    Formatting getTypeColor();

    private MutableText getTypeName() {
        return getText("type").formatted(getTypeColor());
    }

    @Override
    default MutableText getTitle() {
        return getTypeName()
                .append(Text.literal(" - ").formatted(Formatting.GRAY))
                .append(DiamondSection.super.getTitle().formatted(getLevelColor()));
    }

    private Formatting getLevelColor() {
        return switch (getValue().ordinal()) {
            case 0, 1 -> Formatting.GREEN;
            case 2, 3 -> Formatting.YELLOW;
            case 4 -> Formatting.RED;
            default -> Formatting.WHITE;
        };
    }
}
