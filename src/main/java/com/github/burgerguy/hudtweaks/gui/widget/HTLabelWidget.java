package com.github.burgerguy.hudtweaks.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class HTLabelWidget implements Drawable {
	private final int x;
	private final int y;
	private final boolean centered;
	public int color;
	public Text text;

	public HTLabelWidget(Text text, int x, int y, int color, boolean centered) {
		this.x = x;
		this.y = y;
		this.color = color;
		this.centered = centered;
		this.text = text;
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
		TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
		if (centered) {
			DrawableHelper.drawCenteredText(matrixStack, textRenderer, text, x, y, color);
		} else {
			textRenderer.drawWithShadow(matrixStack, text, x, y, color);
		}
	}

}
