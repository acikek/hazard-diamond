package com.acikek.hdiamond.item;

import com.acikek.hdiamond.HDiamond;
import com.acikek.hdiamond.core.HazardData;
import com.acikek.hdiamond.entity.PanelEntity;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.item.TooltipType;
import net.minecraft.component.DataComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.List;

public class PanelItem extends Item {

    public static DataComponentType<NbtComponent> HAZARD_DATA_COMPONENT;
    public static PanelItem INSTANCE;

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
        NbtComponent entityData = context.getStack().getOrDefault(DataComponentTypes.ENTITY_DATA, NbtComponent.DEFAULT);
        if (entityData != null) {
            EntityType.loadFromEntityNbt(world, player, panelEntity, entityData);
            NbtComponent data = context.getStack().get(HAZARD_DATA_COMPONENT);
            if (data != null) {
                panelEntity.setHazardData(HazardData.fromNbt(data.copyNbt()));
            }
        }
        if (!panelEntity.canStayAttached()) {
            return ActionResult.CONSUME;
        }
        if (!world.isClient()) {
            world.emitGameEvent(player, GameEvent.ENTITY_PLACE, panelEntity.getBlockPos());
            world.spawnEntity(panelEntity);
            panelEntity.onPlace();
        }
        if (player != null && !player.isCreative()) {
            context.getStack().decrement(1);
        }
        return ActionResult.success(world.isClient());
    }

    public boolean canPlaceOn(PlayerEntity player, Direction side, ItemStack stack, BlockPos pos) {
        return !side.getAxis().isVertical() && player.canPlaceOn(pos, side, stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        NbtComponent nbt = stack.get(HAZARD_DATA_COMPONENT);
        if (nbt != null) {
            HazardData data = HazardData.fromNbt(nbt.copyNbt());
            tooltip.addAll(data.getTooltip());
        }
        super.appendTooltip(stack, context, tooltip, type);
    }

    public static void register() {
        if (!HDiamond.config.enableContent) {
            return;
        }
        HAZARD_DATA_COMPONENT = Registry.register(Registries.DATA_COMPONENT_TYPE, HDiamond.id("hazard_data"), DataComponentType.<NbtComponent>builder().codec(NbtComponent.CODEC).build()); // Bad. TODO: Fix!
        INSTANCE = Registry.register(Registries.ITEM, HDiamond.id("panel_item"), new PanelItem(new Item.Settings()));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries ->
            entries.addAfter(Items.GLOW_ITEM_FRAME, INSTANCE)
        );
    }
}
