package com.github.burgerguy.hudtweaks.hud.element;

import net.minecraft.client.MinecraftClient;

public class JumpBarElement extends HudElement {
	
	public JumpBarElement() {
		super("jumpbar");
	}

	@Override
	protected double calculateWidth(MinecraftClient client) {
		return 182;
	}

	@Override
	protected double calculateHeight(MinecraftClient client) {
		return 5;
	}

	@Override
	protected double calculateDefaultX(MinecraftClient client) {
		return client.getWindow().getScaledWidth() / 2 - 91;
	}

	@Override
	protected double calculateDefaultY(MinecraftClient client) {
		return client.getWindow().getScaledHeight() - 29;
	}
}
