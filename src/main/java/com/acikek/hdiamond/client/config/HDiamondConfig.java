package com.acikek.hdiamond.client.config;

import mc.microconfig.Comment;
import mc.microconfig.ConfigData;

public class HDiamondConfig implements ConfigData {

    @Comment("Render the diamond symbols in-world")
    public boolean renderFull = true;
}
