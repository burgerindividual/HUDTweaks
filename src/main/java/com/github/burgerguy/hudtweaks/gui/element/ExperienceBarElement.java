package com.github.burgerguy.hudtweaks.gui.element;

import com.github.burgerguy.hudtweaks.gui.HudElement;
import com.github.burgerguy.hudtweaks.util.gui.MatrixCache.UpdateEvent;

import net.minecraft.client.MinecraftClient;

public class ExperienceBarElement extends HudElement {
	// TODO: add force display option somewhere

	public ExperienceBarElement() {
		super("expbar", UpdateEvent.ON_SCREEN_BOUNDS_CHANGE);
	}

	@Override
	public int getWidth(MinecraftClient client) {
		return 182;
	}

	@Override
	public int getHeight(MinecraftClient client) {
		// TODO: is this right?
		return 12;
	}

	@Override
	public int getDefaultX(MinecraftClient client) {
		return client.getWindow().getScaledWidth() / 2 - 91;
	}

	@Override
	public int getDefaultY(MinecraftClient client) {
		return client.getWindow().getScaledHeight() - 36;
	}
	
}
