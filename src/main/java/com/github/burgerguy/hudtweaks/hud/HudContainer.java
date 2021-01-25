package com.github.burgerguy.hudtweaks.hud;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.github.burgerguy.hudtweaks.hud.element.*;
import com.github.burgerguy.hudtweaks.util.Util;
import com.google.gson.JsonElement;

public enum HudContainer {
	;
	
	private static final Map<String, HudElement> ELEMENT_MAP = new HashMap<>();
	private static transient final MatrixCache MATRIX_CACHE = new MatrixCache();
	private static transient final UpdateEventRegistry EVENT_REGISTRY = new UpdateEventRegistry();
	private static transient final RelativeTreeRootScreen SCREEN_ROOT = new RelativeTreeRootScreen();
	
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
	}

	public static HudElement getElement(String identifier) {
		return ELEMENT_MAP.get(identifier);
	}
	
	public static Collection<HudElement> getElements() {
		return ELEMENT_MAP.values();
	}
	
	public static void addElement(String identifier, HudElement element) {
		if (!identifier.equals("screen")) {
			ELEMENT_MAP.put(identifier, element);
		} else {
			Util.LOGGER.error("Failed to add element: identifier \"screen\" is reserved");
		}
	}
	
	public static void addElementIfAbsent(String identifier, HudElement element) {
		if (!identifier.equals("screen")) {
			ELEMENT_MAP.putIfAbsent(identifier, element);
		} else {
			Util.LOGGER.error("Failed to add element: identifier \"screen\" is reserved");
		}
	}
	
	/**
	 * Only use for saving config.
	 */
	public static Map<String, HudElement> getElementMap() {
		return ELEMENT_MAP;
	}
	
	public static MatrixCache getMatrixCache() {
		return MATRIX_CACHE;
	}
	
	public static RelativeTreeRootScreen getScreenRoot() {
		return SCREEN_ROOT;
	}
	
	public static UpdateEventRegistry getEventRegistry() {
		return EVENT_REGISTRY;
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
