package oneaura.pearllandingtimer.screen;

import net.minecraft.client.gui.Click;
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
	private boolean dragging;
	private int dragOffsetX;
	private int dragOffsetY;

	public HudPositionScreen(Screen parent) {
		super(Text.translatable("text.pearl-landing-timer.position.title"));
		this.parent = parent;
		this.config = PearlLandingTimerClient.getConfig();
	}

	@Override
	protected void init() {
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

	@Override
	public boolean mouseClicked(Click click, boolean doubled) {
		int hudWidth = PearlLandingHudRenderer.getPanelWidth(textRenderer, config, PREVIEW_TEXT);
		int hudHeight = PearlLandingHudRenderer.getPanelHeight();
		int hudX = config.getHudX(hudWidth, width);
		int hudY = config.getHudY(hudHeight, height);

		if (click.button() == 0
				&& click.x() >= hudX
				&& click.x() <= hudX + hudWidth
				&& click.y() >= hudY
				&& click.y() <= hudY + hudHeight) {
			dragging = true;
			dragOffsetX = (int) click.x() - hudX;
			dragOffsetY = (int) click.y() - hudY;
			return true;
		}

		return super.mouseClicked(click, doubled);
	}

	@Override
	public boolean mouseDragged(Click click, double deltaX, double deltaY) {
		if (dragging) {
			int hudWidth = PearlLandingHudRenderer.getPanelWidth(textRenderer, config, PREVIEW_TEXT);
			int hudHeight = PearlLandingHudRenderer.getPanelHeight();
			config.setHudPosition((int) click.x() - dragOffsetX, (int) click.y() - dragOffsetY, hudWidth, hudHeight, width, height);
			return true;
		}

		return super.mouseDragged(click, deltaX, deltaY);
	}

	@Override
	public boolean mouseReleased(Click click) {
		if (dragging && click.button() == 0) {
			dragging = false;
			config.save();
			return true;
		}

		return super.mouseReleased(click);
	}

	@Override
	public void close() {
		config.save();
		client.setScreen(parent);
	}
}
