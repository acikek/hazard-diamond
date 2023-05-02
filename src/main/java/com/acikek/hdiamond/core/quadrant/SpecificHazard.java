package com.acikek.hdiamond.core.quadrant;

/**
 * Describes special hazards pertaining to a material.
 * Source: NFPA, <em>Fire Protection on Hazardous Materials</em>, Standard 704, 1990
 */
public enum SpecificHazard implements HazardQuadrant {
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

    @Override
    public Texture getTexture() {
        return this == RADIOACTIVE
                ? new Texture(256 - 20, 256, 20, 20)
                : new Texture(64 + ordinal() * 18, 42, 18, 14);
    }
}
