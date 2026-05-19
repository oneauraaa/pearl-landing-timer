package oneaura.pearllandingtimer.tracking;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PearlTracker {
	private static final int FALLBACK_ACQUIRE_TICKS = 10;
	private static final double FALLBACK_ACQUIRE_DISTANCE_SQUARED = 64.0D;
	private static final int MANUAL_TRACKED_ID = -2;

	private int trackedPearlId = -1;
	private int predictedTicks;

	public void tick(MinecraftClient client) {
		if (client.world == null || client.player == null) {
			clear();
			return;
		}

		EnderPearlEntity trackedPearl = null;
		EnderPearlEntity latestPearl = null;
		for (Entity entity : client.world.getEntities()) {
			if (!(entity instanceof EnderPearlEntity pearl)) {
				continue;
			}

			if (pearl.getId() == trackedPearlId) {
				trackedPearl = pearl;
			}

			if (!isTrackablePearl(pearl, client.player)) {
				continue;
			}

			if (latestPearl == null || pearl.age < latestPearl.age) {
				latestPearl = pearl;
			}
		}

		if (latestPearl == null && trackedPearl != null && !trackedPearl.isRemoved()) {
			latestPearl = trackedPearl;
		}

		if (latestPearl == null || latestPearl.isRemoved()) {
			if (trackedPearlId == MANUAL_TRACKED_ID && predictedTicks > 0) {
				predictedTicks--;
			} else {
				clear();
			}
			return;
		}

		trackedPearlId = latestPearl.getId();
		predictedTicks = PearlPrediction.predictTicksUntilLanding(latestPearl);
	}

	public boolean hasPrediction() {
		return trackedPearlId != -1 && predictedTicks > 0;
	}

	public int getPredictedTicks() {
		return predictedTicks;
	}

	private void clear() {
		trackedPearlId = -1;
		predictedTicks = 0;
	}

	private static boolean isTrackablePearl(EnderPearlEntity pearl, Entity player) {
		if (PearlPrediction.isOwnedBy(pearl, player)) {
			return true;
		}

		return pearl.getOwner() == null
				&& pearl.age <= FALLBACK_ACQUIRE_TICKS
				&& pearl.squaredDistanceTo(player) <= FALLBACK_ACQUIRE_DISTANCE_SQUARED;
	}

	public void trackManualThrow(World world, Vec3d position, Vec3d velocity, Entity raycastEntity) {
		trackedPearlId = MANUAL_TRACKED_ID;
		predictedTicks = PearlPrediction.predictTicksUntilLanding(world, position, velocity, raycastEntity);
	}
}
