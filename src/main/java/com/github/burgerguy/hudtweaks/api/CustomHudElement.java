package com.github.burgerguy.hudtweaks.api;

import com.github.burgerguy.hudtweaks.hud.HudContainer;
import com.github.burgerguy.hudtweaks.hud.element.HudElement;

public abstract class CustomHudElement extends HudElement {
	public CustomHudElement(String identifier, MatrixUpdater updater, String... updateEvents) {
		super(identifier, updateEvents);
		updater.fillRunnables(ms -> HudContainer.getMatrixCache().tryPushMatrix(identifier, ms),
				ms -> HudContainer.getMatrixCache().tryPopMatrix(identifier, ms));
	}
}
