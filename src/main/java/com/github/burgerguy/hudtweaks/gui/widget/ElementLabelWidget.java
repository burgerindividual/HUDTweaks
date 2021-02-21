package com.github.burgerguy.hudtweaks.gui.widget;

import org.jetbrains.annotations.Nullable;

import com.github.burgerguy.hudtweaks.hud.element.HudElementEntry;
import com.github.burgerguy.hudtweaks.util.gui.OverflowTextRenderer;

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
	private final OverflowTextRenderer overflowTextRenderer;
	private HudElementEntry element;
	
	public ElementLabelWidget(int x, int y, int maxWidth) {
		this.x = x;
		this.y = y;
		this.overflowTextRenderer = new OverflowTextRenderer(30, 30, 4, x, y, maxWidth);
	}

	@SuppressWarnings("resource")
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
		if (element == null) {
			DrawableHelper.drawCenteredText(matrices, textRenderer, new TranslatableText("hudtweaks.options.current_element.blank.display").setStyle(STYLE), x, y, 0xCCB0B0B0);
		} else {
			overflowTextRenderer.render(matrices, textRenderer, new LiteralText(element.getIdentifier().getElementType().toTranslatedString()).setStyle(STYLE), delta, 0xCCFFFFFF);
		}
	}
	
	public void setHudElement(@Nullable HudElementEntry element) {
		this.element = element;
		overflowTextRenderer.restart();
	}
}

