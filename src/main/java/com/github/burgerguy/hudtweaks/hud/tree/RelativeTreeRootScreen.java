package com.github.burgerguy.hudtweaks.hud.tree;

import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import com.github.burgerguy.hudtweaks.util.Util;
import net.minecraft.client.MinecraftClient;

public final class RelativeTreeRootScreen extends AbstractContainerNode {
	public static final HTIdentifier IDENTIFIER = new HTIdentifier(Util.MINECRAFT_MODID, new HTIdentifier.ElementId("screen", "hudtweaks.element.screen"));
	private ScreenElement screenElement;

	public void init() {
		screenElement = new ScreenElement();
		screenElement.setContainerNode(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public AbstractElementNode getInitialElement() {
		return screenElement;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public AbstractElementNode getActiveElement() {
		return screenElement;
	}

	private static class ScreenElement extends AbstractElementNode {
		private int width, height;
		
		public ScreenElement() {
			super(IDENTIFIER, "onScreenBoundsChange");
		}

		@Override
		public float getX() {
			return 0;
		}
		
		@Override
		public float getWidth() {
			return width;
		}
		
		@Override
		public float getY() {
			return 0;
		}
		
		@Override
		public float getHeight() {
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
		public void moveXUnder(AbstractContainerNode newXParent) {
			// noop, always want to be the root node
		}

		@Override
		public void moveYUnder(AbstractContainerNode newYParent) {
			// noop, always want to be the root node
		}
	}

}
