package com.acikek.hdiamond.core;

import com.acikek.hdiamond.core.pictogram.Pictogram;
import com.google.gson.JsonObject;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.JsonHelper;

import java.util.HashSet;
import java.util.Set;

public record HazardData(HazardDiamond diamond, Set<Pictogram> pictograms) {

    public static final TrackedDataHandler<HazardData> DATA_TRACKER = TrackedDataHandler.of(
            (buf, data) -> data.write(buf),
            HazardData::read
    );

    public static HazardData empty() {
        return new HazardData(HazardDiamond.empty(), new HashSet<>());
    }

    public static HazardData fromJson(JsonObject obj) {
        HazardDiamond diamond = HazardDiamond.fromJson(JsonHelper.getObject(obj, "diamond"));
        Set<Pictogram> pictograms = Pictogram.fromJson(obj);
        return new HazardData(diamond, pictograms);
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.put("Diamond", diamond.toNbt());
        Pictogram.writeNbt(nbt, pictograms);
        return nbt;
    }

    public static HazardData fromNbt(NbtCompound nbt) {
        var diamond = HazardDiamond.fromNbt(nbt.getCompound("Diamond"));
        var pictograms = Pictogram.readNbt(nbt);
        return new HazardData(diamond, pictograms);
    }

    public void write(PacketByteBuf buf) {
        diamond.write(buf);
        Pictogram.write(buf, pictograms);
    }

    public static HazardData read(PacketByteBuf buf) {
        var diamond = HazardDiamond.read(buf);
        var pictograms = Pictogram.read(buf);
        return new HazardData(diamond, pictograms);
    }

    public HazardData copy() {
        return new HazardData(diamond.copy(), new HashSet<>(pictograms));
    }

    public boolean isEmpty() {
        return diamond().isEmpty() && pictograms().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HazardData data = (HazardData) o;
        if (!diamond.equals(data.diamond)) {
            return false;
        }
        return pictograms.equals(data.pictograms);
    }

    @Override
    public int hashCode() {
        int result = diamond.hashCode();
        result = 31 * result + pictograms.hashCode();
        return result;
    }

    public static void register() {
        TrackedDataHandlerRegistry.register(DATA_TRACKER);
    }
}
