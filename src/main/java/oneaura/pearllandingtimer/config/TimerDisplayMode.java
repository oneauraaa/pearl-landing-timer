package oneaura.pearllandingtimer.config;

import net.minecraft.text.Text;

public enum TimerDisplayMode {
	SECONDS,
	TICKS;

	public Text getDisplayName() {
		return switch (this) {
			case SECONDS -> Text.translatable("text.pearl-landing-timer.timer_mode.seconds");
			case TICKS -> Text.translatable("text.pearl-landing-timer.timer_mode.ticks");
		};
	}
}
