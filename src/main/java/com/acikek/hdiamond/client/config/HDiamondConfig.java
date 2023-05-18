package com.acikek.hdiamond.client.config;

import com.acikek.hdiamond.HDiamond;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HDiamondConfig {


    public static final String FILENAME = "hdiamond.json";
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public boolean renderFull = true;

    public static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve(FILENAME);
    }

    public static HDiamondConfig read() {
        try {
            return GSON.fromJson(Files.readString(file()), HDiamondConfig.class);
        }
        catch (IOException e) {
            var config = new HDiamondConfig();
            config.write();
            return config;
        }
    }

    public void write() {
        try {
            Files.writeString(file(), GSON.toJson(this));
        }
        catch (IOException e) {
            HDiamond.LOGGER.error("Failed to write config file (hdiamond.json):", e);
        }
    }
}
