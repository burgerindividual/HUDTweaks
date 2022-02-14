package com.github.burgerguy.hudtweaks.api;

import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import com.github.burgerguy.hudtweaks.hud.element.HudElement;
import com.github.burgerguy.hudtweaks.util.RenderStateUtil;

public abstract class CustomHudElement extends HudElement {
	public CustomHudElement(HTIdentifier identifier, RenderStateUpdater updater, String... updateEvents) {
		super(identifier, updateEvents);
		updater.fill(ms -> RenderStateUtil.tryStartRender(this, ms),
				ms -> RenderStateUtil.tryFinishRender(this, ms));
	}
}
