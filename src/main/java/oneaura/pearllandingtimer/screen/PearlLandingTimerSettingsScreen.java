package oneaura.pearllandingtimer.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import oneaura.pearllandingtimer.PearlLandingTimerClient;
import oneaura.pearllandingtimer.config.PearlLandingTimerConfig;
import oneaura.pearllandingtimer.config.TimerDisplayMode;

public class PearlLandingTimerSettingsScreen extends Screen {
	private final Screen parent;
	private final PearlLandingTimerConfig config;

	public PearlLandingTimerSettingsScreen(Screen parent) {
		super(Text.translatable("text.pearl-landing-timer.settings.title"));
		this.parent = parent;
		this.config = PearlLandingTimerClient.getConfig();
	}

	@Override
	protected void init() {
		int buttonWidth = 180;
		int buttonX = (width - buttonWidth) / 2;
		int buttonY = height / 2 - 64;

		addDrawableChild(ButtonWidget.builder(hudEnabledText(), button -> {
						config.hudEnabled = !config.hudEnabled;
						config.save();
						button.setMessage(hudEnabledText());
					})
				.dimensions(buttonX, buttonY, buttonWidth, 20)
				.build());

		addDrawableChild(ButtonWidget.builder(iconText(), button -> {
						config.showPearlIcon = !config.showPearlIcon;
						config.save();
						button.setMessage(iconText());
					})
				.dimensions(buttonX, buttonY + 24, buttonWidth, 20)
				.build());

		addDrawableChild(ButtonWidget.builder(backgroundText(), button -> {
						config.showBackground = !config.showBackground;
						config.save();
						button.setMessage(backgroundText());
					})
				.dimensions(buttonX, buttonY + 48, buttonWidth, 20)
				.build());

		addDrawableChild(ButtonWidget.builder(timerModeText(), button -> {
						config.timerDisplayMode = config.timerDisplayMode == TimerDisplayMode.SECONDS
								? TimerDisplayMode.TICKS
								: TimerDisplayMode.SECONDS;
						config.save();
						button.setMessage(timerModeText());
					})
				.dimensions(buttonX, buttonY + 72, buttonWidth, 20)
				.build());

		addDrawableChild(ButtonWidget.builder(
						Text.translatable("text.pearl-landing-timer.settings.edit_position"),
						button -> client.setScreen(new HudPositionScreen(this))
				)
				.dimensions(buttonX, buttonY + 106, buttonWidth, 20)
				.build());

		addDrawableChild(ButtonWidget.builder(
						Text.translatable("gui.done"),
						button -> close()
				)
				.dimensions(buttonX, buttonY + 140, buttonWidth, 20)
				.build());
	}

	private Text hudEnabledText() {
		return toggleText("text.pearl-landing-timer.config.hud_enabled", config.hudEnabled);
	}

	private Text iconText() {
		return toggleText("text.pearl-landing-timer.config.show_pearl_icon", config.showPearlIcon);
	}

	private Text backgroundText() {
		return toggleText("text.pearl-landing-timer.config.show_background", config.showBackground);
	}

	private Text timerModeText() {
		return Text.translatable("text.pearl-landing-timer.settings.timer_mode_button", config.timerDisplayMode.getDisplayName());
	}

	private static Text toggleText(String key, boolean enabled) {
		return Text.translatable("text.pearl-landing-timer.settings.toggle", Text.translatable(key), enabled ? Text.translatable("options.on") : Text.translatable("options.off"));
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		context.fill(0, 0, width, height, 0xC0101010);
		context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 32, 0xFFFFFF);
		super.render(context, mouseX, mouseY, delta);
	}

	@Override
	public void close() {
		client.setScreen(parent);
	}
}
