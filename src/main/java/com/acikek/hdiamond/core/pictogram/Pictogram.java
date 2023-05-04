package com.acikek.hdiamond.core.pictogram;

import com.acikek.hdiamond.core.section.DiamondSection;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.EnumUtils;

import java.util.*;
import java.util.stream.Collectors;

public enum Pictogram implements DiamondSection<Pictogram> {

    EXPLOSIVE,
    FLAMMABLE,
    OXIDIZING,
    COMPRESSED_GAS,
    CORROSIVE,
    TOXIC,
    HARMFUL,
    HEALTH_HAZARD,
    ENVIRONMENTAL_HAZARD;

    private final Texture texture = new Texture((ordinal() * 32) % 160, 64 + (ordinal() >= 5 ? 32 : 0), 32, 32);

    @Override
    public Pictogram getValue() {
        return this;
    }

    @Override
    public Texture getTexture() {
        return texture;
    }

    @Override
    public String getType() {
        return "ghs.hdiamond";
    }

    public static Set<Pictogram> fromJson(JsonObject obj) {
        Set<Pictogram> result = new HashSet<>();
        for (var pictogram : JsonHelper.getArray(obj, "pictograms")) {
            String str = pictogram.getAsString();
            Pictogram p = EnumUtils.getEnumIgnoreCase(Pictogram.class, str);
            if (p == null) {
                throw new JsonParseException("unrecognized pictogram '" + str + "'");
            }
            result.add(p);
        }
        return result;
    }

    public static void writeNbt(NbtCompound nbt, Set<Pictogram> pictograms) {
        var ints = pictograms.stream()
                .map(Enum::ordinal)
                .toList();
        nbt.putIntArray("Pictograms", ints);
    }

    public static Set<Pictogram> readNbt(NbtCompound nbt) {
        return Arrays.stream(nbt.getIntArray("Pictograms"))
                .mapToObj(index -> Pictogram.values()[index])
                .collect(Collectors.toSet());
    }

    public static void write(PacketByteBuf buf, Set<Pictogram> pictograms) {
        buf.writeCollection(pictograms, PacketByteBuf::writeEnumConstant);
    }

    public static Set<Pictogram> read(PacketByteBuf buf) {
        return buf.readCollection(HashSet::new, b -> b.readEnumConstant(Pictogram.class));
    }
}
