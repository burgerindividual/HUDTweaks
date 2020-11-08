package com.github.burgerguy.hudtweaks.util;

import net.minecraft.util.math.Matrix4f;

public class TransformHelper {
	private Matrix4f transform = null;
	private Matrix4f inverse = null;
	
	public void set(Matrix4f transform) {
		this.transform = transform;
		this.inverse = transform.copy();
		this.inverse.invert();
	}
	
	public void reset() {
		this.transform = null;
		this.inverse = null;
	}
	
	public Matrix4f get() {
		return transform;
	}
	
	public Matrix4f getInverse() {
		return inverse;
	}
	
	public boolean isSet() {
		return transform != null && inverse != null;
	}
}
