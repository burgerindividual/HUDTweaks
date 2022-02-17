package com.github.burgerguy.hudtweaks.gui.widget;

import com.github.burgerguy.hudtweaks.util.gl.GLUtil;
import com.github.burgerguy.hudtweaks.util.gui.OverflowTextRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public abstract class HTButtonWidget extends PressableWidget {
	protected final OverflowTextRenderer overflowTextRenderer;

	public HTButtonWidget(int x, int y, int width, int height, Text message) {
		super(x, y, width, height, message);
		setAlpha(0.8f);
		overflowTextRenderer = new OverflowTextRenderer(40, 40, 4, x + width / 2, y + (height - 8) / 2, width - 2);
	}
	
	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		TextRenderer textRenderer = minecraftClient.textRenderer;
		float x1 = x;
		float y1 = y;
		float x2 = x + width;
		float y2 = y + height;
		int color = isHovered() && active ? 0xFFFFFFFF : 0xFF000000;
		GLUtil.drawFillColor(matrices, x1, y1, x2, y1 + 1.0f, color);
		GLUtil.drawFillColor(matrices, x1, y2, x2, y2 - 1.0f, color);
		GLUtil.drawFillColor(matrices, x1, y1 + 1.0f, x1 + 1.0f, y2 - 1.0f, color);
		GLUtil.drawFillColor(matrices, x2, y1 + 1.0f, x2 - 1.0f, y2 - 1.0f, color);
		renderBackground(matrices, minecraftClient, mouseX, mouseY);
		int activeColor = active ? 0x00FFFFFF : 0x00A0A0A0;
		overflowTextRenderer.render(matrices, textRenderer, getMessage(), delta, activeColor | MathHelper.ceil(alpha * 255.0f) << 24);
		if (!active) {
			GLUtil.drawFillColor(matrices, x1, y1, x2, y2, 0x50303030);
		}

		if (this.isHovered()) {
			this.renderToolTip(matrices, mouseX, mouseY);
		}
	}

	public void renderToolTip(MatrixStack matrices, int mouseX, int mouseY) {
		// TODO: add tooltips
	}

	public void appendNarrations(NarrationMessageBuilder builder) {
		// TODO: fix narration
	}
}
