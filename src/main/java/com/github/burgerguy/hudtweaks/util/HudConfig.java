package com.github.burgerguy.hudtweaks.util;

public enum HudConfig {
	;
	
	public static TransformHelper hotbarTransform = new TransformHelper();
	public static TransformHelper armorTransform = new TransformHelper();
	public static TransformHelper healthTransform = new TransformHelper();
	public static TransformHelper foodTransform = new TransformHelper();
	public static TransformHelper mountTransform = new TransformHelper();
	public static TransformHelper airTransform = new TransformHelper();
	public static TransformHelper expBarTransform = new TransformHelper();
	public static TransformHelper jumpBarTransform = new TransformHelper();
	public static TransformHelper statusEffectTransform = new TransformHelper();
	public static boolean statusEffectVertical = false;
	
	static {
		//statusEffectVertical = true;
	}
}
