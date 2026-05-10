package com.selim.pearllandingtimer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class PearlLandingTimerClient implements ClientModInitializer {
    public static final String MOD_ID = "pearl-landing-timer";

    private static PearlLandingTimerConfig config;
    private static KeyBinding editHudKey;
    private final List<PearlPrediction> predictions = new ArrayList<>();
    private int particleTicker;

    public static PearlLandingTimerConfig config() {
        return config;
    }

    @Override
    public void onInitializeClient() {
        config = PearlLandingTimerConfig.load();
        editHudKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.pearl-landing-timer.edit_hud",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_P,
                KeyBinding.Category.create(Identifier.of(MOD_ID, "category"))
        ));
        ClientTickEvents.END_CLIENT_TICK.register(this::tick);
        HudRenderCallback.EVENT.register(this::renderHud);
    }

    private void tick(MinecraftClient client) {
        predictions.clear();

        if (!config.enabled || client.world == null || client.player == null) {
            return;
        }

        while (editHudKey.wasPressed()) {
            client.setScreen(new PearlHudEditorScreen(config));
        }

        for (Entity entity : client.world.getEntities()) {
            if (!(entity instanceof EnderPearlEntity pearl)) {
                continue;
            }
            if (config.onlyOwnPearls && pearl.getOwner() != client.player) {
                continue;
            }

            PearlPredictor.predict(client.world, pearl, config.maxPredictionTicks)
                    .ifPresent(predictions::add);
        }

        predictions.sort(Comparator.comparingInt(PearlPrediction::ticksUntilLanding));
        spawnMarkerParticles(client);
    }

    private void renderHud(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!config.enabled || !config.showCountdown || client.player == null || predictions.isEmpty()) {
            return;
        }

        PearlPrediction nearest = predictions.getFirst();
        Text text = Text.translatable("hud.pearl-landing-timer.lands_in", nearest.secondsUntilLanding());
        int width = client.textRenderer.getWidth(text);
        int x = config.hudX < 0 ? (context.getScaledWindowWidth() - width) / 2 : clamp(config.hudX, 0, Math.max(0, context.getScaledWindowWidth() - width));
        int y = config.hudY < 0 ? context.getScaledWindowHeight() / 2 + 18 : clamp(config.hudY, 0, Math.max(0, context.getScaledWindowHeight() - client.textRenderer.fontHeight));
        context.drawTextWithShadow(client.textRenderer, text, x, y, 0xFFFFFF);
    }

    private void spawnMarkerParticles(MinecraftClient client) {
        if (!config.markerParticles || client.world == null || predictions.isEmpty()) {
            return;
        }

        particleTicker = (particleTicker + 1) % 5;
        if (particleTicker != 0) {
            return;
        }

        int color = (config.markerRed << 16) | (config.markerGreen << 8) | config.markerBlue;
        DustParticleEffect particle = new DustParticleEffect(color, 1.15F);

        for (PearlPrediction prediction : predictions) {
            Vec3d pos = prediction.landingPos();
            client.world.addParticleClient(particle, pos.x, pos.y + 0.12, pos.z, 0.0, 0.01, 0.0);
        }
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
