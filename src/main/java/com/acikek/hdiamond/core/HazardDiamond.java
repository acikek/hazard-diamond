package com.acikek.hdiamond.core;

import com.acikek.hdiamond.HDiamond;
import com.acikek.hdiamond.core.quadrant.*;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

public record HazardDiamond(FireHazard fire, HealthHazard health, Reactivity reactivity, SpecificHazard specific) {

    public static final Identifier PANEL = HDiamond.id("textures/hazard/diamond/panel.png");

    public static HazardDiamond fromJson(JsonObject obj) {
        var fire = HazardQuadrant.fromJson(obj.get("fire"), FireHazard.class);
        var health = HazardQuadrant.fromJson(obj.get("health"), HealthHazard.class);
        var reactivity = HazardQuadrant.fromJson(obj.get("reactivity"), Reactivity.class);
        var specific = HazardQuadrant.fromJson(obj.get("specific"), SpecificHazard.class);
        return new HazardDiamond(fire, health, reactivity, specific);
    }
}
