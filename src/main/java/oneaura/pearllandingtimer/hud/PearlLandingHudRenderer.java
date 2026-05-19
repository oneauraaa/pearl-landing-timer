package oneaura.pearllandingtimer.hud;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import oneaura.pearllandingtimer.config.PearlLandingTimerConfig;
import oneaura.pearllandingtimer.config.TimerDisplayMode;
import oneaura.pearllandingtimer.tracking.PearlTracker;

import java.util.Locale;

public class PearlLandingHudRenderer implements HudElement {
	private static final ItemStack PEARL_STACK = new ItemStack(Items.ENDER_PEARL);
	private static final int PADDING_X = 6;
	private static final int PADDING_Y = 5;
	private static final int ICON_SIZE = 16;
	private static final int ICON_GAP = 5;
	private static final int BACKGROUND_COLOR = 0x99000000;
	private static final int TEXT_COLOR = 0xFFF4F7FB;

	private final PearlLandingTimerConfig config;
	private final PearlTracker pearlTracker;

	public PearlLandingHudRenderer(PearlLandingTimerConfig config, PearlTracker pearlTracker) {
		this.config = config;
		this.pearlTracker = pearlTracker;
	}

	@Override
	public void render(DrawContext context, RenderTickCounter tickCounter) {
		if (!config.hudEnabled || !pearlTracker.hasPrediction()) {
			return;
		}

		String text = formatTimer(pearlTracker.getPredictedTicks(), tickCounter.getTickProgress(false));
		renderPanel(context, config, text);
	}

	public static void renderPanel(DrawContext context, PearlLandingTimerConfig config, String text) {
		MinecraftClient client = MinecraftClient.getInstance();
		TextRenderer textRenderer = client.textRenderer;
		int width = getPanelWidth(textRenderer, config, text);
		int height = getPanelHeight();
		int x = config.getHudX(width, context.getScaledWindowWidth());
		int y = config.getHudY(height, context.getScaledWindowHeight());

		if (config.showBackground) {
			context.fill(x, y, x + width, y + height, BACKGROUND_COLOR);
		}

		int textX = x + PADDING_X;
		if (config.showPearlIcon) {
			context.drawItemWithoutEntity(PEARL_STACK, x + PADDING_X, y + PADDING_Y);
			textX += ICON_SIZE + ICON_GAP;
		}

		int textY = y + (height - textRenderer.fontHeight) / 2;
		context.drawTextWithShadow(textRenderer, text, textX, textY, TEXT_COLOR);
	}

	public static int getPanelWidth(TextRenderer textRenderer, PearlLandingTimerConfig config, String text) {
		int iconWidth = config.showPearlIcon ? ICON_SIZE + ICON_GAP : 0;
		return PADDING_X + iconWidth + textRenderer.getWidth(text) + PADDING_X;
	}

	public static int getPanelHeight() {
		return ICON_SIZE + PADDING_Y * 2;
	}

	public static String formatTimer(int ticks, float tickProgress) {
		PearlLandingTimerConfig config = oneaura.pearllandingtimer.PearlLandingTimerClient.getConfig();
		if (config.timerDisplayMode == TimerDisplayMode.TICKS) {
			return Math.max(0, ticks) + "t";
		}

		float seconds = Math.max(0.0F, (ticks - tickProgress) / 20.0F);
		return String.format(Locale.ROOT, "%.2fs", seconds);
	}
}
