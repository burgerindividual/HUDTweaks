package com.github.burgerguy.hudtweaks.gui.element;

import java.awt.Point;

import com.github.burgerguy.hudtweaks.gui.HudElement;
import com.github.burgerguy.hudtweaks.util.gui.MatrixCache.UpdateEvent;

import net.minecraft.client.MinecraftClient;

public class HotbarElement extends HudElement {

	public HotbarElement() {
		super("hotbar", UpdateEvent.ON_SCREEN_BOUNDS_CHANGE);
	}

	@Override
	public int getWidth(MinecraftClient client) {
		// FIXME: WORK WITH OFFHAND SLOT AND HOTBAR ATTACK INDICATOR
		return 182;
	}

	@Override
	public int getHeight(MinecraftClient client) {
		return 24;
	}

	@Override
	public Point calculateDefaultCoords(MinecraftClient client) {
		return new Point((client.getWindow().getScaledWidth() / 2) - 91, client.getWindow().getScaledHeight() - 24);
	}
	
}
