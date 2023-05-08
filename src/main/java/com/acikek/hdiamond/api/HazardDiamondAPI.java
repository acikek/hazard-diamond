package com.acikek.hdiamond.api;

import com.acikek.hdiamond.api.impl.HazardDiamondAPIImpl;
import com.acikek.hdiamond.client.screen.HazardScreen;
import com.acikek.hdiamond.core.HazardData;
import com.acikek.hdiamond.core.HazardDiamond;
import com.acikek.hdiamond.core.pictogram.Pictogram;
import com.acikek.hdiamond.core.quadrant.FireHazard;
import com.acikek.hdiamond.core.quadrant.HealthHazard;
import com.acikek.hdiamond.core.quadrant.Reactivity;
import com.acikek.hdiamond.core.quadrant.SpecificHazard;
import com.acikek.hdiamond.network.HDNetworking;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class HazardDiamondAPI {

    /**
     * @param id the ID of the hazard data object
     * @return whether the data object exists and is loaded from a data pack source
     */
    public static boolean hasData(Identifier id) {
        return HazardDiamondAPIImpl.getData(id) != null;
    }

    /**
     * Retrieves loaded hazard data from the {@code hazard_data} data pack source.
     * @param id the ID of the hazard data object
     * @return the data object, if any
     * @throws IllegalStateException if the data object does not exist
     * @see HazardDiamondAPI#hasData(Identifier) 
     */
    public static HazardData getData(Identifier id) {
        var result = HazardDiamondAPIImpl.getData(id);
        if (result == null) {
            throw new IllegalStateException("hazard data '" + id + "' does not exist");
        }
        return result;
    }

    /**
     * Opens an immutable {@link HazardScreen} on this client instance.
     * @param data the hazard data object to display
     */
    @Environment(EnvType.CLIENT)
    public static void open(HazardData data) {
        MinecraftClient.getInstance().setScreen(new HazardScreen(data));
    }

    /**
     * Opens an immutable {@link HazardScreen} on the specified players' clients.
     * @param players the players to target
     * @param data the hazard data object to display
     */
    public static void open(Collection<ServerPlayerEntity> players, HazardData data) {
        HDNetworking.s2cOpenScreen(players, data);
    }

    /**
     * @param diamond the four NFPA 704 quadrnats
     * @param pictograms a set of GHS pictograms
     * @return the hazard data object
     */
    public static HazardData data(HazardDiamond diamond, Set<Pictogram> pictograms) {
        return new HazardData(diamond, pictograms);
    }

    /**
     * @see HazardDiamondAPI#data(HazardDiamond, Set)
     */
    public static HazardData data(HazardDiamond diamond, Pictogram... pictograms) {
        return data(diamond, Arrays.stream(pictograms).collect(Collectors.toSet()));
    }

    /**
     * @see HazardDiamondAPI#data(HazardDiamond, Set)
     */
    public static HazardData data(FireHazard fire, HealthHazard health, Reactivity reactivity, SpecificHazard specific, Set<Pictogram> pictograms) {
        return data(new HazardDiamond(fire, health, reactivity, specific), pictograms);
    }

    /**
     * @see HazardDiamondAPI#data(HazardDiamond, Set)
     */
    public static HazardData data(FireHazard fire, HealthHazard health, Reactivity reactivity, SpecificHazard specific, Pictogram... pictograms) {
        return data(fire, health, reactivity, specific, Arrays.stream(pictograms).collect(Collectors.toSet()));
    }
}
