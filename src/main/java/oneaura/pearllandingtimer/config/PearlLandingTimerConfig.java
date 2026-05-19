package oneaura.pearllandingtimer.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import oneaura.pearllandingtimer.PearlLandingTimerClient;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class PearlLandingTimerConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("pearl-landing-timer.json");

	public boolean hudEnabled = true;
	public boolean showPearlIcon = false;
	public boolean showBackground = false;
	public TimerDisplayMode timerDisplayMode = TimerDisplayMode.SECONDS;
	public double hudX = 0.5D;
	public double hudY = 0.35D;

	public static PearlLandingTimerConfig load() {
		if (!Files.exists(CONFIG_PATH)) {
			PearlLandingTimerConfig config = new PearlLandingTimerConfig();
			config.save();
			return config;
		}

		try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
			PearlLandingTimerConfig config = GSON.fromJson(reader, PearlLandingTimerConfig.class);
			return config == null ? new PearlLandingTimerConfig() : config.sanitize();
		} catch (IOException | RuntimeException exception) {
			PearlLandingTimerClient.LOGGER.warn("Could not load Pearl Landing Timer config; using defaults", exception);
			return new PearlLandingTimerConfig();
		}
	}

	public void save() {
		sanitize();

		try {
			Files.createDirectories(CONFIG_PATH.getParent());

			try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
				GSON.toJson(this, writer);
			}
		} catch (IOException exception) {
			PearlLandingTimerClient.LOGGER.error("Could not save Pearl Landing Timer config", exception);
		}
	}

	public PearlLandingTimerConfig sanitize() {
		if (timerDisplayMode == null) {
			timerDisplayMode = TimerDisplayMode.SECONDS;
		}

		hudX = clamp01(hudX);
		hudY = clamp01(hudY);
		return this;
	}

	public void setHudPosition(int x, int y, int hudWidth, int hudHeight, int screenWidth, int screenHeight) {
		int maxX = Math.max(0, screenWidth - hudWidth);
		int maxY = Math.max(0, screenHeight - hudHeight);
		hudX = maxX == 0 ? 0.0D : clamp01((double) clamp(x, 0, maxX) / (double) maxX);
		hudY = maxY == 0 ? 0.0D : clamp01((double) clamp(y, 0, maxY) / (double) maxY);
	}

	public int getHudX(int hudWidth, int screenWidth) {
		int maxX = Math.max(0, screenWidth - hudWidth);
		return clamp((int) Math.round(hudX * maxX), 0, maxX);
	}

	public int getHudY(int hudHeight, int screenHeight) {
		int maxY = Math.max(0, screenHeight - hudHeight);
		return clamp((int) Math.round(hudY * maxY), 0, maxY);
	}

	private static double clamp01(double value) {
		if (Double.isNaN(value) || Double.isInfinite(value)) {
			return 0.5D;
		}

		return Math.max(0.0D, Math.min(1.0D, value));
	}

	private static int clamp(int value, int min, int max) {
		return Math.max(min, Math.min(max, value));
	}
}
