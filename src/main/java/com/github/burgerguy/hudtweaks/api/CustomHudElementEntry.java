package com.github.burgerguy.hudtweaks.api;

import com.github.burgerguy.hudtweaks.hud.HudContainer;
import com.github.burgerguy.hudtweaks.hud.element.HudElementEntry;
import net.minecraft.client.MinecraftClient;

public class CustomHudElementEntry extends HudElementEntry {
	PlaceholderName wrapped;

	public CustomHudElementEntry(PlaceholderName wrapped) {
		super(wrapped.identifier, wrapped.updateEvents);
		this.wrapped = wrapped;
		wrapped.fillRunnables(ms -> HudContainer.getMatrixCache().tryPushMatrix(identifier.getElementType(), ms),
				ms -> HudContainer.getMatrixCache().tryPopMatrix(identifier.getElementType(), ms));
	}

	@Override
	protected double calculateWidth(MinecraftClient client) {
		return wrapped.calculateWidth(client);
	}

	@Override
	protected double calculateHeight(MinecraftClient client) {
		return wrapped.calculateHeight(client);
	}

	@Override
	protected double calculateDefaultX(MinecraftClient client) {
		return wrapped.calculateDefaultX(client);
	}

	@Override
	protected double calculateDefaultY(MinecraftClient client) {
		return wrapped.calculateDefaultY(client);
	}
}
