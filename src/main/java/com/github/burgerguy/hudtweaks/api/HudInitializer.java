package com.github.burgerguy.hudtweaks.api;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.github.burgerguy.hudtweaks.util.gui.UpdateEvent;

public interface HudInitializer {
	
	/**
	 * Used for manipulating already existing things in the HudContainer.
	 */
	default public void onInitialize() {
		// noop by default
	}
	
	/**
	 * Implement if custom events are needed for updating the custom
	 * elements.
	 */
	default public Set<UpdateEvent> getCustomEvents() {
		return Collections.emptySet();
	}
	
	/**
	 * Each of the CustomHudElements must have a RenderStatusUpdater so it
	 * can update the matricies before and after the render of the
	 * element.
	 */
	public Collection<CustomHudElement> getCustomElements();
}
