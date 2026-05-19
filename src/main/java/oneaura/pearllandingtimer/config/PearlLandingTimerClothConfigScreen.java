package oneaura.pearllandingtimer.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import oneaura.pearllandingtimer.PearlLandingTimerClient;

public final class PearlLandingTimerClothConfigScreen {
	private PearlLandingTimerClothConfigScreen() {
	}

	public static Screen create(Screen parent) {
		PearlLandingTimerConfig config = PearlLandingTimerClient.getConfig();
		ConfigBuilder builder = ConfigBuilder.create()
				.setParentScreen(parent)
				.setTitle(Text.translatable("text.pearl-landing-timer.config.title"))
				.setSavingRunnable(config::save);
		ConfigEntryBuilder entries = builder.entryBuilder();
		ConfigCategory hud = builder.getOrCreateCategory(Text.translatable("text.pearl-landing-timer.config.category.hud"));

		hud.addEntry(entries.startBooleanToggle(Text.translatable("text.pearl-landing-timer.config.hud_enabled"), config.hudEnabled)
				.setDefaultValue(true)
				.setTooltip(Text.translatable("text.pearl-landing-timer.config.hud_enabled.tooltip"))
				.setSaveConsumer(value -> config.hudEnabled = value)
				.build());

		hud.addEntry(entries.startBooleanToggle(Text.translatable("text.pearl-landing-timer.config.show_pearl_icon"), config.showPearlIcon)
				.setDefaultValue(true)
				.setTooltip(Text.translatable("text.pearl-landing-timer.config.show_pearl_icon.tooltip"))
				.setSaveConsumer(value -> config.showPearlIcon = value)
				.build());

		hud.addEntry(entries.startEnumSelector(
						Text.translatable("text.pearl-landing-timer.config.timer_display_mode"),
						TimerDisplayMode.class,
						config.timerDisplayMode
				)
				.setDefaultValue(TimerDisplayMode.SECONDS)
				.setEnumNameProvider(value -> ((TimerDisplayMode) value).getDisplayName())
				.setTooltip(Text.translatable("text.pearl-landing-timer.config.timer_display_mode.tooltip"))
				.setSaveConsumer(value -> config.timerDisplayMode = value)
				.build());

		hud.addEntry(entries.startTextDescription(Text.translatable("text.pearl-landing-timer.config.position_hint")).build());

		return builder.build();
	}
}
