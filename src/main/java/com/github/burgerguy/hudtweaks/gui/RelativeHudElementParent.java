package com.github.burgerguy.hudtweaks.gui;

import java.util.Set;

import com.github.burgerguy.hudtweaks.util.gui.MatrixCache.UpdateEvent;

import net.minecraft.client.MinecraftClient;

public class RelativeHudElementParent implements RelativeParent {
	private final HudElement element;
	private final boolean isX;
	
	public RelativeHudElementParent(HudElement element, boolean isX) {
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

	@Override
	public Set<UpdateEvent> getUpdateEvents() {
		return element.getUpdateEvents();
	}
}
