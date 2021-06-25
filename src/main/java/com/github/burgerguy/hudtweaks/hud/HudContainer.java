package com.github.burgerguy.hudtweaks.hud;

import com.github.burgerguy.hudtweaks.hud.tree.RelativeTreeRootScreen;

public final class HudContainer {
	private HudContainer() {
		// no instantiation, all contents static
	}

	private static final ElementRegistry ELEMENT_REGISTRY = new ElementRegistry();
	private static final UpdateEventRegistry EVENT_REGISTRY = new UpdateEventRegistry();
	private static final RelativeTreeRootScreen SCREEN_ROOT = new RelativeTreeRootScreen();

	public static void init() {
		EVENT_REGISTRY.init();
		ELEMENT_REGISTRY.init();
		SCREEN_ROOT.init();
	}

	public static ElementRegistry getElementRegistry() {
		return ELEMENT_REGISTRY;
	}

	public static RelativeTreeRootScreen getScreenRoot() {
		return SCREEN_ROOT;
	}

	public static UpdateEventRegistry getEventRegistry() {
		return EVENT_REGISTRY;
	}
}
