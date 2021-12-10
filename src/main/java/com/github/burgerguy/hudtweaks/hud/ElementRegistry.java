package com.github.burgerguy.hudtweaks.hud;

import com.github.burgerguy.hudtweaks.api.HudElementOverride;
import com.github.burgerguy.hudtweaks.hud.element.*;
import com.github.burgerguy.hudtweaks.hud.tree.RelativeTreeRootScreen;
import com.github.burgerguy.hudtweaks.util.Util;
import com.google.gson.JsonElement;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ElementRegistry {
	private final Map<HTIdentifier, HudElementContainer> elementContainerMap = new HashMap<>();

	public void init() {
		addElement(new DefaultHotbarElement());
		addElement(new DefaultExperienceBarElement());
		addElement(new DefaultJumpBarElement());
		addElement(new DefaultArmorElement());
		addElement(new DefaultHealthElement());
		addElement(new DefaultHungerElement());
		addElement(new DefaultMountHealthElement());
		addElement(new DefaultAirElement());
		addElement(new DefaultStatusEffectsElement());
		addElement(new DefaultTooltipElement());
		addElement(new DefaultBossBarElement());
		addElement(new DefaultActionBarElement());
		addElement(new DefaultTitleElement());
		addElement(new DefaultSubtitleElement());
	}

	public HudElementContainer getElementContainer(HTIdentifier identifier) {
		return elementContainerMap.get(identifier);
	}
	
	public HudElement getActiveElement(HTIdentifier identifier) {
		HudElementContainer hudElementContainer = getElementContainer(identifier);
		return hudElementContainer != null ? hudElementContainer.getActiveElement() : null;
	}

	public Collection<HudElementContainer> getElementContainers() {
		return elementContainerMap.values();
	}

	public void addElement(HudElement element) {
		if (elementContainerMap.containsKey(element.getIdentifier())) {
			Util.LOGGER.error("Failed to add element: element with identifier \"" + element + "\" already exists");
			return;
		}

		HudElementContainer elementContainer = new HudElementContainer(element);
		elementContainerMap.put(element.getIdentifier(), elementContainer); // use initial element identifier for overriding
	}

	public void addOverride(HudElementOverride override) {
		if (override.getOverrideTarget().equals(RelativeTreeRootScreen.IDENTIFIER)) {
			Util.LOGGER.error("Failed to add override: overriding \"screen\" not allowed");
			return;
		}

		HudElementContainer elementContainer = elementContainerMap.get(override.getOverrideTarget());
		if (elementContainer != null) {
			elementContainer.addOverride(override);
		} else {
			Util.LOGGER.error("Failed to add override: element with identifier \"" + override.getOverrideTarget() + "\" doesn't exist");
		}
	}

	public void updateFromJson(JsonElement json) {
		for (Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
			try {
				// temporary element identifier to retrieve the container from a string representation
				getElementContainer(HTIdentifier.fromString(entry.getKey())).updateFromJson(entry.getValue());
			} catch (NullPointerException e) {
				Util.LOGGER.error("HudElementType specified in config doesn't exist in element type map, skipping...", e);
			}
		}
	}
}
