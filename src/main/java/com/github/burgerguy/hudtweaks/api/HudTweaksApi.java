package com.github.burgerguy.hudtweaks.api;

import java.util.Collection;
import java.util.Set;

import com.github.burgerguy.hudtweaks.hud.UpdateEvent;

public interface HudTweaksApi {

	/**
	 * Used for manipulating already existing things in the HudContainer.
	 */
	default void onInitialize() {
		// noop by default
	}

	/**
	 * If the mod's built in set of events doesn't come with events you
	 * need, you can add them here.
	 *
	 * @return a set of custom update events, or null if none are needed.
	 */
	Set<UpdateEvent> getCustomEvents();

	/**
	 * Each of the CustomHudElements must have a MatrixUpdater so it
	 * can update the matrices before and after the render of the
	 * entry.
	 *
	 * @return a set of custom elements, or null if none are added.
	 */
	Collection<CustomHudElement> getCustomElements();

	/**
	 * Used to override existing elements, and can be set to enabled
	 * or disabled.
	 *
	 * Note: This is only to let hudtweaks know what to change about
	 * the bounds, position, etc of the element. This doesn't actually
	 * change what is rendered onto the screen, and that should be done
	 * through a mixin. Overrides are applied by going through the list
	 * and applying the first enabled override. This means that another
	 * mod might unknowingly take priority, but you probably have bigger
	 * issues if two mods try to modify the same thing through mixins
	 * anyway.
	 *
	 * @return a set of element overrides, or null if none are added.
	 */
	Collection<HudElementOverride> getOverrides();
}
