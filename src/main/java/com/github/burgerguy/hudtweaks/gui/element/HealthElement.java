package com.github.burgerguy.hudtweaks.gui.element;

import java.awt.Point;

import com.github.burgerguy.hudtweaks.gui.HudElement;
import com.github.burgerguy.hudtweaks.util.gui.MatrixCache.UpdateEvent;
import com.google.gson.JsonElement;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.MathHelper;

public class HealthElement extends HudElement {
	private boolean flipped;

	public HealthElement() {
		super("health", UpdateEvent.ON_SCREEN_BOUNDS_CHANGE, UpdateEvent.ON_HEALTH_ROWS_CHANGE);
	}

	@Override
	public int getWidth(MinecraftClient client) {
		return 81;
	}
	
	private int getRawHeight(MinecraftClient client) {
		double maxHealth = client.player.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
		int absorption = MathHelper.ceil(client.player.getAbsorptionAmount());
		int healthRows = MathHelper.ceil((maxHealth + absorption) / 2.0D / 10.0D);
		return (healthRows - 1) * Math.max(10 - (healthRows - 2), 3);
	}
	
	private int getHeartJumpDistance(MinecraftClient client) {
		if (flipped || client == null || client.player == null) {
			return 2;
		} else {
			// absorption hearts don't jump, so if we know the top row
			// is only absorption hearts, the distance will be 0.
			int heartsInTopRow = MathHelper.ceil(client.player.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH) / 2.0D) % 10;
			int absorption = MathHelper.ceil(client.player.getAbsorptionAmount());
			if (heartsInTopRow == 0) {
				return (absorption > 0) ? 0 : 2;
			} else {
				return (heartsInTopRow + absorption > 10) ? 0 : 2;
			}
		}
	}

	@Override
	public int getHeight(MinecraftClient client) {
		if (client != null && client.player != null) {
			return getRawHeight(client) + 9 + getHeartJumpDistance(client); // +9 because of the base heart height
		} else {
			return 9;
		}
	}

	@Override
	public Point calculateDefaultCoords(MinecraftClient client) {// FIXME
		return new Point((client.getWindow().getScaledWidth() / 2) - 91, client.getWindow().getScaledHeight() - 39 - (flipped || client == null || client.player == null ? 0 : getRawHeight(client)) - getHeartJumpDistance(client));
	}
	
	public boolean isFlipped() {
		return flipped;
	}
	
	public void setFlipped(boolean flipped) {
		this.flipped = flipped;
	}
	
	@Override
	public void updateFromJson(JsonElement json) {
		super.updateFromJson(json);
		setFlipped(json.getAsJsonObject().get("flipped").getAsBoolean());
	}
	
}
