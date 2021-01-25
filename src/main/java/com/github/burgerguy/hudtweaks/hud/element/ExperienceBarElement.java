package com.github.burgerguy.hudtweaks.hud.element;

import net.minecraft.client.MinecraftClient;

public class ExperienceBarElement extends HudElement {
	// TODO: add force display option somewhere

	public ExperienceBarElement() {
		super("expbar");
	}

	@Override
	protected double calculateWidth(MinecraftClient client) {
		return 182;
	}

	@Override
	protected double calculateHeight(MinecraftClient client) {
		// TODO: is this right?
		return 12;
	}

	@Override
	protected double calculateDefaultX(MinecraftClient client) {
		return client.getWindow().getScaledWidth() / 2 - 91;
	}

	@Override
	protected double calculateDefaultY(MinecraftClient client) {
		return client.getWindow().getScaledHeight() - 36;
	}
	
	@Override
	protected boolean isVisible(MinecraftClient client) {
		return true;
	}	
}
