package com.github.burgerguy.hudtweaks.hud.element;

import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import com.github.burgerguy.hudtweaks.mixin.InGameHudAccessor;
import com.github.burgerguy.hudtweaks.util.Util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;

public class DefaultTitleEntry extends HudElementEntry {
	public static final HTIdentifier IDENTIFIER = new HTIdentifier(new HTIdentifier.ElementType("title", "hudtweaks.element.title"), Util.MINECRAFT_NAMESPACE);
	private static final int SCALE = 4;
	private static final int Y_OFFSET = -10;

	public DefaultTitleEntry() {
		super(IDENTIFIER, "onTitleTextChange");
	}
	
	@Override
	protected double calculateWidth(MinecraftClient client) {
		Text titleText = ((InGameHudAccessor) client.inGameHud).getTitleText();
		if (titleText != null) {
			return client.textRenderer.getWidth(titleText) * SCALE;
		}
		return 56;
	}
	
	@Override
	protected double calculateHeight(MinecraftClient client) {
		return client.textRenderer.fontHeight * SCALE;
	}
	
	@Override
	protected double calculateDefaultX(MinecraftClient client) {
		return (client.getWindow().getScaledWidth() - getWidth()) / 2.0f;
	}
	
	@Override
	protected double calculateDefaultY(MinecraftClient client) {
		return client.getWindow().getScaledHeight() / 2.0f + Y_OFFSET * SCALE;
	}

	@Override
	// TODO: X scaling is weird here, scales from middle rather than left side
	public Matrix4f createMatrix() {
		Matrix4f matrix = Matrix4f.scale((float) xScale, (float) yScale, 1);
		matrix.multiply(Matrix4f.translate((float) ((getX() - getDefaultX()) / SCALE / xScale),
				(float) ((getY() - getDefaultY()) / SCALE / yScale) - (float) (Y_OFFSET - Y_OFFSET / yScale), 1));
		parentNode.setUpdated();
		return matrix;
	}
}
