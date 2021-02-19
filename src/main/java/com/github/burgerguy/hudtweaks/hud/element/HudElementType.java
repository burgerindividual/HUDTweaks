package com.github.burgerguy.hudtweaks.hud.element;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;

import com.github.burgerguy.hudtweaks.util.Util;
import com.google.gson.JsonElement;

import net.minecraft.util.math.MathHelper;

public class HudElementType { // TODO: somehow fit this for RelativeTreeNode
	private transient final HTIdentifier.ElementType elementIdentifier;
	private final List<HudElementEntry> entries = new ArrayList<>();
	private transient int activeIndex;
	
	public HudElementType(HTIdentifier.ElementType elementIdentifier, HudElementEntry firstElement) {
		this.elementIdentifier = elementIdentifier;
		entries.add(firstElement);
	}
	
	public void add(HudElementEntry element) {
		if (element.getIdentifier().getElementType().equals(elementIdentifier)) {
			entries.add(element);
		} else {
			Util.LOGGER.error("HudElementType with element identifier " + element.getIdentifier().toString() + " does not match element identifier " + elementIdentifier.toString());
		}
	}
	
	public HudElementEntry getActiveElement() {
		return entries.get(activeIndex);
	}
	
	public int getElementCount() {
		return entries.size();
	}
	
	public void cycleTypeForward() {
		activeIndex = MathHelper.clamp(activeIndex + 1, 0, entries.size() - 1);
	}
	
	public void cycleTypeBackward() {
		activeIndex = MathHelper.clamp(activeIndex - 1, 0, entries.size() - 1);
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
				Util.LOGGER.error("HudElementType specified in config doesn't exist in element map, skipping...", e);
				continue;
			}
		}
	}
}
