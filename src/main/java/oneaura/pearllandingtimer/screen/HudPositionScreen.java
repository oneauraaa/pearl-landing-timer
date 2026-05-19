package oneaura.pearllandingtimer.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import oneaura.pearllandingtimer.PearlLandingTimerClient;
import oneaura.pearllandingtimer.config.PearlLandingTimerConfig;
import oneaura.pearllandingtimer.hud.PearlLandingHudRenderer;

public class HudPositionScreen extends Screen {
	private static final String PREVIEW_TEXT = "1.42s";

	private final Screen parent;
	private final PearlLandingTimerConfig config;

	public HudPositionScreen(Screen parent) {
		super(Text.translatable("text.pearl-landing-timer.position.title"));
		this.parent = parent;
		this.config = PearlLandingTimerClient.getConfig();
	}

	@Override
	protected void init() {
		int buttonY = height - 82;
		addDrawableChild(ButtonWidget.builder(Text.literal("^"), button -> moveHud(0, -10))
				.dimensions(width / 2 - 10, buttonY, 20, 20)
				.build());
		addDrawableChild(ButtonWidget.builder(Text.literal("<"), button -> moveHud(-10, 0))
				.dimensions(width / 2 - 34, buttonY + 24, 20, 20)
				.build());
		addDrawableChild(ButtonWidget.builder(Text.literal(">"), button -> moveHud(10, 0))
				.dimensions(width / 2 + 14, buttonY + 24, 20, 20)
				.build());
		addDrawableChild(ButtonWidget.builder(Text.literal("v"), button -> moveHud(0, 10))
				.dimensions(width / 2 - 10, buttonY + 48, 20, 20)
				.build());
		addDrawableChild(ButtonWidget.builder(Text.translatable("gui.done"), button -> close())
				.dimensions(width / 2 - 75, height - 32, 150, 20)
				.build());
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		context.fill(0, 0, width, height, 0xC0101010);
		context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 18, 0xFFFFFF);
		context.drawCenteredTextWithShadow(
				textRenderer,
				Text.translatable("text.pearl-landing-timer.position.instructions"),
				width / 2,
				34,
				0xDADADA
		);
		PearlLandingHudRenderer.renderPanel(context, config, PREVIEW_TEXT);
		super.render(context, mouseX, mouseY, delta);
	}

	private void moveHud(int deltaX, int deltaY) {
		int hudWidth = PearlLandingHudRenderer.getPanelWidth(textRenderer, config, PREVIEW_TEXT);
		int hudHeight = PearlLandingHudRenderer.getPanelHeight();
		int hudX = config.getHudX(hudWidth, width);
		int hudY = config.getHudY(hudHeight, height);
		config.setHudPosition(hudX + deltaX, hudY + deltaY, hudWidth, hudHeight, width, height);
		config.save();
	}

	@Override
	public void close() {
		config.save();
		client.setScreen(parent);
	}
}
