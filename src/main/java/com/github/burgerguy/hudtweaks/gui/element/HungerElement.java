package com.github.burgerguy.hudtweaks.gui.element;

import java.awt.Point;

import com.github.burgerguy.hudtweaks.gui.HudElement;
import com.github.burgerguy.hudtweaks.util.gui.MatrixCache.UpdateEvent;

import net.minecraft.client.MinecraftClient;

public class HungerElement extends HudElement {
	// TODO: add force display option somewhere, maybe not in here

	public HungerElement() {
		super("hunger", UpdateEvent.ON_SCREEN_BOUNDS_CHANGE);
	}

	@Override
	public int getWidth(MinecraftClient client) {
		return 81;
	}

	@Override
	public int getHeight(MinecraftClient client) {
		return 9;
	}

	@Override
	public Point calculateDefaultCoords(MinecraftClient client) {
		return new Point((client.getWindow().getScaledWidth() / 2) + 10, client.getWindow().getScaledHeight() - 39);
	}
	
}
