package com.github.burgerguy.hudtweaks.hud;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

public class MatrixCache {
	private final Map<String, Matrix4f> matrixMap = new HashMap<>();
	private final Map<String, Boolean> appliedMatrixMap = new HashMap<>();
	
	public Matrix4f getMatrix(String identifier) {
		return matrixMap.get(identifier);
	}
	
	public void putMatrix(String identifier, Matrix4f matrix) {
		matrixMap.put(identifier, matrix);
	}
	
	public void tryPushMatrix(String identifier, @Nullable MatrixStack matrixStack) {
		Matrix4f matrix = HudContainer.getMatrixCache().getMatrix(identifier);
		if (matrix != null) {
			appliedMatrixMap.put(identifier, true);
			if (matrixStack != null) {
				matrixStack.push();
				matrixStack.peek().getModel().multiply(matrix);
			} else {
				RenderSystem.pushMatrix();
				RenderSystem.multMatrix(matrix);
			}
		}
	}
	
	public void tryPopMatrix(String identifier, @Nullable MatrixStack matrixStack) {
		if (appliedMatrixMap.get(identifier)) {
			if (matrixStack != null) {
				matrixStack.pop();
			} else {
				RenderSystem.popMatrix();
			}
			appliedMatrixMap.put(identifier, false);
		}
	}
}
