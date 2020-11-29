package com.github.burgerguy.hudtweaks.gui;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.github.burgerguy.hudtweaks.gui.element.*;
import com.github.burgerguy.hudtweaks.util.Util;
import com.google.gson.JsonElement;

public class HudContainer {
	private static Map<String, HudElement> elementMap = new HashMap<>();
	
	static {
		HudElement hotbar = new HotbarElement();
		addElementIfAbsent(hotbar.getIdentifier(), hotbar);
		
		HudElement expBar = new ExperienceBarElement();
		addElementIfAbsent(expBar.getIdentifier(), expBar);
		
//		HudElement armor = new HudElement("armor", 182, 22, (w, h) -> new Point((w / 2) - 91, h - 22));
//		addElement(armor.getIdentifier(), armor);
		
		HudElement health = new HealthElement();
		addElement(health.getIdentifier(), health);
		
		HudElement hunger = new HungerElement();
		addElement(hunger.getIdentifier(), hunger);
		
//		HudElement air = new HudElement("air", 182, 22, (w, h) -> new Point((w / 2) - 91, h - 22));
//		addElement(air.getIdentifier(), air);
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
	
	public static void addElementIfAbsent(String identifier, HudElement element) {
		elementMap.putIfAbsent(identifier, element);
	}
	
	/**
	 * Only use for saving config.
	 */
	public static Map<String, HudElement> getElementMap() {
		return elementMap;
	}
	
	public static void updateFromJson(JsonElement json) {
		for (Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
			try {
				getElement(entry.getKey()).updateFromJson(entry.getValue());
			} catch (NullPointerException e) {
				Util.LOGGER.error("Element specified in config doesn't exist in element map, skipping...", e);
				continue;
			}
		}
	}
	
}
