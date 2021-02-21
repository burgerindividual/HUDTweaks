package com.github.burgerguy.hudtweaks.gui.widget;

import com.github.burgerguy.hudtweaks.util.gui.OverflowTextRenderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public abstract class HTButtonWidget extends AbstractPressableButtonWidget {
	private final OverflowTextRenderer overflowTextRenderer;
	
	public HTButtonWidget(int x, int y, int width, int height, Text message) {
		super(x, y, width, height, message);
		setAlpha(0.8F);
		this.overflowTextRenderer = new OverflowTextRenderer(40, 40, 4, x + width / 2, y + (height - 8) / 2, width - 2);
	}

	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		TextRenderer textRenderer = minecraftClient.textRenderer;
		int x1 = x;
		int y1 = y;
		int x2 = x + width;
		int y2 = y + height;
		int color = isHovered() && active ? 0xFFFFFFFF : 0xFF000000;
		DrawableHelper.fill(matrices, x1,     y1,     x2,     y1 + 1, color);
		DrawableHelper.fill(matrices, x1,     y2,     x2,     y2 - 1, color);
		DrawableHelper.fill(matrices, x1,     y1 + 1, x1 + 1, y2 - 1, color);
		DrawableHelper.fill(matrices, x2,     y1 + 1, x2 - 1, y2 - 1, color);
		renderBg(matrices, minecraftClient, mouseX, mouseY);
		int activeColor = active ? 0x00FFFFFF : 0x00A0A0A0;
		overflowTextRenderer.render(matrices, textRenderer, getMessage(), delta, activeColor | MathHelper.ceil(alpha * 255.0F) << 24);
		if (!active) {
			DrawableHelper.fill(matrices, x1, y1, x2, y2, 0x50303030);
		}
	}
}
