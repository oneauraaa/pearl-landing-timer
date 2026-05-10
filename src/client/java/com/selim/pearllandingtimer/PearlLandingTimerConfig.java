package com.selim.pearllandingtimer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class PearlLandingTimerConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("pearl-landing-timer.json");

    public boolean enabled = true;
    public boolean showCountdown = true;
    public boolean onlyOwnPearls = true;
    public int maxPredictionTicks = 200;
    public boolean markerParticles = true;
    public int markerRed = 90;
    public int markerGreen = 255;
    public int markerBlue = 180;
    public int hudX = -1;
    public int hudY = -1;

    public static PearlLandingTimerConfig load() {
        if (Files.notExists(CONFIG_PATH)) {
            PearlLandingTimerConfig config = new PearlLandingTimerConfig();
            config.save();
            return config;
        }

        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            PearlLandingTimerConfig config = GSON.fromJson(reader, PearlLandingTimerConfig.class);
            return config == null ? new PearlLandingTimerConfig() : config.clamp();
        } catch (IOException exception) {
            return new PearlLandingTimerConfig();
        }
    }

    public void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(clamp(), writer);
            }
        } catch (IOException ignored) {
        }
    }

    public Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("text.autoconfig.pearl-landing-timer.title"))
                .setSavingRunnable(this::save);

        ConfigEntryBuilder entries = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("text.autoconfig.pearl-landing-timer.title"));

        general.addEntry(entries.startBooleanToggle(Text.translatable("option.pearl-landing-timer.enabled"), enabled)
                .setDefaultValue(true)
                .setSaveConsumer(value -> enabled = value)
                .build());
        general.addEntry(entries.startBooleanToggle(Text.translatable("option.pearl-landing-timer.showCountdown"), showCountdown)
                .setDefaultValue(true)
                .setSaveConsumer(value -> showCountdown = value)
                .build());
        general.addEntry(entries.startBooleanToggle(Text.translatable("option.pearl-landing-timer.onlyOwnPearls"), onlyOwnPearls)
                .setDefaultValue(true)
                .setSaveConsumer(value -> onlyOwnPearls = value)
                .build());
        general.addEntry(entries.startIntSlider(Text.translatable("option.pearl-landing-timer.maxPredictionTicks"), maxPredictionTicks, 20, 600)
                .setDefaultValue(200)
                .setSaveConsumer(value -> maxPredictionTicks = value)
                .build());
        general.addEntry(entries.startBooleanToggle(Text.translatable("option.pearl-landing-timer.markerParticles"), markerParticles)
                .setDefaultValue(true)
                .setSaveConsumer(value -> markerParticles = value)
                .build());
        general.addEntry(entries.startIntSlider(Text.translatable("option.pearl-landing-timer.markerRed"), markerRed, 0, 255)
                .setDefaultValue(90)
                .setSaveConsumer(value -> markerRed = value)
                .build());
        general.addEntry(entries.startIntSlider(Text.translatable("option.pearl-landing-timer.markerGreen"), markerGreen, 0, 255)
                .setDefaultValue(255)
                .setSaveConsumer(value -> markerGreen = value)
                .build());
        general.addEntry(entries.startIntSlider(Text.translatable("option.pearl-landing-timer.markerBlue"), markerBlue, 0, 255)
                .setDefaultValue(180)
                .setSaveConsumer(value -> markerBlue = value)
                .build());

        return builder.build();
    }

    PearlLandingTimerConfig clamp() {
        maxPredictionTicks = Math.max(20, Math.min(600, maxPredictionTicks));
        markerRed = clampColor(markerRed);
        markerGreen = clampColor(markerGreen);
        markerBlue = clampColor(markerBlue);
        return this;
    }

    private static int clampColor(int value) {
        return Math.max(0, Math.min(255, value));
    }
}
