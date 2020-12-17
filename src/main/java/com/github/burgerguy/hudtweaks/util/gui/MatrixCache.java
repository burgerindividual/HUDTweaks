package com.github.burgerguy.hudtweaks.util.gui;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.math.Matrix4f;

public class MatrixCache {
	private final Map<String, Matrix4f> matrixMap = new HashMap<>();
	
	public Matrix4f getMatrix(String identifier) {
		return matrixMap.get(identifier);
	}
	
	public void putMatrix(String identifier, Matrix4f matrix) {
		matrixMap.put(identifier, matrix);
	}
}
