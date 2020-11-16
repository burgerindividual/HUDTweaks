package com.github.burgerguy.hudtweaks.util.gui;

import java.util.HashMap;
import java.util.Map;

import com.github.burgerguy.hudtweaks.gui.HudContainer;
import com.github.burgerguy.hudtweaks.gui.HudElement;

import net.minecraft.util.math.Matrix4f;

public enum MatrixCache {
	;
	
	private static Map<String, Matrix4f> matrixMap = new HashMap<>();
	
	public static void calculateMatrix(HudElement element, int screenWidth, int screenHeight) {
		matrixMap.put(element.getIdentifier(), element.calculateMatrix(screenWidth, screenHeight));
	}
	
	public static void calculateMatrix(String identifier, int screenWidth, int screenHeight) {
		calculateMatrix(HudContainer.getElement(identifier), screenWidth, screenHeight);
	}
	
	public static void calculateAllMatricies(int screenWidth, int screenHeight) {
		for(HudElement element : HudContainer.getElements()) {
			calculateMatrix(element, screenWidth, screenHeight);
		}
	}
	
	public static Matrix4f getMatrix(String identifier) {
		return matrixMap.get(identifier);
	}
}
