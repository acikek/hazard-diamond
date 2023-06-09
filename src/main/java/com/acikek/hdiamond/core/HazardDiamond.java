package com.acikek.hdiamond.core;

import com.acikek.hdiamond.core.quadrant.*;
import com.acikek.hdiamond.core.section.QuadrantSection;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

public record HazardDiamond(
        QuadrantValue<FireHazard> fire,
        QuadrantValue<HealthHazard> health,
        QuadrantValue<Reactivity> reactivity,
        QuadrantValue<SpecificHazard> specific) {

    public static HazardDiamond empty() {
        return new HazardDiamond(FireHazard.NONFLAMMABLE, HealthHazard.NORMAL, Reactivity.STABLE, SpecificHazard.NONE);
    }

    public HazardDiamond(FireHazard fire, HealthHazard health, Reactivity reactivity, SpecificHazard specific) {
        this(
                new QuadrantValue<>(FireHazard.class, fire),
                new QuadrantValue<>(HealthHazard.class, health),
                new QuadrantValue<>(Reactivity.class, reactivity),
                new QuadrantValue<>(SpecificHazard.class, specific)
        );
    }

    public static HazardDiamond fromJson(JsonObject obj) {
        var fire = QuadrantSection.fromJson(obj.get("fire"), FireHazard.class);
        var health = QuadrantSection.fromJson(obj.get("health"), HealthHazard.class);
        var reactivity = QuadrantSection.fromJson(obj.get("reactivity"), Reactivity.class);
        var specific = obj.has("specific")
                ? QuadrantSection.fromJson(obj.get("specific"), SpecificHazard.class)
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

    public boolean isEmpty() {
        return fire.isEmpty() && health().isEmpty() && reactivity().isEmpty() && specific.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HazardDiamond that = (HazardDiamond) o;
        if (!fire.equals(that.fire)) {
            return false;
        }
        if (!health.equals(that.health)) {
            return false;
        }
        if (!reactivity.equals(that.reactivity)) {
            return false;
        }
        return specific.equals(that.specific);
    }

    @Override
    public int hashCode() {
        int result = fire.hashCode();
        result = 31 * result + health.hashCode();
        result = 31 * result + reactivity.hashCode();
        result = 31 * result + specific.hashCode();
        return result;
    }
}
