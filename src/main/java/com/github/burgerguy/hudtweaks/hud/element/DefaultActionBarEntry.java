package com.github.burgerguy.hudtweaks.hud.element;

import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import com.github.burgerguy.hudtweaks.mixin.InGameHudAccessor;
import com.github.burgerguy.hudtweaks.util.Util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;

public class DefaultActionBarEntry extends HudElementEntry {
	public static final HTIdentifier IDENTIFIER = new HTIdentifier(new HTIdentifier.ElementType("actionbar", "hudtweaks.element.actionbar"), Util.MINECRAFT_NAMESPACE);
	
	public DefaultActionBarEntry() {
		super(IDENTIFIER, "onActionBarChange");
	}

	@Override
	protected double calculateWidth(MinecraftClient client) {
		if (((InGameHudAccessor) client.inGameHud).getActionBarRemaining() - Util.getTrueTickDelta(client) > (160.0F / 255.0F)) {
			Text actionBarText = ((InGameHudAccessor) client.inGameHud).getActionBarText();
			if (actionBarText != null) {
				return client.textRenderer.getWidth(actionBarText) - 1; // compensate for lack of shadow
			}
		}
		return 14; // same default size as tooltip
	}

	@Override
	protected double calculateHeight(MinecraftClient client) {
		return client.textRenderer.fontHeight - 1; // compensate for lack of shadow
	}

	@Override
	protected double calculateDefaultX(MinecraftClient client) {
		return (client.getWindow().getScaledWidth() - (int) getWidth()) / 2;
	}

	@Override
	protected double calculateDefaultY(MinecraftClient client) {
		return client.getWindow().getScaledHeight() - 72;
	}
	
	@Override
	public Matrix4f createMatrix() {
		Matrix4f matrix = Matrix4f.scale((float) xScale, (float) yScale, 1);
		matrix.multiply(Matrix4f.translate((float) (getX() - getDefaultX() * (1 / xScale)),
				(float) ((getY() * (1 / yScale)) - (getDefaultY() * (1 / yScale))), 1));
		parentNode.setUpdated();
		return matrix;
	}
}
