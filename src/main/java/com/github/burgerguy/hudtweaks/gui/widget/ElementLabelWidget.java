package com.github.burgerguy.hudtweaks.gui.widget;

import org.jetbrains.annotations.Nullable;

import com.github.burgerguy.hudtweaks.hud.element.HudElement;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;

public class ElementLabelWidget implements Drawable {
	private static final Style STYLE = Style.EMPTY.withItalic(true);
	
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
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
		if (element == null) {
			DrawableHelper.drawCenteredText(matrices, textRenderer, new TranslatableText("hudtweaks.options.current_element.blank.display").setStyle(STYLE), x, y, 0xCCB0B0B0);
		} else {
			DrawableHelper.drawCenteredText(matrices, textRenderer, new LiteralText(textRenderer.getTextHandler().trimToWidth(element.getIdentifier(), maxWidth, STYLE)).setStyle(STYLE), x, y, 0xCCFFFFFF);
		}
	}
	
	public void setHudElement(@Nullable HudElement element) {
		this.element = element;
	}
}

