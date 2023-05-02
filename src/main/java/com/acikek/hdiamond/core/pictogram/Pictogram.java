package com.acikek.hdiamond.core.pictogram;

import com.acikek.hdiamond.core.TexturedElement;

public enum Pictogram implements TexturedElement {
    EXPLOSIVE,
    FLAMMABLE,
    OXIDIZING,
    COMPRESSED_GAS,
    CORROSIVE,
    TOXIC,
    HARMFUL,
    HEALTH_HAZARD,
    ENVIRONMENTAL_HAZARD;

    @Override
    public TexturedElement.Result getTexture() {
        return new TexturedElement.Result((ordinal() * 32) % 160, 64 + (ordinal() >= 5 ? 32 : 0), 32, 32);
    }
}
