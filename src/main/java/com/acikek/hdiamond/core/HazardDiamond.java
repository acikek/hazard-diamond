package com.acikek.hdiamond.core;

import com.acikek.hdiamond.core.quadrant.*;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

public record HazardDiamond(
        HazardQuadrant<FireHazard> fire,
        HazardQuadrant<HealthHazard> health,
        HazardQuadrant<Reactivity> reactivity,
        HazardQuadrant<SpecificHazard> specific) {

    public static HazardDiamond empty() {
        return new HazardDiamond(FireHazard.INFLAMMABLE, HealthHazard.NORMAL, Reactivity.STABLE, SpecificHazard.NONE);
    }

    public HazardDiamond(FireHazard fire, HealthHazard health, Reactivity reactivity, SpecificHazard specific) {
        this(
                new HazardQuadrant<>(FireHazard.class, fire),
                new HazardQuadrant<>(HealthHazard.class, health),
                new HazardQuadrant<>(Reactivity.class, reactivity),
                new HazardQuadrant<>(SpecificHazard.class, specific)
        );
    }

    public static HazardDiamond fromJson(JsonObject obj) {
        var fire = TexturedElement.quadrantsFromJson(obj.get("fire"), FireHazard.class);
        var health = TexturedElement.quadrantsFromJson(obj.get("health"), HealthHazard.class);
        var reactivity = TexturedElement.quadrantsFromJson(obj.get("reactivity"), Reactivity.class);
        var specific = obj.has("specific")
                ? TexturedElement.quadrantsFromJson(obj.get("specific"), SpecificHazard.class)
                : SpecificHazard.NONE;
        return new HazardDiamond(fire, health, reactivity, specific);
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putInt("Fire", fire.get().ordinal());
        nbt.putInt("Health", health.get().ordinal());
        nbt.putInt("Reactivity", reactivity.get().ordinal());
        nbt.putInt("Specific", specific.get().ordinal());
        return nbt;
    }

    public static HazardDiamond fromNbt(NbtCompound nbt) {
        var fire = FireHazard.values()[nbt.getInt("Fire")];
        var health = HealthHazard.values()[nbt.getInt("Health")];
        var reactivity = Reactivity.values()[nbt.getInt("Reactivity")];
        var specific = SpecificHazard.values()[nbt.getInt("Specific")];
        return new HazardDiamond(fire, health, reactivity, specific);
    }

    public void write(PacketByteBuf buf) {
        buf.writeEnumConstant(fire.get());
        buf.writeEnumConstant(health.get());
        buf.writeEnumConstant(reactivity.get());
        buf.writeEnumConstant(specific.get());
    }

    public static HazardDiamond read(PacketByteBuf buf) {
        var fire = buf.readEnumConstant(FireHazard.class);
        var health = buf.readEnumConstant(HealthHazard.class);
        var reactivity = buf.readEnumConstant(Reactivity.class);
        var specific = buf.readEnumConstant(SpecificHazard.class);
        return new HazardDiamond(fire, health, reactivity, specific);
    }

    public HazardDiamond copy() {
        return new HazardDiamond(fire.copy(), health.copy(), reactivity.copy(), specific.copy());
    }
}
