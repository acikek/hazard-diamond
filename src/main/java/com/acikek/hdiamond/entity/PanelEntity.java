package com.acikek.hdiamond.entity;

import com.acikek.hdiamond.HDiamond;
import com.acikek.hdiamond.client.screen.HazardScreen;
import com.acikek.hdiamond.core.HazardData;
import com.acikek.hdiamond.item.PanelItem;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PanelEntity extends AbstractDecorationEntity {

    public static EntityType<PanelEntity> ENTITY_TYPE;

    public static final TrackedData<Boolean> WAXED = DataTracker.registerData(PanelEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<HazardData> HAZARD_DATA = DataTracker.registerData(PanelEntity.class, HazardData.DATA_TRACKER);

    public PanelEntity(EntityType<PanelEntity> entityType, World world) {
        super(entityType, world);
    }

    public PanelEntity(World world, BlockPos pos, Direction direction) {
        super(ENTITY_TYPE, world, pos);
        setFacing(direction);
        setPosition(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    protected void initDataTracker() {
        getDataTracker().startTracking(WAXED, false);
        getDataTracker().startTracking(HAZARD_DATA, HazardData.empty());
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (hand != Hand.MAIN_HAND) {
            return ActionResult.PASS;
        }
        ItemStack stack = player.getStackInHand(hand);
        if (player.isSneaking() && stack.isOf(Items.HONEYCOMB)) {
            if (isWaxed()) {
                return ActionResult.FAIL;
            }
            if (!player.isCreative()) {
                stack.decrement(1);
            }
            if (player instanceof ServerPlayerEntity serverPlayer) {
                Criteria.ITEM_USED_ON_BLOCK.trigger(serverPlayer, getBlockPos(), stack);
            }
            playSound(SoundEvents.ITEM_HONEYCOMB_WAX_ON, 1.0f, 1.0f);
            getDataTracker().set(WAXED, true);
        }
        else if (world.isClient()) {
            MinecraftClient.getInstance().setScreen(new HazardScreen(this));
        }
        return ActionResult.success(world.isClient());
    }

    @Override
    public int getWidthPixels() {
        return 14;
    }

    @Override
    public int getHeightPixels() {
        return 14;
    }

    @Override
    public void onBreak(@Nullable Entity entity) {
        if (!world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            return;
        }
        dropStack(getPickBlockStack());
        playSound(SoundEvents.ENTITY_PAINTING_BREAK, 1.0f, 1.0f);
    }

    @Override
    public void onPlace() {
        playSound(SoundEvents.BLOCK_NETHERITE_BLOCK_PLACE, 1.0f, 1.0f);
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this, facing.getId(), getDecorationBlockPos());
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        setFacing(Direction.byId(packet.getEntityData()));
    }

    @Override
    public boolean canStayAttached() {
        if (!world.isSpaceEmpty(this)) {
            return false;
        }
        BlockState blockState = world.getBlockState(attachmentPos.offset(facing.getOpposite()));
        return blockState.getMaterial().isSolid() || facing.getAxis().isHorizontal() && AbstractRedstoneGateBlock.isRedstoneGate(blockState);
    }

    public boolean isWaxed() {
        return getDataTracker().get(WAXED);
    }

    public HazardData getHazardData() {
        return getDataTracker().get(HAZARD_DATA);
    }

    public void updateHazardData(HazardData data) {
        if (isWaxed()) {
            return;
        }
        if (!getHazardData().equals(data)) {
            getDataTracker().set(HAZARD_DATA, data);
            playSound(SoundEvents.BLOCK_SMITHING_TABLE_USE, 1.0f, 1.0f);
        }
    }

    @Nullable
    @Override
    public ItemStack getPickBlockStack() {
        ItemStack stack = PanelItem.INSTANCE.getDefaultStack();
        if (!getHazardData().isEmpty()) {
            stack.getOrCreateNbt().put("HazardData", getHazardData().toNbt());
        }
        return stack;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("Waxed", isWaxed());
        nbt.put("HazardData", getHazardData().toNbt());
        nbt.putByte("Facing", (byte) facing.getId());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        getDataTracker().set(WAXED, nbt.getBoolean("Waxed"));
        getDataTracker().set(HAZARD_DATA, HazardData.fromNbt(nbt.getCompound("HazardData")));
        facing = Direction.byId(nbt.getByte("Facing"));
    }

    public static void register() {
        ENTITY_TYPE = Registry.register(
                Registry.ENTITY_TYPE,
                HDiamond.id("panel"),
                FabricEntityTypeBuilder.<PanelEntity>create(SpawnGroup.MISC, PanelEntity::new)
                        .dimensions(EntityDimensions.fixed(1.0f, 1.0f))
                        .trackedUpdateRate(Integer.MAX_VALUE)
                        .build()
        );
    }
}
