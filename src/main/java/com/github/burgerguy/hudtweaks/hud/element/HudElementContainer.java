package com.github.burgerguy.hudtweaks.hud.element;

import com.github.burgerguy.hudtweaks.api.HudElementOverride;
import com.github.burgerguy.hudtweaks.hud.tree.AbstractContainerNode;
import com.github.burgerguy.hudtweaks.util.Util;
import com.github.burgerguy.hudtweaks.util.gl.DrawTest;
import com.google.gson.JsonElement;
import com.mojang.blaze3d.systems.RenderSystem;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

public class HudElementContainer extends AbstractContainerNode {
	private final HudElement initialElement;
	private final List<HudElementOverride> overrides = new ArrayList<>(2);

	protected HudElementWidget widget;
	protected DrawTest drawTest;
	protected Boolean drawTestResult;
	protected boolean drawTestedSinceClear;

	public HudElementContainer(HudElement initialElement) {
		this.initialElement = initialElement;
		// we have to create the draw test here because
		// it has to be on the render thread and it has
		// to be after lwjgl has initialized
		RenderSystem.recordRenderCall(() -> drawTest = new DrawTest());
	}

	public void addOverride(HudElementOverride override) {
		overrides.add(override);
	}

	@SuppressWarnings("unchecked")
	@Override
	public HudElement getInitialElement() {
		return initialElement;
	}

	/**
	 * Gets the first enabled override in the list, otherwise the initial element.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public HudElement getActiveElement() {
		if (!overrides.isEmpty()) {
			for (HudElementOverride override : overrides) {
				if (override.isEnabled()) {
					return override.getElement();
				}
			}
		}
		return initialElement;
	}

	/**
	 * Should only be used for profile saving and loading.
	 */
	public List<HudElementOverride> getOverrides() {
		return Collections.unmodifiableList(overrides);
	}

	public void updateFromJson(JsonElement json) {
		initialElement.updateFromJson(json);
		JsonElement overridesElement = json.getAsJsonObject().get("overrides");
		if (overridesElement != null) {
			for (Entry<String, JsonElement> jsonOverride : overridesElement.getAsJsonObject().entrySet()) {
				boolean foundOverride = false;
				for (HudElementOverride override : overrides) {
					HudElement element = override.getElement();
					if (element.getIdentifier().toString().equals(jsonOverride.getKey())) {
						element.updateFromJson(jsonOverride.getValue());
						foundOverride = true;
						break;
					}
				}

				if (!foundOverride)
					Util.LOGGER.error("Override specified in config doesn't exist in override map, skipping...");
			}
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
