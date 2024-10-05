package com.acikek.hdiamond.client.config;

import com.acikek.hdiamond.HDiamond;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class HDiamondModMenuIntegration implements ModMenuApi {

    public record ConfigBinding<T>(Supplier<T> value, Consumer<T> setter, T defaultValue) implements Binding<T> {

        @Override
        public void setValue(T value) {
            setter.accept(value);
            HDiamond.config.write();
        }

        @Override
        public T getValue() {
            return value.get();
        }
    }

    public YetAnotherConfigLib createConfig() {
        return YetAnotherConfigLib.createBuilder()
                .title(Text.literal("Hazard Diamond"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.translatable("entity.hdiamond.panel"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("config.hdiamond.render_full.name"))
                                .description(OptionDescription.of(Text.translatable("config.hdiamond.render_full.description")))
                                .flag(OptionFlag.WORLD_RENDER_UPDATE)
                                .binding(new ConfigBinding<>(() -> HDiamond.config.renderFull, value -> HDiamond.config.renderFull = value, true))
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("config.hdiamond.enable_content.name"))
                                .description(OptionDescription.of(Text.translatable("config.hdiamond.enable_content.description")))
                                .flag(OptionFlag.GAME_RESTART)
                                .binding(new ConfigBinding<>(() -> HDiamond.config.enableContent, value -> HDiamond.config.enableContent = value, true))
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
