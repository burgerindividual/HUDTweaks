package com.github.burgerguy.hudtweaks.api;

import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import com.github.burgerguy.hudtweaks.hud.HudContainer;
import com.github.burgerguy.hudtweaks.hud.element.HudElement;

public abstract class CustomHudElement extends HudElement {
	public CustomHudElement(HTIdentifier identifier, MatrixUpdater updater, String... updateEvents) {
		super(identifier, updateEvents);
		updater.fill(ms -> HudContainer.getMatrixCache().tryPushMatrix(identifier, ms),
				ms -> HudContainer.getMatrixCache().tryPopMatrix(identifier, ms));
	}
}
