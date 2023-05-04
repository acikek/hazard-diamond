package com.acikek.hdiamond.item;

import com.acikek.hdiamond.HDiamond;
import com.acikek.hdiamond.entity.PanelEntity;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class PanelItem extends Item {

    public static final PanelItem INSTANCE = new PanelItem(new FabricItemSettings().group(ItemGroup.DECORATIONS));

    public PanelItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        BlockPos offset = context.getBlockPos().offset(context.getSide());
        if (player != null && !canPlaceOn(player, context.getSide(), context.getStack(), offset)) {
            return ActionResult.FAIL;
        }
        var panelEntity = new PanelEntity(world, offset, context.getSide());
        NbtCompound nbt = context.getStack().getNbt();
        if (nbt != null) {
            EntityType.loadFromEntityNbt(world, player, panelEntity, nbt);
        }
        if (!panelEntity.canStayAttached()) {
            return ActionResult.CONSUME;
        }
        if (!world.isClient()) {
            panelEntity.onPlace();
            world.emitGameEvent(player, GameEvent.ENTITY_PLACE, panelEntity.getBlockPos());
            world.spawnEntity(panelEntity);
        }
        if (player != null && !player.isCreative()) {
            context.getStack().decrement(1);
        }
        return ActionResult.success(world.isClient());
    }

    public boolean canPlaceOn(PlayerEntity player, Direction side, ItemStack stack, BlockPos pos) {
        return !side.getAxis().isVertical() && player.canPlaceOn(pos, side, stack);
    }

    public static void register() {
        Registry.register(Registry.ITEM, HDiamond.id("panel_item"), INSTANCE);
    }
}
