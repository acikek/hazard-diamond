package com.acikek.hdiamond.client.config;

import com.acikek.hdiamond.client.HDiamondClient;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl.api.Binding;
import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.YetAnotherConfigLib;
import dev.isxander.yacl.gui.controllers.TickBoxController;
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
                        .option(Option.createBuilder(Boolean.class)
                                .name(Text.translatable("config.hdiamond.render_full.name"))
                                .tooltip(Text.translatable("config.hdiamond.render_full.description"))
                                .binding(RENDER_FULL)
                                .controller(TickBoxController::new)
                                .build())
                        .build())
                .build();
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return !FabricLoader.getInstance().isModLoaded("yet_another_config_lib")
                ? screen -> null
                : screen -> createConfig().generateScreen(screen);
    }
}
