package com.acikek.hdiamond.core.quadrant;

import com.acikek.hdiamond.core.section.QuadrantSection;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Describes special hazards pertaining to a material.
 * Source: NFPA, <em>Fire Protection on Hazardous Materials</em>, Standard 704, 1990
 */
public enum SpecificHazard implements QuadrantSection<SpecificHazard> {
    /**
     * No specific hazard to denote.
     */
    NONE,
    /**
     * Reacts with water in an unusual or dangerous manner.
     */
    REACTS_WITH_WATER,
    /**
     * Allows chemicals to burn without air supply.
     */
    OXIDIZER,
    /**
     * Denotes a "simple asphyxiant gas," or one that alters the normal oxygen concentration in breathable air.
     */
    SIMPLE_ASPHYXIANT,
    /**
     * Decays radioactively.
     */
    RADIOACTIVE;

    private Texture texture = null;

    @Override
    public SpecificHazard getValue() {
        return this;
    }

    @Override
    public Texture getTexture() {
        if (texture == null) {
            texture = this == SpecificHazard.RADIOACTIVE
                    ? new Texture(256 - 20, 256, 20, 20)
                    : new Texture(64 + (ordinal() - 1) * 18, 42, 18, 14);
        }
        return texture;
    }

    @Override
    public String getType() {
        return "quadrant.hdiamond.specific";
    }

    @Override
    public Formatting getTypeColor() {
        return Formatting.WHITE;
    }

    @Override
    public MutableText getSymbol() {
        var text = switch (this) {
            case NONE -> Text.empty();
            case REACTS_WITH_WATER -> Text.literal("W").formatted(Formatting.STRIKETHROUGH);
            case OXIDIZER -> Text.literal("OX");
            case SIMPLE_ASPHYXIANT -> Text.literal("SA");
            case RADIOACTIVE -> Text.literal("R");
        };
        return text.formatted(Formatting.WHITE);
    }

    @Override
    public Formatting getLevelColor() {
        return switch (this) {
            case NONE -> Formatting.GRAY;
            case REACTS_WITH_WATER -> Formatting.DARK_AQUA;
            case OXIDIZER -> Formatting.RED;
            case SIMPLE_ASPHYXIANT -> Formatting.GOLD;
            case RADIOACTIVE -> Formatting.DARK_GREEN;
        };
    }
}
