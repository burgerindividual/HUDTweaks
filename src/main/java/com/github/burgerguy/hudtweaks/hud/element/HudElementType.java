package com.github.burgerguy.hudtweaks.hud.element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.jetbrains.annotations.Nullable;

import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import com.github.burgerguy.hudtweaks.hud.tree.AbstractTypeNode;
import com.github.burgerguy.hudtweaks.util.Util;
import com.github.burgerguy.hudtweaks.util.gl.DrawTest;
import com.google.gson.JsonElement;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.util.math.MathHelper;

public class HudElementType extends AbstractTypeNode { // TODO: somehow fit this for RelativeTreeNode
	private transient final HTIdentifier.ElementType elementIdentifier;
	private final List<HudElementEntry> entries = new ArrayList<>();
	private transient int activeIndex;
	
	protected transient HudElementWidget widget;
	protected transient DrawTest drawTest;
	protected transient Boolean drawTestResult;
	protected transient boolean drawTestedSinceClear;
	
	public HudElementType(HTIdentifier.ElementType elementIdentifier) {
		super(elementIdentifier);
		this.elementIdentifier = elementIdentifier;
		// we have to create the draw test here because
		// it has to be on the render thread and it has
		// to be after lwjgl has initialized
		RenderSystem.recordRenderCall(() -> drawTest = new DrawTest());
	}
	
	public void add(HudElementEntry element) {
		if (element.getIdentifier().getElementType().equals(elementIdentifier)) {
			entries.add(element);
		} else {
			Util.LOGGER.error("HudElementType with element identifier " + element.getIdentifier().toString() + " does not match element identifier " + elementIdentifier.toString());
		}
	}
	
	@SuppressWarnings("unchecked")
	public HudElementEntry getActiveEntry() {
		return entries.get(activeIndex);
	}
	
	/**
	 * Should only be used for profile saving and loading.
	 */
	public List<HudElementEntry> getRawEntryList() {
		return entries;
	}
	
	public int getElementCount() {
		return entries.size();
	}
	
	public void cycleType(int amount) {
		activeIndex = MathHelper.clamp(activeIndex + amount, 0, entries.size() - 1);
	}
	
	public String toString() {
		return elementIdentifier.toString();
	}
	
	public String toTranslatedString() {
		return elementIdentifier.toTranslatedString();
	}
	
	public void updateFromJson(JsonElement json) {
		for (Entry<String, JsonElement> jsonEntry : json.getAsJsonObject().entrySet()) {
			JsonElement value = jsonEntry.getValue();
			if (jsonEntry.getKey().equals("activeEntry") && value.isJsonPrimitive()) {
				if (value.getAsJsonPrimitive().isString()) {
					String[] identifiers = value.getAsString().split(":");
					int index = 0;
					for (int i = 0; i < entries.size(); i++) {
						HTIdentifier id = entries.get(i).getIdentifier();
						if (id.getNamespace().toString().equals(identifiers[0]) && id.getEntryName().toString().equals(identifiers[1])) {
							index = i;
							break;
						}
					}
					activeIndex = index;
					continue;
				}
			}
			
			String[] identifiers = jsonEntry.getKey().split(":");
			boolean foundEntry = false;
			for (HudElementEntry entry : entries) {
				HTIdentifier id = entry.getIdentifier();
				if (id.getNamespace().toString().equals(identifiers[0]) && id.getEntryName().toString().equals(identifiers[1])) {
					entry.updateFromJson(jsonEntry.getValue());
					foundEntry = true;
					break;
				}
			}
			
			if (!foundEntry) Util.LOGGER.error("Entry specified in config doesn't exist in entry map, skipping...");
		}
	}
	
	public void startDrawTest() {
		drawTest.start();
	}
	
	public void endDrawTest() {
		if (drawTest.end()) drawTestedSinceClear = true;
	}
	
	public void clearDrawTest() {
		drawTestResult = null;
		drawTestedSinceClear = false;
	}
	
	public boolean isRendered() {
		if (!drawTestedSinceClear) return false;
		if (drawTestResult == null) drawTestResult = drawTest.getResultSync();
		return drawTestResult;
	}
	
	@Nullable
	public HudElementWidget getWidget() {
		return widget;
	}
	
	public HudElementWidget createWidget(@Nullable Runnable valueUpdater) {
		return widget = new HudElementWidget(this, valueUpdater);
	}
}
