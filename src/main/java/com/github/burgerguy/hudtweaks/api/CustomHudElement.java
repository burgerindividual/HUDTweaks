package com.github.burgerguy.hudtweaks.api;

import com.github.burgerguy.hudtweaks.hud.HudContainer;
import com.github.burgerguy.hudtweaks.hud.element.HTIdentifier;
import com.github.burgerguy.hudtweaks.hud.element.HudElementEntry;

public abstract class CustomHudElement extends HudElementEntry {
	public CustomHudElement(HTIdentifier identifier, MatrixUpdater updater, String... updateEvents) {
		super(identifier, updateEvents);
		updater.fillRunnables(ms -> HudContainer.getMatrixCache().tryPushMatrix(identifier, ms),
				ms -> HudContainer.getMatrixCache().tryPopMatrix(identifier, ms));
	}
}
