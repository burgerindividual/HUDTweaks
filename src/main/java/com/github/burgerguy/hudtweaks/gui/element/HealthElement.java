package com.github.burgerguy.hudtweaks.gui.element;

import java.awt.Point;

import com.github.burgerguy.hudtweaks.gui.HudElement;
import com.github.burgerguy.hudtweaks.util.gui.MatrixCache.UpdateEvent;
import com.google.gson.JsonElement;

import net.minecraft.client.MinecraftClient;

public class HealthElement extends HudElement {
	private boolean flipped;

	public HealthElement() {
		super("health", UpdateEvent.ON_SCREEN_BOUNDS_CHANGE, UpdateEvent.ON_HEALTH_ROWS_CHANGE);
	}

	@Override
	public int getWidth(MinecraftClient client) {// FIXME
		return 81;
	}

	@Override
	public int getHeight(MinecraftClient client) {// FIXME
		return 9;
	}

	@Override
	public Point calculateDefaultCoords(MinecraftClient client) {// FIXME
		return new Point((client.getWindow().getScaledWidth() / 2) - 91, client.getWindow().getScaledHeight() - 39);
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
