package com.github.burgerguy.hudtweaks.hud;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.github.burgerguy.hudtweaks.hud.element.AirElement;
import com.github.burgerguy.hudtweaks.hud.element.ArmorElement;
import com.github.burgerguy.hudtweaks.hud.element.ExperienceBarElement;
import com.github.burgerguy.hudtweaks.hud.element.HTIdentifier;
import com.github.burgerguy.hudtweaks.hud.element.HealthElement;
import com.github.burgerguy.hudtweaks.hud.element.HotbarElement;
import com.github.burgerguy.hudtweaks.hud.element.HudElement;
import com.github.burgerguy.hudtweaks.hud.element.HudElementGroup;
import com.github.burgerguy.hudtweaks.hud.element.HungerElement;
import com.github.burgerguy.hudtweaks.hud.element.JumpBarElement;
import com.github.burgerguy.hudtweaks.hud.element.MountHealthElement;
import com.github.burgerguy.hudtweaks.hud.element.RelativeTreeRootScreen;
import com.github.burgerguy.hudtweaks.hud.element.StatusEffectsElement;
import com.github.burgerguy.hudtweaks.util.Util;
import com.google.gson.JsonElement;

/**
 * We maintain two maps in this class. The first one is a map of raw
 * identifiers to elements, and is used with matrix application for
 * speed. The second is a map from element types to groups of that
 * type of element. It is used for basically anything else and manages
 * element replacements by mods that implement the API.
 */
public class ElementRegistry {
	private final Map<HTIdentifier, HudElement> elementMap = new HashMap<>();
	private final Map<HTIdentifier.Element, HudElementGroup> elementGroupMap = new HashMap<>();
	
	public void init() {
		addElement(new HotbarElement());
		addElement(new ExperienceBarElement());
		addElement(new JumpBarElement());
		addElement(new ArmorElement());
		addElement(new HealthElement());
		addElement(new HungerElement());
		addElement(new MountHealthElement());
		addElement(new AirElement());
		addElement(new StatusEffectsElement());
	}
	
	public HudElementGroup getElementGroup(HTIdentifier.Element identifier) {
		return elementGroupMap.get(identifier);
	}

	public HudElement getActiveElement(HTIdentifier.Element identifier) {
		return getElementGroup(identifier).getActiveElement();
	}
	
	public HudElement getElement(HTIdentifier identifier) {
		return elementMap.get(identifier);
	}
	
	public Collection<HudElementGroup> getElementGroups() {
		return elementGroupMap.values();
	}
	
	public Collection<HudElement> getElements() {
		return elementMap.values();
	}
	
	public void addElement(HudElement element) {
		if (!element.getIdentifier().getElement().equals(RelativeTreeRootScreen.IDENTIFIER.getElement())) {
			if (elementGroupMap.containsKey(element.getIdentifier().getElement())) {
				getElementGroup(element.getIdentifier().getElement()).add(element);
			} else {
				elementGroupMap.put(element.getIdentifier().getElement(), new HudElementGroup(element.getIdentifier().getElement(), element));
			}
			elementMap.put(element.getIdentifier(), element);
		} else {
			Util.LOGGER.error("Failed to add element: identifier \"screen\" is reserved");
		}
	}
	
	public void updateFromJson(JsonElement json) { //TODO fixme
		for (Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
			try {
				// temporary element identifier to retrieve the group from a string representation
				getElementGroup(new HTIdentifier.Element(entry.getKey(), null)).updateFromJson(entry.getValue());
			} catch (NullPointerException e) {
				Util.LOGGER.error("Element specified in config doesn't exist in element map, skipping...", e);
				continue;
			}
		}
	}
}
