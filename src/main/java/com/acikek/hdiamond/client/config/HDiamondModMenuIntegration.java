package com.acikek.hdiamond.client.config;

import com.acikek.hdiamond.client.HDiamondClient;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;

public class HDiamondModMenuIntegration implements ModMenuApi {

    public static Binding<Boolean> RENDER_FULL = new Binding<>() {

        @Override
        public void setValue(Boolean value) {
            HDiamondClient.config.renderFull = value;
            HDiamondClient.config.write();
        }

        @Override
        public Boolean getValue() {
            return HDiamondClient.config.renderFull;
        }

        @Override
        public Boolean defaultValue() {
            return true;
        }
    };

    public YetAnotherConfigLib createConfig() {
        return YetAnotherConfigLib.createBuilder()
                .title(Text.literal("Hazard Diamond"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.translatable("entity.hdiamond.panel"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("config.hdiamond.render_full.name"))
                                .description(OptionDescription.of(Text.translatable("config.hdiamond.render_full.description")))
                                .flag(OptionFlag.WORLD_RENDER_UPDATE)
                                .binding(RENDER_FULL)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .build())
                .build();
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return !FabricLoader.getInstance().isModLoaded("yet_another_config_lib_v3")
                ? screen -> null
                : screen -> createConfig().generateScreen(screen);
    }
}
