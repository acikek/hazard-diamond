package com.acikek.hdiamond;

import com.acikek.hdiamond.client.HazardScreen;
import com.acikek.hdiamond.command.HDiamondCommand;
import com.acikek.hdiamond.core.HazardDiamond;
import com.acikek.hdiamond.core.quadrant.FireHazard;
import com.acikek.hdiamond.core.quadrant.HealthHazard;
import com.acikek.hdiamond.core.quadrant.Reactivity;
import com.acikek.hdiamond.core.quadrant.SpecificHazard;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

public class HDiamond implements ModInitializer {

    public static final String ID = "hdiamond";

    public static Identifier id(String path) {
        return new Identifier(ID, path);
    }

    @Override
    public void onInitialize() {
        HDiamondCommand.register();

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClient()) {
                var d = new HazardDiamond(FireHazard.BELOW_25C, HealthHazard.EXTREME, Reactivity.STABLE, SpecificHazard.OXIDIZER);
                MinecraftClient.getInstance().setScreen(new HazardScreen(Text.empty(), d));
            }
            return ActionResult.PASS;
        });
    }
}
