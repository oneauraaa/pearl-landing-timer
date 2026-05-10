package com.selim.pearllandingtimer;

import net.minecraft.util.math.Vec3d;

public record PearlPrediction(int entityId, Vec3d landingPos, int ticksUntilLanding) {
    public double secondsUntilLanding() {
        return ticksUntilLanding / 20.0;
    }
}
