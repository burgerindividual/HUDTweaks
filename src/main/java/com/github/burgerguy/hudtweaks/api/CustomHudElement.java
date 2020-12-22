package com.github.burgerguy.hudtweaks.api;

import com.github.burgerguy.hudtweaks.gui.HudContainer;
import com.github.burgerguy.hudtweaks.gui.HudElement;

public abstract class CustomHudElement extends HudElement {
	public CustomHudElement(String identifier, String[] updateEvents, RenderStatusUpdater updater) {
		super(identifier, updateEvents);
		updater.fillRunnables(ms -> HudContainer.getMatrixCache().tryPushMatrix(identifier, ms),
				ms -> HudContainer.getMatrixCache().tryPopMatrix(identifier, ms));
	}
}
