package com.github.burgerguy.hudtweaks.hud.element;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;

import com.github.burgerguy.hudtweaks.util.Util;
import com.google.gson.JsonElement;

import net.minecraft.util.math.MathHelper;

public class HudElementGroup {
	private transient final HTIdentifier.Element elementIdentifier;
	private final List<HudElement> elements = new ArrayList<>();
	private transient int activeIndex;
	
	public HudElementGroup(HTIdentifier.Element elementIdentifier, HudElement firstElement) {
		this.elementIdentifier = elementIdentifier;
		elements.add(firstElement);
	}
	
	public void add(HudElement element) {
		if (element.getIdentifier().getElement().equals(elementIdentifier)) {
			elements.add(element);
		} else {
			Util.LOGGER.error("Element with element identifier " + element.getIdentifier().toString() + " does not match element identifier " + elementIdentifier.toString());
		}
	}
	
	public HudElement getActiveElement() {
		return elements.get(activeIndex);
	}
	
	public int getElementCount() {
		return elements.size();
	}
	
	public void cycleTypeForward() {
		activeIndex = MathHelper.clamp(activeIndex + 1, 0, elements.size() - 1);
	}
	
	public void cycleTypeBackward() {
		activeIndex = MathHelper.clamp(activeIndex - 1, 0, elements.size() - 1);
	}
	
	public String toString() {
		return elementIdentifier.toString();
	}
	
	public String toTranslatedString() {
		return elementIdentifier.toTranslatedString();
	}
	
	public void updateFromJson(JsonElement json) { // TODO: do this with the element map in ElementRegistry
		for (Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
			try {
				getActiveElement(entry.getKey()).updateFromJson(entry.getValue());
			} catch (NullPointerException e) {
				Util.LOGGER.error("Element specified in config doesn't exist in element map, skipping...", e);
				continue;
			}
		}
	}
}
