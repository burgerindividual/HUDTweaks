package com.github.burgerguy.hudtweaks.util.gui;

import java.util.LinkedHashMap;
import java.util.Map;

import com.github.burgerguy.hudtweaks.gui.HudContainer;
import com.github.burgerguy.hudtweaks.gui.HudElement;
import com.github.burgerguy.hudtweaks.gui.RelativeHudElementParent;
import com.github.burgerguy.hudtweaks.gui.RelativeParent;
import com.github.burgerguy.hudtweaks.util.Util;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import net.minecraft.client.MinecraftClient;

public class RelativeParentCache {
	public static final String SCREEN_IDENTIFIER = "screen";
	private final Table<String, Boolean, RelativeParent> relativeParentTable = HashBasedTable.create();
	
	public RelativeParentCache() {
		add(SCREEN_IDENTIFIER, true, new RelativeParent() {
			@Override public String getIdentifier() { return SCREEN_IDENTIFIER; }
			@Override public int getPosition(MinecraftClient client) { return 0; }
			@Override public int getDimension(MinecraftClient client) { return client.getWindow().getScaledWidth(); }
		});
		
		add(SCREEN_IDENTIFIER, false, new RelativeParent() {
			@Override public String getIdentifier() { return SCREEN_IDENTIFIER; }
			@Override public int getPosition(MinecraftClient client) { return 0; }
			@Override public int getDimension(MinecraftClient client) { return client.getWindow().getScaledHeight(); }
		});
	}
	
	/**
	 * Gets a RelativeParent from the table, or if it doesn't exist, tries
	 * to create one from a HudElement of the same identifier.
	 */
	public RelativeParent getOrCreate(String identifier, boolean isX) {
		RelativeParent currentValue = relativeParentTable.get(identifier, isX);
		if (currentValue != null) {
			return currentValue;
		} else {
			HudElement element = HudContainer.getElement(identifier);
			if (element != null) {
				RelativeParent newValue = new RelativeHudElementParent(element, isX);
				add(identifier, isX, newValue);
				return newValue;
			} else {
				Util.LOGGER.error("No RelativeParent or HudElement found for identifier " + identifier);
				return null;
			}
		}
	}
	
	/**
	 * Gets a RelativeParent from the table using the HudElement's
	 * identifier, or if it doesn't exist, tries to create one from the
	 * HudElement provided.
	 */
	public RelativeParent getOrCreate(HudElement element, boolean isX) {
		RelativeParent currentValue = relativeParentTable.get(element.getIdentifier(), isX);
		if (currentValue != null) {
			return currentValue;
		} else {
			RelativeParent newValue = new RelativeHudElementParent(element, isX);
			add(element, isX, newValue);
			return newValue;
		}
	}
	
	public void add(String identifier, boolean isX, RelativeParent relativeParent) {
		relativeParentTable.put(identifier, isX, relativeParent);
	}
	
	public void add(HudElement element, boolean isX, RelativeParent relativeParent) {
		relativeParentTable.put(element.getIdentifier(), isX, relativeParent);
	}
	
	/**
	 * @return A copy of the column map.
	 */
	public Map<String, RelativeParent> getColumn(boolean isX) {
		return new LinkedHashMap<>(relativeParentTable.column(isX));
	}
}
