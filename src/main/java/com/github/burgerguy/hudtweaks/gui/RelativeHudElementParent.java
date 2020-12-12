package com.github.burgerguy.hudtweaks.gui;

import net.minecraft.client.MinecraftClient;

public class RelativeHudElementParent implements RelativeParent {
	private final HudElement element;
	private final boolean isX;
	
	public RelativeHudElementParent(HudElement element, boolean isX) { // TODO: figure out when to update
		this.element = element;
		this.isX = isX;
	}
	
	@Override
	public String getIdentifier() {
		return element.getIdentifier();
	}

	@Override
	public int getPosition(MinecraftClient client) {
		if (isX) {
			return element.getXPosHelper().calculateScreenPos(element.getWidth(client), element.getDefaultX(client), client);
		} else {
			return element.getYPosHelper().calculateScreenPos(element.getHeight(client), element.getDefaultY(client), client);
		}
	}

	@Override
	public int getDimension(MinecraftClient client) {
		return isX ? element.getWidth(client) : element.getHeight(client);
	}
}
