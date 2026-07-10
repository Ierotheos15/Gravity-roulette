package io.github.ierotheos15.gravityroulette;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

public class GravityPlayerTick {
    private static final double GRAVITY_STRENGTH = 0.25;
    private static final double MAX_SPEED = 1.5;

    public static void onPlayerTick(ServerPlayer player) {
        applyGravity(player);
    }

    public static void onEntityTick(Entity entity) {
        if (entity instanceof Player) return;
        if (entity instanceof Projectile) return;
        if (entity instanceof ItemEntity) return;
        applyGravity(entity);
    }

    private static void applyGravity(Entity entity) {
        if (entity instanceof ServerPlayer player && player.isSpectator()) return;

        GravityDirection dir = GravityState.getCurrent();
        if (dir == GravityDirection.DOWN) return;

        Vec3 vel = entity.getDeltaMovement();
        double x = vel.x;
        double y = vel.y;
        double z = vel.z;

        Direction mcDir = toDirection(dir);

        // Cancel vanilla gravity
        y += 0.08;

        // If touching surface in gravity direction, stop on that axis but allow movement on others
        if (isBlocked(entity, mcDir)) {
            switch (dir) {
                case UP    -> entity.setDeltaMovement(x, Math.min(0, y), z);
                case NORTH -> entity.setDeltaMovement(x, y, Math.max(0, z));
                case SOUTH -> entity.setDeltaMovement(x, y, Math.min(0, z));
                case EAST  -> entity.setDeltaMovement(Math.min(0, x), y, z);
                case WEST  -> entity.setDeltaMovement(Math.max(0, x), y, z);
                default    -> {}
            }
            entity.hurtMarked = true;
            return;
        }

        // Kill if too far with nothing stopping them
        if (isTooFar(entity, dir)) {
            if (entity instanceof ServerPlayer player) {
                player.hurt(player.damageSources().fellOutOfWorld(), 1000f);
            } else {
                entity.discard();
            }
            return;
        }

        switch (dir) {
            case UP -> {
                y = Math.min(y + GRAVITY_STRENGTH, MAX_SPEED);
            }
            case NORTH -> {
                z = Math.max(z - GRAVITY_STRENGTH, -MAX_SPEED);
                y = 0;
            }
            case SOUTH -> {
                z = Math.min(z + GRAVITY_STRENGTH, MAX_SPEED);
                y = 0;
            }
            case EAST -> {
                x = Math.min(x + GRAVITY_STRENGTH, MAX_SPEED);
                y = 0;
            }
            case WEST -> {
                x = Math.max(x - GRAVITY_STRENGTH, -MAX_SPEED);
                y = 0;
            }
        }

        entity.setDeltaMovement(x, y, z);
        entity.resetFallDistance();
        entity.hurtMarked = true;
        entity.setOnGround(false);
    }

    private static boolean isTooFar(Entity entity, GravityDirection dir) {
        return switch (dir) {
            case UP    -> entity.getY() > 400;
            case NORTH -> entity.getZ() < -30000000;
            case SOUTH -> entity.getZ() > 30000000;
            case EAST  -> entity.getX() > 30000000;
            case WEST  -> entity.getX() < -30000000;
            default    -> false;
        };
    }

    private static boolean isBlocked(Entity entity, Direction dir) {
        BlockPos pos = entity.blockPosition().relative(dir, 1);
        return !entity.level().getBlockState(pos).isAir();
    }

    private static Direction toDirection(GravityDirection dir) {
        return switch (dir) {
            case UP    -> Direction.UP;
            case NORTH -> Direction.NORTH;
            case SOUTH -> Direction.SOUTH;
            case EAST  -> Direction.EAST;
            case WEST  -> Direction.WEST;
            default    -> Direction.DOWN;
        };
    }
}