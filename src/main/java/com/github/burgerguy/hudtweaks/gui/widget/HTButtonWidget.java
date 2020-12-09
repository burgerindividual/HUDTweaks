package com.github.burgerguy.hudtweaks.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public abstract class HTButtonWidget extends AbstractPressableButtonWidget {
	
	public HTButtonWidget(int x, int y, int width, int height, Text message) {
		super(x, y, width, height, message);
		setAlpha(0.8F);
	}

	@Override
	public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		TextRenderer textRenderer = minecraftClient.textRenderer;
		int x1 = x;
		int y1 = y;
		int x2 = x + width;
		int y2 = y + height;
		int color = isHovered() ? 0xFFFFFFFF : 0xFF000000;
		DrawableHelper.fill(matrixStack, x1,     y1,     x2,     y1 + 1, color);
		DrawableHelper.fill(matrixStack, x1,     y2,     x2,     y2 - 1, color);
		DrawableHelper.fill(matrixStack, x1,     y1 + 1, x1 + 1, y2 - 1, color);
		DrawableHelper.fill(matrixStack, x2,     y1 + 1, x2 - 1, y2 - 1, color);
		renderBg(matrixStack, minecraftClient, mouseX, mouseY);
		int j = active ? 0x00FFFFFF : 0x00A0A0A0;
		drawCenteredText(matrixStack, textRenderer, getMessage(), x + width / 2, y + (height - 8) / 2, j | MathHelper.ceil(alpha * 255.0F) << 24);
		if (!active) {
			DrawableHelper.fill(matrixStack, x1, y1, x2, y2, 0x50303030);
		}
	}
}
