package com.github.burgerguy.hudtweaks.gui.element;

import com.github.burgerguy.hudtweaks.gui.HudElement;

import net.minecraft.client.MinecraftClient;

public class ExperienceBarElement extends HudElement {
	// TODO: add force display option somewhere

	public ExperienceBarElement() {
		super("expbar");
	}

	@Override
	public int calculateWidth(MinecraftClient client) {
		return 182;
	}

	@Override
	public int calculateHeight(MinecraftClient client) {
		// TODO: is this right?
		return 12;
	}

	@Override
	public int calculateDefaultX(MinecraftClient client) {
		return client.getWindow().getScaledWidth() / 2 - 91;
	}

	@Override
	public int calculateDefaultY(MinecraftClient client) {
		return client.getWindow().getScaledHeight() - 36;
	}
	
}
