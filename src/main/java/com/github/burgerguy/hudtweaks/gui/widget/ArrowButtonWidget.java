package com.github.burgerguy.hudtweaks.gui.widget;

import com.github.burgerguy.hudtweaks.HudTweaksMod;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ArrowButtonWidget extends ButtonWidget {
	private static final Identifier ARROWS_LOCATION = new Identifier(HudTweaksMod.MOD_ID, "arrows.png");
	private static final int TEXTURE_SIZE = 16;
	private final boolean isLeft;

	public ArrowButtonWidget(int x, int y, boolean isLeft, Text message, PressAction onPress) {
		super(x, y, TEXTURE_SIZE, TEXTURE_SIZE, message, onPress);
		this.isLeft = isLeft;
	}

	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, ARROWS_LOCATION);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		DrawableHelper.drawTexture(matrices, x, y, isLeft ? 0 : width, isHovered() ? height : 0, width, height, 32, 32);
	}
}
