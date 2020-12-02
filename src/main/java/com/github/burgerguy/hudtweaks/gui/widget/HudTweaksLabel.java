package com.github.burgerguy.hudtweaks.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.util.math.MatrixStack;

public class HudTweaksLabel implements Drawable {
	private final int x;
	private final int y;
	private final String text;
	
	public HudTweaksLabel(String text, int x, int y) {
		this.x = x;
		this.y = y;
		this.text = text;
	}

	@SuppressWarnings("resource")
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
		MinecraftClient.getInstance().textRenderer.drawWithShadow(matrixStack, text, x, y, 0xCCFFFFFF);
	}
	
}
