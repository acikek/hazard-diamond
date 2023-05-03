package com.acikek.hdiamond.core;

import com.acikek.hdiamond.HDiamond;
import com.acikek.hdiamond.core.quadrant.*;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.Optional;

public record HazardDiamond(FireHazard fire, HealthHazard health, Reactivity reactivity, Optional<SpecificHazard> specific) {

    public static HazardDiamond empty() {
        return new HazardDiamond(FireHazard.INFLAMMABLE, HealthHazard.NORMAL, Reactivity.STABLE, Optional.empty());
    }

    public static HazardDiamond fromJson(JsonObject obj) {
        var fire = TexturedElement.quadrantsFromJson(obj.get("fire"), FireHazard.class);
        var health = TexturedElement.quadrantsFromJson(obj.get("health"), HealthHazard.class);
        var reactivity = TexturedElement.quadrantsFromJson(obj.get("reactivity"), Reactivity.class);
        Optional<SpecificHazard> specific = obj.has("specific")
                ? Optional.of(TexturedElement.quadrantsFromJson(obj.get("specific"), SpecificHazard.class))
                : Optional.empty();
        return new HazardDiamond(fire, health, reactivity, specific);
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putInt("Fire", fire.ordinal());
        nbt.putInt("Health", health.ordinal());
        nbt.putInt("Reactivity", reactivity.ordinal());
        specific.ifPresent(s -> nbt.putInt("Specific", s.ordinal()));
        return nbt;
    }

    public static HazardDiamond fromNbt(NbtCompound nbt) {
        var fire = FireHazard.values()[nbt.getInt("Fire")];
        var health = HealthHazard.values()[nbt.getInt("Health")];
        var reactivity = Reactivity.values()[nbt.getInt("Reactivity")];
        Optional<SpecificHazard> specific = nbt.contains("Specific")
                ? Optional.of(SpecificHazard.values()[nbt.getInt("Specific")])
                : Optional.empty();
        return new HazardDiamond(fire, health, reactivity, specific);
    }

    public void write(PacketByteBuf buf) {
        buf.writeEnumConstant(fire);
        buf.writeEnumConstant(health);
        buf.writeEnumConstant(reactivity);
        buf.writeOptional(specific, PacketByteBuf::writeEnumConstant);
    }

    public static HazardDiamond read(PacketByteBuf buf) {
        var fire = buf.readEnumConstant(FireHazard.class);
        var health = buf.readEnumConstant(HealthHazard.class);
        var reactivity = buf.readEnumConstant(Reactivity.class);
        var specific = buf.readOptional(b -> b.readEnumConstant(SpecificHazard.class));
        return new HazardDiamond(fire, health, reactivity, specific);
    }

}
