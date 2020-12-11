package com.github.burgerguy.hudtweaks.gui.element;

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
		return 9 + 2; // the +2 is for the possible jump distance of the food
	}

	@Override
	public int getDefaultX(MinecraftClient client) {
		// TODO Auto-generated method stub
		return client.getWindow().getScaledWidth() / 2 + 10;
	}

	@Override
	public int getDefaultY(MinecraftClient client) {
		return client.getWindow().getScaledHeight() - 39 - 1;  // the -1 is for the possible jump distance of the food
	}
	
}
