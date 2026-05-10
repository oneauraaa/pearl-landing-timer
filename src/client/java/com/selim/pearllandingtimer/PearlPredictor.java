package com.selim.pearllandingtimer;

import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.Optional;

public final class PearlPredictor {
    private static final double GRAVITY = 0.03;
    private static final double AIR_DRAG = 0.99;
    private static final double WATER_DRAG = 0.8;

    private PearlPredictor() {
    }

    public static Optional<PearlPrediction> predict(World world, EnderPearlEntity pearl, int maxTicks) {
        Vec3d pos = pearl.getEntityPos();
        Vec3d velocity = pearl.getVelocity();
        Box box = pearl.getBoundingBox();

        for (int tick = 1; tick <= maxTicks; tick++) {
            Vec3d nextPos = pos.add(velocity);
            BlockHitResult blockHit = world.raycast(new RaycastContext(
                    pos,
                    nextPos,
                    RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.ANY,
                    pearl
            ));

            if (blockHit.getType() != HitResult.Type.MISS) {
                return Optional.of(new PearlPrediction(pearl.getId(), blockHit.getPos(), tick));
            }

            Box nextBox = box.offset(velocity).expand(0.05);
            HitResult entityHit = net.minecraft.entity.projectile.ProjectileUtil.getEntityCollision(
                    world,
                    pearl,
                    pos,
                    nextPos,
                    nextBox,
                    entity -> !entity.isSpectator() && entity.canHit()
            );
            if (entityHit != null) {
                return Optional.of(new PearlPrediction(pearl.getId(), entityHit.getPos(), tick));
            }

            pos = nextPos;
            box = box.offset(velocity);

            FluidState fluidState = world.getFluidState(BlockPos.ofFloored(pos));
            double drag = fluidState.isEmpty() ? AIR_DRAG : WATER_DRAG;
            velocity = velocity.multiply(drag);
            velocity = velocity.add(0.0, -GRAVITY, 0.0);
        }

        return Optional.empty();
    }
}
