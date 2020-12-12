package com.github.burgerguy.hudtweaks.gui;

import net.minecraft.client.MinecraftClient;

public class RelativeHudElementParent implements RelativeParent {
	private final HudElement element;
	private final boolean useX;
	
	public RelativeHudElementParent(HudElement element, boolean useX) { // TODO: figure out when to update
		this.element = element;
		this.useX = useX;
	}
	
	@Override
	public String getIdentifier() {
		return element.getIdentifier();
	}

	@Override
	public int getPosition(MinecraftClient client) {
		if (useX) {
			return element.getXPosHelper().calculateScreenPos(element.getWidth(client), element.getDefaultX(client), client);
		} else {
			return element.getYPosHelper().calculateScreenPos(element.getHeight(client), element.getDefaultY(client), client);
		}
	}

	@Override
	public int getDimension(MinecraftClient client) {
		return useX ? element.getWidth(client) : element.getHeight(client);
	}
}
