package com.github.burgerguy.hudtweaks.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class HudTweaksButtonWidget extends AbstractButtonWidget {
	
	public HudTweaksButtonWidget(int x, int y, int width, int height, Text message) {
		super(x, y, width, height, message);
		this.setAlpha(0.8F);
	}

	@Override
	public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		TextRenderer textRenderer = minecraftClient.textRenderer;
		int x1 = this.x;
		int y1 = this.y;
		int x2 = this.x + this.width;
		int y2 = this.y + this.height;
		int color = this.isHovered() ? 0xFFFFFFFF : 0xFF000000;
		DrawableHelper.fill(matrixStack, x1,     y1,     x2,     y1 + 1, color);
		DrawableHelper.fill(matrixStack, x1,     y2,     x2,     y2 - 1, color);
		DrawableHelper.fill(matrixStack, x1,     y1 + 1, x1 + 1, y2 - 1, color);
		DrawableHelper.fill(matrixStack, x2,     y1 + 1, x2 - 1, y2 - 1, color);
		this.renderBg(matrixStack, minecraftClient, mouseX, mouseY);
		int j = this.active ? 0x00FFFFFF : 0x00A0A0A0;
		drawCenteredText(matrixStack, textRenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
		if (!this.active) DrawableHelper.fill(matrixStack, x1, y1, x2, y2, 0x50303030);
	}
}
