package com.acikek.hdiamond.entity;

import com.acikek.hdiamond.HDiamond;
import com.acikek.hdiamond.api.util.HazardDataHolder;
import com.acikek.hdiamond.client.screen.HazardScreen;
import com.acikek.hdiamond.core.HazardData;
import com.acikek.hdiamond.item.PanelItem;
import com.acikek.hdiamond.network.HDNetworking;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;

public class PanelEntity extends AbstractDecorationEntity implements HazardDataHolder {

    public static EntityType<PanelEntity> ENTITY_TYPE;

    private boolean waxed = false;
    private HazardData data = HazardData.empty();

    public PanelEntity(EntityType<PanelEntity> entityType, World world) {
        super(entityType, world);
    }

    public PanelEntity(World world, BlockPos pos, Direction direction) {
        super(ENTITY_TYPE, world, pos);
        setFacing(direction);
        setPosition(pos.getX(), pos.getY(), pos.getZ());
    }

    @Environment(EnvType.CLIENT)
    public void openScreen() {
        MinecraftClient.getInstance().setScreen(new HazardScreen(this));
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
            waxed = true;
        }
        else if (getWorld().isClient()) {
            openScreen();
        }
        return ActionResult.success(getWorld().isClient());
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        if (super.isInvulnerableTo(damageSource)) {
            return true;
        }
        return damageSource.getSource() instanceof ServerPlayerEntity player
                && player.interactionManager.getGameMode() == GameMode.ADVENTURE;
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
        if (!getWorld().getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            return;
        }
        dropStack(getPickBlockStack());
        playSound(SoundEvents.ENTITY_PAINTING_BREAK, 1.0f, 1.0f);
    }

    private void sync(ServerPlayerEntity broadcaster) {
        var players = new HashSet<>(PlayerLookup.tracking(this));
        if (broadcaster != null) {
            players.remove(broadcaster);
        }
        HDNetworking.s2cUpdatePanelData(players, this);
    }

    @Override
    public void onPlace() {
        playSound(SoundEvents.BLOCK_NETHERITE_BLOCK_PLACE, 1.0f, 1.0f);
        sync(null);
    }

    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        HDNetworking.s2cUpdatePanelData(List.of(player), this);
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this, facing.getId(), getDecorationBlockPos());
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        setFacing(Direction.byId(packet.getEntityData()));
    }

    @Override
    public boolean canStayAttached() {
        if (!getWorld().isSpaceEmpty(this)) {
            return false;
        }
        BlockState blockState = getWorld().getBlockState(attachmentPos.offset(facing.getOpposite()));
        return blockState.isSolid() || facing.getAxis().isHorizontal() && AbstractRedstoneGateBlock.isRedstoneGate(blockState);
    }

    public boolean isWaxed() {
        return waxed;
    }

    @Override
    public @NotNull HazardData getHazardData() {
        return data;
    }

    @Override
    public boolean isEditable() {
        return !isWaxed();
    }

    @Override
    public void updateHazardData(HazardData data) {
        this.data = data;
        HDNetworking.c2sUpdatePanelData(this, data);
    }

    @Override
    public void setHazardData(HazardData data, ServerPlayerEntity broadcaster) {
        if (isWaxed() || getHazardData().equals(data)) {
            return;
        }
        this.data = data;
        if (broadcaster != null) {
            playSound(SoundEvents.BLOCK_SMITHING_TABLE_USE, 1.0f, 1.0f);
            sync(broadcaster);
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
        waxed = nbt.getBoolean("Waxed");
        data = HazardData.fromNbt(nbt.getCompound("HazardData"));
        facing = Direction.byId(nbt.getByte("Facing"));
    }

    public static void register() {
        ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                HDiamond.id("panel"),
                FabricEntityTypeBuilder.<PanelEntity>create(SpawnGroup.MISC, PanelEntity::new)
                        .dimensions(EntityDimensions.fixed(1.0f, 1.0f))
                        .trackedUpdateRate(Integer.MAX_VALUE)
                        .build()
        );
    }
}
