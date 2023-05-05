package com.acikek.hdiamond.item;

import com.acikek.hdiamond.HDiamond;
import com.acikek.hdiamond.core.HazardData;
import com.acikek.hdiamond.core.quadrant.SpecificHazard;
import com.acikek.hdiamond.entity.PanelEntity;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
            if (nbt.contains("HazardData")) {
                var data = HazardData.fromNbt(nbt.getCompound("HazardData"));
                panelEntity.getDataTracker().set(PanelEntity.HAZARD_DATA, data);
            }
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

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (stack.hasNbt()) {
            var nbt = stack.getOrCreateNbt();
            if (nbt.contains("HazardData")) {
                var data = HazardData.fromNbt(nbt.getCompound("HazardData"));
                var sep = Text.literal("-").formatted(Formatting.GRAY);
                var numerals = data.diamond().fire().get().getSymbol()
                        .append(sep).append(data.diamond().health().get().getSymbol())
                        .append(sep).append(data.diamond().reactivity().get().getSymbol());
                if (data.diamond().specific().get() != SpecificHazard.NONE) {
                    numerals.append(sep).append(data.diamond().specific().get().getSymbol());
                }
                tooltip.add(numerals);
                var pictograms = Text.translatable("tooltip.hdiamond.panel_item.pictograms", data.pictograms().size())
                        .formatted(Formatting.GRAY);
                tooltip.add(pictograms);
            }
        }
        super.appendTooltip(stack, world, tooltip, context);
    }

    public static void register() {
        Registry.register(Registry.ITEM, HDiamond.id("panel_item"), INSTANCE);
    }
}
