package com.acikek.hdiamond.entity;

import com.acikek.hdiamond.HDiamond;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PanelEntity extends AbstractDecorationEntity {

    public static EntityType<PanelEntity> ENTITY_TYPE;

    public PanelEntity(EntityType<PanelEntity> entityType, World world) {
        super(entityType, world);
    }

    public PanelEntity(World world, BlockPos pos, Direction direction) {
        super(ENTITY_TYPE, world, pos);
        setFacing(direction);
        setPosition(pos.getX(), pos.getY(), pos.getZ());
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
        return super.canStayAttached();
    }

    public static void register() {
        ENTITY_TYPE = Registry.register(
                Registry.ENTITY_TYPE,
                HDiamond.id("panel"),
                FabricEntityTypeBuilder.<PanelEntity>create(SpawnGroup.MISC, PanelEntity::new)
                        .dimensions(EntityDimensions.fixed(1.0f, 1.0f))
                        .build()
        );
    }
}
