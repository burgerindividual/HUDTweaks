package com.github.burgerguy.hudtweaks.gui.widget;

import org.jetbrains.annotations.Nullable;

import com.github.burgerguy.hudtweaks.gui.HudElement;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

public class ElementLabelWidget implements Drawable {
	private final int x;
	private final int y;
	private final int maxWidth;
	private HudElement element;
	
	public ElementLabelWidget(int x, int y, int maxWidth) {
		this.x = x;
		this.y = y;
		this.maxWidth = maxWidth;
	}

	@SuppressWarnings("resource")
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
		TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
		if (element == null) {
			DrawableHelper.drawCenteredString(matrixStack, textRenderer, "None Selected", x, y, 0xCCB0B0B0);
		} else {
			DrawableHelper.drawCenteredString(matrixStack, textRenderer, textRenderer.trimToWidth(element.getIdentifier(), maxWidth), x, y, 0xCCFFFFFF);
		}
	}
	
	public void setHudElement(@Nullable HudElement element) {
		this.element = element;
	}
}

