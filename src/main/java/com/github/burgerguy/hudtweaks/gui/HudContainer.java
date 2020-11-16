package com.github.burgerguy.hudtweaks.gui;

import java.awt.Point;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.github.burgerguy.hudtweaks.gui.HudPosHelper.Anchor;

public class HudContainer {
	private static Map<String, HudElement> elementMap = new HashMap<>();
	
	static {
		HudElement hotbar = new HudElement("hotbar", 182, 22, (w, h) -> new Point((w / 2) - 91, h - 22));
		addElement(hotbar.getIdentifier(), hotbar);
		hotbar.getXPosHelper().setAnchor(Anchor.MINIMUM);
		hotbar.getYPosHelper().setAnchor(Anchor.MINIMUM);
		hotbar.getXPosHelper().setRelativePos(0);
		hotbar.getYPosHelper().setRelativePos(0);
		hotbar.getXPosHelper().setOffset(5);
		hotbar.getYPosHelper().setOffset(5);
	}

	public static HudElement getElement(String identifier) {
		return elementMap.get(identifier);
	}
	
	public static Collection<HudElement> getElements() {
		return elementMap.values();
	}
	
	public static void addElement(String identifier, HudElement element) {
		elementMap.put(identifier, element);
	}
}
