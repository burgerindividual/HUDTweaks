package com.github.burgerguy.hudtweaks.gui.widget;

import com.github.burgerguy.hudtweaks.hud.element.HudElementContainer;
import com.github.burgerguy.hudtweaks.util.gui.OverflowTextRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class ElementLabelWidget implements Drawable {
	private static final Style STYLE = Style.EMPTY.withItalic(true);

	private final int x;
	private final int y;
	private final OverflowTextRenderer overflowTextRenderer;
	private HudElementContainer elementContainer;

	public ElementLabelWidget(int x, int y, int maxWidth) {
		this.x = x;
		this.y = y;
		overflowTextRenderer = new OverflowTextRenderer(40, 40, 4, x, y, maxWidth);
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
		if (elementContainer == null) {
			DrawableHelper.drawCenteredText(matrices, textRenderer, Text.translatable("hudtweaks.options.current_element.blank.display").setStyle(STYLE), x, y, 0xCCB0B0B0);
		} else {
			overflowTextRenderer.render(matrices, textRenderer, Text.literal(elementContainer.getActiveElement().getIdentifier().toDisplayableString()).setStyle(STYLE), delta, 0xCCFFFFFF);
		}
	}

	public void setHudElementContainer(@Nullable HudElementContainer elementContainer) {
		this.elementContainer = elementContainer;
		overflowTextRenderer.restart();
	}
}

