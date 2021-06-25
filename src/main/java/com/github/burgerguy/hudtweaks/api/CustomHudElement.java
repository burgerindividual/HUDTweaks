package com.github.burgerguy.hudtweaks.api;

import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import com.github.burgerguy.hudtweaks.hud.element.HudElement;

public abstract class CustomHudElement extends HudElement {
	public CustomHudElement(HTIdentifier identifier, RenderStateUpdater updater, String... updateEvents) {
		super(identifier, updateEvents);
		updater.fill(ms -> getContainerNode().tryPushMatrix(identifier, ms),
				ms -> getContainerNode().tryPopMatrix(identifier, ms));
	}
}
