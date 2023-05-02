package com.acikek.hdiamond.core.quadrant;

/**
 * Describes the type of possible injury while handling materials.<br>
 * Source: NFPA, <em>Fire Protection on Hazardous Materials</em>, Standard 704, 1990
 */
public enum HealthHazard implements HazardQuadrant {
    /**
     * On exposure under fire conditions, offers no hazard beyond that of ordinary combustible materials.
     */
    NORMAL,
    /**
     * On exposure, causes irritation but only minor residual injury.
     */
    MINOR,
    /**
     * On intense or continued but not chronic exposure, can cause temporary incapacitation or possible residual injury.
     */
    HAZARDOUS,
    /**
     * On short exposure, can cause serious temporary or residual injury.
     */
    EXTREME,
    /**
     * On a very short exposure, can cause death or major residual injury.
     */
    DEADLY;

    @Override
    public Placement getPlacement() {
        return Placement.numeral(1, ordinal());
    }
}
