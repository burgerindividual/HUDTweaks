package com.github.burgerguy.hudtweaks.util;

import net.minecraft.util.math.Matrix4f;

public class HudTransforms {
	public static TransformHelper hotbarTransform = new TransformHelper();
	public static TransformHelper armorTransform = new TransformHelper();
	public static TransformHelper healthTransform = new TransformHelper();
	public static TransformHelper foodTransform = new TransformHelper();
	public static TransformHelper mountTransform = new TransformHelper();
	public static TransformHelper airTransform = new TransformHelper();
	public static TransformHelper expBarTransform = new TransformHelper();
	public static TransformHelper jumpBarTransform = new TransformHelper();
	
	static {
		hotbarTransform.set(Matrix4f.translate(0, -200, 0));
		armorTransform.set(Matrix4f.translate(0, -300, 0));
		healthTransform.set(Matrix4f.translate(0, -170, 0));
		foodTransform.set(Matrix4f.translate(0, -190, 0));
		mountTransform.set(Matrix4f.translate(0, -190, 0));
		airTransform.set(Matrix4f.translate(0, -210, 0));
		expBarTransform.set(Matrix4f.translate(0, -250, 0));
		jumpBarTransform.set(Matrix4f.translate(0, -250, 0));
	}
}
