package com.github.burgerguy.hudtweaks.gui;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.github.burgerguy.hudtweaks.gui.element.*;
import com.github.burgerguy.hudtweaks.util.Util;
import com.github.burgerguy.hudtweaks.util.gui.MatrixCache;
import com.github.burgerguy.hudtweaks.util.gui.RelativeParentCache;
import com.google.gson.JsonElement;

public enum HudContainer {
	;
	
	private static final Map<String, HudElement> elementMap = new HashMap<>();
	private static transient final MatrixCache matrixCache = new MatrixCache();
	private static transient final RelativeParentCache relativeParentCache = new RelativeParentCache();
	
	static {
		HudElement hotbar = new HotbarElement();
		addElement(hotbar.getIdentifier(), hotbar);
		
		HudElement expBar = new ExperienceBarElement();
		addElement(expBar.getIdentifier(), expBar);
		
		HudElement armor = new ArmorElement();
		addElement(armor.getIdentifier(), armor);
		
		HudElement health = new HealthElement();
		addElement(health.getIdentifier(), health);
		
		HudElement hunger = new HungerElement();
		addElement(hunger.getIdentifier(), hunger);
		
		HudElement air = new AirElement();
		addElement(air.getIdentifier(), air);
		
		HudElement statusEffects = new StatusEffectsElement();
		addElement(statusEffects.getIdentifier(), statusEffects);
		
		for(HudElement element : getElements()) {
			relativeParentCache.getOrCreate(element, true);
			relativeParentCache.getOrCreate(element, false);
		}
	}

	public static HudElement getElement(String identifier) {
		return elementMap.get(identifier);
	}
	
	public static Collection<HudElement> getElements() {
		return elementMap.values();
	}
	
	public static void addElement(String identifier, HudElement element) {
		if (!identifier.equals("screen")) {
			elementMap.put(identifier, element);
		} else {
			Util.LOGGER.error("Failed to add element: identifier \"screen\" is reserved");
		}
	}
	
	public static void addElementIfAbsent(String identifier, HudElement element) {
		if (!identifier.equals("screen")) {
			elementMap.putIfAbsent(identifier, element);
		} else {
			Util.LOGGER.error("Failed to add element: identifier \"screen\" is reserved");
		}
	}
	
	/**
	 * Only use for saving config.
	 */
	public static Map<String, HudElement> getElementMap() {
		return elementMap;
	}
	
	public static MatrixCache getMatrixCache() {
		return matrixCache;
	}
	
	public static RelativeParentCache getRelativeParentCache() {
		return relativeParentCache;
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
