package com.github.burgerguy.hudtweaks.util.gui;

import com.github.burgerguy.hudtweaks.util.gui.MatrixCache.UpdateEvent;

import net.minecraft.client.MinecraftClient;

public final class RelativeTreeRootScreen extends RelativeTreeNode {
	public static final String IDENTIFIER = "screen";

	public RelativeTreeRootScreen() {
		super(IDENTIFIER, UpdateEvent.ON_SCREEN_BOUNDS_CHANGE);
		xParent = null;
		yParent = null;
	}

	@Override
	public int getX(MinecraftClient client) {
		return 0;
	}

	@Override
	public int getWidth(MinecraftClient client) {
		return client.getWindow().getScaledWidth();
	}

	@Override
	public int getY(MinecraftClient client) {
		return 0;
	}

	@Override
	public int getHeight(MinecraftClient client) {
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
