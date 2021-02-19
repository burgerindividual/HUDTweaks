package com.github.burgerguy.hudtweaks.hud.element;

import com.github.burgerguy.hudtweaks.hud.RelativeTreeNode;
import com.github.burgerguy.hudtweaks.hud.XAxisNode;
import com.github.burgerguy.hudtweaks.hud.YAxisNode;

import net.minecraft.client.MinecraftClient;

public final class RelativeTreeRootScreen extends RelativeTreeNode {
	public static final HTIdentifier IDENTIFIER = new HTIdentifier(new HTIdentifier.ElementType("screen", "hudtweaks.element.screen"));

	public RelativeTreeRootScreen() {
		super(IDENTIFIER, "onScreenBoundsChange");
		xParent = null;
		yParent = null;
	}

	@Override
	public double getX(MinecraftClient client) {
		return 0;
	}

	@Override
	public double getWidth(MinecraftClient client) {
		return client.getWindow().getScaledWidth();
	}

	@Override
	public double getY(MinecraftClient client) {
		return 0;
	}

	@Override
	public double getHeight(MinecraftClient client) {
		return client.getWindow().getScaledHeight();
	}
	
	@Override
	public void moveXUnder(XAxisNode newXParent) {
		// noop, always want to be the root node
	}
	
	@Override
	public void moveYUnder(YAxisNode newXParent) {
		// noop, always want to be the root node
	}

	@Override
	public void updateSelfX(MinecraftClient client) {
		// noop, only update stuff below
	}

	@Override
	public void updateSelfY(MinecraftClient client) {
		// noop, only update stuff below
	}
	
}
