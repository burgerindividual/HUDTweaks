package com.github.burgerguy.hudtweaks.hud.element;

import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import com.github.burgerguy.hudtweaks.mixin.InGameHudAccessor;
import com.github.burgerguy.hudtweaks.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;

public class DefaultSubtitleElement extends HudElement {
	public static final HTIdentifier IDENTIFIER = new HTIdentifier(Util.MINECRAFT_MODID, new HTIdentifier.ElementId("subtitle", "hudtweaks.element.subtitle"));
	private static final int SCALE = 2;
	private static final int Y_OFFSET = 5;

	public DefaultSubtitleElement() {
		super(IDENTIFIER, "onSubtitleTextChange");
	}
	
	@Override
	protected float calculateWidth(MinecraftClient client) {
		Text titleText = ((InGameHudAccessor) client.inGameHud).getSubtitleText();
		if (titleText != null) {
			return client.textRenderer.getWidth(titleText) * SCALE;
		}
		return 56;
	}
	
	@Override
	protected float calculateHeight(MinecraftClient client) {
		return client.textRenderer.fontHeight * SCALE;
	}
	
	@Override
	protected float calculateDefaultX(MinecraftClient client) {
		return (client.getWindow().getScaledWidth() - getWidth()) / 2.0f;
	}
	
	@Override
	protected float calculateDefaultY(MinecraftClient client) {
		return client.getWindow().getScaledHeight() / 2.0f + Y_OFFSET * SCALE;
	}

	@Override
	// TODO: X scaling is weird here, scales from middle rather than left side
	public Matrix4f createMatrix() {
		Matrix4f matrix = Matrix4f.scale(xScale, yScale, 1);
		matrix.multiply(Matrix4f.translate((getX() - getDefaultX()) / SCALE / xScale,
				((getY() - getDefaultY()) / SCALE / yScale) - (Y_OFFSET - Y_OFFSET / yScale), 1));
		parentNode.setUpdated();
		return matrix;
	}
}
