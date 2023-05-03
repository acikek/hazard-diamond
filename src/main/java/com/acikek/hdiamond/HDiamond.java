package com.acikek.hdiamond;

import com.acikek.hdiamond.command.HDiamondCommand;
import com.acikek.hdiamond.core.HazardData;
import com.acikek.hdiamond.entity.PanelEntity;
import com.acikek.hdiamond.item.PanelItem;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class HDiamond implements ModInitializer {

    public static final String ID = "hdiamond";

    public static Identifier id(String path) {
        return new Identifier(ID, path);
    }

    @Override
    public void onInitialize() {
        HDiamondCommand.register();
        HazardData.registerTrackedData();
        PanelEntity.register();
        PanelItem.register();

        /*UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClient()) {
                var d = new HazardDiamond(FireHazard.BELOW_25C, HealthHazard.EXTREME, Reactivity.STABLE, Optional.empty());
                MinecraftClient.getInstance().setScreen(new HazardScreen(Text.empty(), d, List.of(Pictogram.EXPLOSIVE, Pictogram.TOXIC, Pictogram.CORROSIVE)));
            }
            return ActionResult.PASS;
        });*/
    }
}
