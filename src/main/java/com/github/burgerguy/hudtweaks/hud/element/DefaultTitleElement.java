package com.github.burgerguy.hudtweaks.hud.element;

import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import com.github.burgerguy.hudtweaks.mixin.InGameHudAccessor;
import com.github.burgerguy.hudtweaks.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

public class DefaultTitleElement extends HudElement {
	public static final HTIdentifier IDENTIFIER = new HTIdentifier(Util.MINECRAFT_MODID, new HTIdentifier.ElementId("title", "hudtweaks.element.title"));
	private static final float SCALE = 4.0F;
	private static final float Y_OFFSET = -10.0F;

	public DefaultTitleElement() {
		super(IDENTIFIER, "onTitleTextChange");
	}
	
	@Override
	protected float calculateWidth(MinecraftClient client) {
		Text titleText = ((InGameHudAccessor) client.inGameHud).getTitleText();
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
		return (client.getWindow().getScaledWidth() - getWidth()) / 2.0F;
	}
	
	@Override
	protected float calculateDefaultY(MinecraftClient client) {
		return client.getWindow().getScaledHeight() / 2.0f + Y_OFFSET * SCALE;
	}

	@Override
	// TODO: X scaling is weird here, scales from middle rather than left side
	protected void createMatrix() { // TODO: rotation broken on this, also has weird offset
		Quaternion quaternion = new Quaternion(Vec3f.POSITIVE_Z, rotationDegrees, true);
		Matrix4f matrix = Matrix4f.translate(getX() / SCALE, getY() / SCALE, 0);
		matrix.multiply(Matrix4f.translate(getXRotationAnchor() * getWidth(), getYRotationAnchor() * getHeight(), 0));
		matrix.multiply(quaternion);
		matrix.multiply(Matrix4f.translate(-getXRotationAnchor() * getWidth(), -getYRotationAnchor() * getHeight(), 0));
		matrix.multiply(Matrix4f.translate(-getDefaultX() / SCALE, (-getDefaultY() / SCALE) + Y_OFFSET, 0));
		matrix.multiply(Matrix4f.scale(xScale, yScale, 1));
		matrix.multiply(Matrix4f.translate(0, -Y_OFFSET, 0));
		cachedMatrix = matrix;
	}
}
