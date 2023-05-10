package com.acikek.hdiamond.datagen;

import com.acikek.hdiamond.item.PanelItem;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.recipe.book.RecipeCategory;

import java.util.function.Consumer;

public class HDDatagen implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        var pack = fabricDataGenerator.createPack();

        pack.addProvider((output, $) -> new FabricRecipeProvider(output) {
            @Override
            public void generate(Consumer<RecipeJsonProvider> exporter) {
                ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, PanelItem.INSTANCE, 4)
                        .criterion("has_dye", RecipeProvider.conditionsFromTag(ConventionalItemTags.DYES))
                        .criterion("has_iron", RecipeProvider.conditionsFromTag(ConventionalItemTags.IRON_INGOTS))
                        .pattern(" R ")
                        .pattern("BIY")
                        .pattern(" W ")
                        .input('R', ConventionalItemTags.RED_DYES)
                        .input('B', ConventionalItemTags.BLUE_DYES)
                        .input('Y', ConventionalItemTags.YELLOW_DYES)
                        .input('W', ConventionalItemTags.WHITE_DYES)
                        .input('I', ConventionalItemTags.IRON_INGOTS)
                        .offerTo(exporter);
            }
        });

        pack.addProvider((output, $) -> new FabricModelProvider(output) {
            @Override
            public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
                // Empty
            }

            @Override
            public void generateItemModels(ItemModelGenerator itemModelGenerator) {
                itemModelGenerator.register(PanelItem.INSTANCE, Models.GENERATED);
            }
        });
    }
}
