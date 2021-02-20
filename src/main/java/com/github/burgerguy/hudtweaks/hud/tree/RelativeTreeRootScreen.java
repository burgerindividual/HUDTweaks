package com.github.burgerguy.hudtweaks.hud.tree;

import java.util.Collections;
import java.util.List;

import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import com.github.burgerguy.hudtweaks.util.Util;

import net.minecraft.client.MinecraftClient;

public final class RelativeTreeRootScreen extends AbstractTypeNode {
	public static final HTIdentifier IDENTIFIER = new HTIdentifier(new HTIdentifier.ElementType("screen", "hudtweaks.element.screen"), Util.HUDTWEAKS_NAMESPACE);
	private ScreenEntry screenEntry;
	
	public RelativeTreeRootScreen() {
		super(IDENTIFIER.getElementType());
	}
	
	public void init() {
		screenEntry = new ScreenEntry();
		screenEntry.setParentNode(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public AbstractTypeNodeEntry getActiveEntry() {
		return screenEntry;
	}

	@Override
	public List<AbstractTypeNodeEntry> getRawEntryList() {
		return Collections.singletonList(screenEntry);
	}
	
	private class ScreenEntry extends AbstractTypeNodeEntry {

		public ScreenEntry() {
			super(IDENTIFIER, "onScreenBoundsChange");
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
		public void updateSelfX(MinecraftClient client) {
			// noop, only update stuff below
		}

		@Override
		public void updateSelfY(MinecraftClient client) {
			// noop, only update stuff below
		}
		
		@Override
		public void moveXUnder(AbstractTypeNode newXParent) {
			// noop, always want to be the root node
		}
		
		@Override
		public void moveYUnder(AbstractTypeNode newYParent) {
			// noop, always want to be the root node
		}
	}
	
}
