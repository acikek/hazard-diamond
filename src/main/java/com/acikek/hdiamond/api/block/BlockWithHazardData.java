package com.acikek.hdiamond.api.block;

import com.acikek.hdiamond.api.HazardDiamondAPI;
import com.acikek.hdiamond.api.util.HazardDataHolder;
import com.acikek.hdiamond.core.HazardData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

/**
 * A block base that, when interacted with, displays immutable {@link HazardData}.
 */
public class BlockWithHazardData extends Block implements HazardDataHolder {

    private final HazardData data;

    public BlockWithHazardData(Settings settings, HazardData data) {
        super(settings);
        this.data = data;
    }

    @Override
    public @NotNull HazardData getHazardData() {
        return data;
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public void updateHazardData(HazardData data) {

    }

    @Override
    public void setHazardData(HazardData data) {

    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (hand != Hand.MAIN_HAND) {
            return ActionResult.PASS;
        }
        if (world.isClient()) {
            HazardDiamondAPI.open(data);
        }
        return ActionResult.success(world.isClient());
    }
}
