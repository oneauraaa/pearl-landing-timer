package com.selim.pearllandingtimer;

import net.minecraft.client.gui.Click;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public final class PearlHudEditorScreen extends Screen {
    private static final int PADDING = 4;

    private final PearlLandingTimerConfig config;
    private int previewX;
    private int previewY;
    private boolean dragging;
    private int dragOffsetX;
    private int dragOffsetY;

    public PearlHudEditorScreen(PearlLandingTimerConfig config) {
        super(Text.translatable("screen.pearl-landing-timer.edit_hud"));
        this.config = config;
    }

    @Override
    protected void init() {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        Text preview = previewText();
        int previewWidth = textRenderer.getWidth(preview);
        previewX = config.hudX < 0 ? (width - previewWidth) / 2 : clamp(config.hudX, 0, Math.max(0, width - previewWidth));
        previewY = config.hudY < 0 ? height / 2 + 18 : clamp(config.hudY, 0, Math.max(0, height - textRenderer.fontHeight));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        Text title = getTitle();
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 16, 0xFFFFFF);

        Text preview = previewText();
        int previewWidth = textRenderer.getWidth(preview);
        int previewHeight = textRenderer.fontHeight;
        context.fill(previewX - PADDING, previewY - PADDING, previewX + previewWidth + PADDING, previewY + previewHeight + PADDING, 0x99000000);
        context.drawTextWithShadow(textRenderer, preview, previewX, previewY, 0xFFFFFF);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (click.button() == 0 && isOverPreview(click.x(), click.y())) {
            dragging = true;
            dragOffsetX = (int) click.x() - previewX;
            dragOffsetY = (int) click.y() - previewY;
            return true;
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(Click click, double deltaX, double deltaY) {
        if (dragging && click.button() == 0) {
            movePreview((int) click.x() - dragOffsetX, (int) click.y() - dragOffsetY);
            return true;
        }
        return super.mouseDragged(click, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(Click click) {
        if (click.button() == 0 && dragging) {
            dragging = false;
            savePosition();
            return true;
        }
        return super.mouseReleased(click);
    }

    @Override
    public void removed() {
        savePosition();
    }

    private void movePreview(int x, int y) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int previewWidth = textRenderer.getWidth(previewText());
        previewX = clamp(x, 0, Math.max(0, width - previewWidth));
        previewY = clamp(y, 0, Math.max(0, height - textRenderer.fontHeight));
    }

    private boolean isOverPreview(double mouseX, double mouseY) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int previewWidth = textRenderer.getWidth(previewText());
        int previewHeight = textRenderer.fontHeight;
        return mouseX >= previewX - PADDING
                && mouseX <= previewX + previewWidth + PADDING
                && mouseY >= previewY - PADDING
                && mouseY <= previewY + previewHeight + PADDING;
    }

    private void savePosition() {
        config.hudX = previewX;
        config.hudY = previewY;
        config.save();
    }

    private static Text previewText() {
        return Text.translatable("screen.pearl-landing-timer.no_prediction");
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
