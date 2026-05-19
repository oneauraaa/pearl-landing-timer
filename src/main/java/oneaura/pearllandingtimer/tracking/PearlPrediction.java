package oneaura.pearllandingtimer.tracking;

import net.minecraft.entity.Entity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public final class PearlPrediction {
	private static final int MAX_PREDICTION_TICKS = 200;
	private static final double GRAVITY = 0.03D;
	private static final double AIR_DRAG = 0.99D;
	private static final double WATER_DRAG = 0.8D;

	private PearlPrediction() {
	}

	public static int predictTicksUntilLanding(World world, Vec3d position, Vec3d velocity, Entity raycastEntity) {
		for (int ticks = 1; ticks <= MAX_PREDICTION_TICKS; ticks++) {
			Vec3d nextPosition = position.add(velocity);
			BlockHitResult hitResult = world.raycast(new RaycastContext(
					position,
					nextPosition,
					RaycastContext.ShapeType.COLLIDER,
					RaycastContext.FluidHandling.NONE,
					raycastEntity
			));

			if (hitResult.getType() != HitResult.Type.MISS) {
				return ticks;
			}

			position = nextPosition;
			double drag = isInWater(world, position) ? WATER_DRAG : AIR_DRAG;
			velocity = velocity.multiply(drag).add(0.0D, -GRAVITY, 0.0D);

			if (!world.isInBuildLimit(BlockPos.ofFloored(position))) {
				return ticks;
			}
		}

		return MAX_PREDICTION_TICKS;
	}

	private static boolean isInWater(World world, Vec3d position) {
		return world.getFluidState(BlockPos.ofFloored(position)).isIn(FluidTags.WATER);
	}

	public static boolean isOwnedBy(Entity entity, Entity owner) {
		return entity instanceof net.minecraft.entity.projectile.thrown.EnderPearlEntity pearl && owner.equals(pearl.getOwner());
	}
}
