package com.github.burgerguy.hudtweaks.hud.tree;

import java.util.Collections;
import java.util.List;

import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import com.github.burgerguy.hudtweaks.util.Util;

import net.minecraft.client.MinecraftClient;

public final class RelativeTreeRootScreen extends AbstractTypeNode {
	public static final HTIdentifier IDENTIFIER = new HTIdentifier(new HTIdentifier.ElementType("screen", "hudtweaks.element.screen"), Util.MINECRAFT_NAMESPACE);
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
		private int width, height;

		public ScreenEntry() {
			super(IDENTIFIER, "onScreenBoundsChange");
		}
		
		@Override
		public double getX() {
			return 0;
		}

		@Override
		public double getWidth() {
			return width;
		}

		@Override
		public double getY() {
			return 0;
		}

		@Override
		public double getHeight() {
			return height;
		}
		
		@Override
		public void updateSelfX(MinecraftClient client) {
			width = client.getWindow().getScaledWidth();
		}

		@Override
		public void updateSelfY(MinecraftClient client) {
			height = client.getWindow().getScaledHeight();
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
