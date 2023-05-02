package com.acikek.hdiamond;

import com.acikek.hdiamond.command.HDiamondCommand;
import com.acikek.hdiamond.core.HazardDiamond;
import com.acikek.hdiamond.core.quadrant.FireHazard;
import com.acikek.hdiamond.core.quadrant.HealthHazard;
import com.acikek.hdiamond.core.quadrant.Reactivity;
import com.acikek.hdiamond.core.quadrant.SpecificHazard;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class HDiamond implements ModInitializer {

    public static final String ID = "hdiamond";

    public static Identifier id(String path) {
        return new Identifier(ID, path);
    }

    @Override
    public void onInitialize() {
        var bleach = new HazardDiamond(FireHazard.INFLAMMABLE, HealthHazard.EXTREME, Reactivity.STABLE, SpecificHazard.OXIDIZER);
        HDiamondCommand.register();
    }
}
