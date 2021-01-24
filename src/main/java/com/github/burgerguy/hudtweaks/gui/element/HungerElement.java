package com.github.burgerguy.hudtweaks.gui.element;

import com.github.burgerguy.hudtweaks.gui.HudElement;

import net.minecraft.client.MinecraftClient;

public class HungerElement extends HudElement {
	// TODO: add force display option somewhere, maybe not in here

	public HungerElement() {
		super("hunger");
	}

	@Override
	protected double calculateWidth(MinecraftClient client) {
		return 81;
	}

	@Override
	protected double calculateHeight(MinecraftClient client) {
		return 9 + 2; // the +2 is for the possible jump distance of the food
	}

	@Override
	protected double calculateDefaultX(MinecraftClient client) {
		return client.getWindow().getScaledWidth() / 2 + 10;
	}

	@Override
	protected double calculateDefaultY(MinecraftClient client) {
		return client.getWindow().getScaledHeight() - 39 - 1;  // the -1 is for the possible jump distance of the food
	}
	
	@Override
	protected boolean isVisible(MinecraftClient client) {
		return true;
	}
}
