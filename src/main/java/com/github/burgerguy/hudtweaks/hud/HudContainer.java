package com.github.burgerguy.hudtweaks.hud;

import com.github.burgerguy.hudtweaks.hud.tree.RelativeTreeRootScreen;

public enum HudContainer {
	; // no instantiation, all contents static
	
	private static final ElementRegistry ELEMENT_REGISTRY = new ElementRegistry();
	private static final MatrixCache MATRIX_CACHE = new MatrixCache();
	private static final UpdateEventRegistry EVENT_REGISTRY = new UpdateEventRegistry();
	private static final RelativeTreeRootScreen SCREEN_ROOT = new RelativeTreeRootScreen();
	
	public static void init() {
		ELEMENT_REGISTRY.init();
		EVENT_REGISTRY.init();
	}
	
	public static ElementRegistry getElementRegistry() {
		return ELEMENT_REGISTRY;
	}
	
	public static MatrixCache getMatrixCache() {
		return MATRIX_CACHE;
	}
	
	public static RelativeTreeRootScreen getScreenRoot() {
		return SCREEN_ROOT;
	}
	
	public static UpdateEventRegistry getEventRegistry() {
		return EVENT_REGISTRY;
	}	
}
