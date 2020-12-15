package com.github.burgerguy.hudtweaks.gui.element;

import com.github.burgerguy.hudtweaks.gui.HudElement;

import net.minecraft.client.MinecraftClient;

public class HungerElement extends HudElement {
	// TODO: add force display option somewhere, maybe not in here

	public HungerElement() {
		super("hunger");
	}

	@Override
	public int calculateWidth(MinecraftClient client) {
		return 81;
	}

	@Override
	public int calculateHeight(MinecraftClient client) {
		return 9 + 2; // the +2 is for the possible jump distance of the food
	}

	@Override
	public int calculateDefaultX(MinecraftClient client) {
		return client.getWindow().getScaledWidth() / 2 + 10;
	}

	@Override
	public int calculateDefaultY(MinecraftClient client) {
		return client.getWindow().getScaledHeight() - 39 - 1;  // the -1 is for the possible jump distance of the food
	}
	
}
