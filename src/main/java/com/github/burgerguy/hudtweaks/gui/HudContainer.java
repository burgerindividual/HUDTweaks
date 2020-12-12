package com.github.burgerguy.hudtweaks.gui;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.github.burgerguy.hudtweaks.gui.element.*;
import com.github.burgerguy.hudtweaks.util.Util;
import com.github.burgerguy.hudtweaks.util.gui.MatrixCache;
import com.google.gson.JsonElement;

import net.minecraft.client.MinecraftClient;

public enum HudContainer {
	;
	
	private static final Map<String, HudElement> elementMap = new HashMap<>();
	private static transient final MatrixCache matrixCache = new MatrixCache();
	private static transient final RelativeParentCache relativeParentCache = new RelativeParentCache();
	
	public static final RelativeParent SCREEN_RELATIVE_PARENT_X;
	public static final RelativeParent SCREEN_RELATIVE_PARENT_Y;
	
	static {
		SCREEN_RELATIVE_PARENT_X = new RelativeParent() {
			@Override public String getIdentifier() { return "screen"; }
			@Override public int getPosition(MinecraftClient client) { return 0; }
			@Override public int getDimension(MinecraftClient client) { return client.getWindow().getScaledWidth(); }
		};
		
		SCREEN_RELATIVE_PARENT_Y = new RelativeParent() {
			@Override public String getIdentifier() { return "screen"; }
			@Override public int getPosition(MinecraftClient client) { return 0; }
			@Override public int getDimension(MinecraftClient client) { return client.getWindow().getScaledHeight(); }
		};
		
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
