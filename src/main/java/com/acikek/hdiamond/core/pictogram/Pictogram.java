package com.acikek.hdiamond.core.pictogram;

import com.acikek.hdiamond.core.TexturedElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.EnumUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public static List<Pictogram> fromJson(JsonObject obj) {
        List<Pictogram> result = new ArrayList<>();
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

    public static void writeNbt(NbtCompound nbt, List<Pictogram> pictograms) {
        var ints = pictograms.stream()
                .map(Enum::ordinal)
                .toList();
        nbt.putIntArray("Pictograms", ints);
    }

    public static List<Pictogram> readNbt(NbtCompound nbt) {
        return Arrays.stream(nbt.getIntArray("Pictograms"))
                .mapToObj(index -> Pictogram.values()[index])
                .toList();
    }

    public static void write(PacketByteBuf buf, List<Pictogram> pictograms) {
        buf.writeCollection(pictograms, PacketByteBuf::writeEnumConstant);
    }

    public static List<Pictogram> read(PacketByteBuf buf) {
        return buf.readCollection(ArrayList::new, b -> b.readEnumConstant(Pictogram.class));
    }
}
