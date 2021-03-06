package com.github.burgerguy.hudtweaks.api;

import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import com.github.burgerguy.hudtweaks.hud.HudContainer;
import com.github.burgerguy.hudtweaks.hud.element.HudElementEntry;

public abstract class CustomHudElementEntry extends HudElementEntry {
	public CustomHudElementEntry(HTIdentifier identifier, MatrixUpdater updater, String... updateEvents) {
		super(identifier, updateEvents);
		updater.fill(ms -> HudContainer.getMatrixCache().tryPushMatrix(identifier, ms),
				ms -> HudContainer.getMatrixCache().tryPopMatrix(identifier, ms),
				this::isActive);
	}
}
