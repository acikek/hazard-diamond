package com.acikek.hdiamond.core.quadrant;

import com.acikek.hdiamond.core.section.QuadrantSection;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;

/**
 * Describes the susceptibility of materials to burn.<br>
 * Source: NFPA, <em>Fire Protection on Hazardous Materials</em>, Standard 704, 1990
 */
public enum FireHazard implements QuadrantSection<FireHazard> {
    /**
     * Will not burn.
     */
    NONFLAMMABLE,
    /**
     * Must be preheated before ignition can occur.
     * This is defined as burning only above 93 degrees Celsius.
     */
    ABOVE_93C,
    /**
     * Must be moderately heated or exposed to relatively high ambient temperatures before ignition can occur.
     * This is defined as burning below 93 degrees Celsius.
     */
    BELOW_93C,
    /**
     * Can be ignited under <b>almost all</b> ambient temperature conditions.
     * This is defined as burning below 37 degrees Celsius.
     */
    BELOW_37C,
    /**
     * Rapidly or completely vaporizes at atmospheric pressure and normal ambient temperature
     * or readily disperses and air and burns readily.
     * This is defined as burning below 25 degrees Celsius.
     */
    BELOW_25C;

    private final Texture texture = Texture.numeral(0, ordinal());

    @Override
    public FireHazard getValue() {
        return this;
    }

    @Override
    public Texture getTexture() {
        return texture;
    }

    @Override
    public String getType() {
        return "quadrant.hdiamond.fire";
    }

    @Override
    public Formatting getTypeColor() {
        return Formatting.RED;
    }

    @Override
    public Pair<Integer, Integer> getScreenOffsets() {
        return new Pair<>(16, -50);
    }
}
