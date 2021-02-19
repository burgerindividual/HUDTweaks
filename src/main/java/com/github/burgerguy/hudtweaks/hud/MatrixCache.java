package com.github.burgerguy.hudtweaks.hud;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.github.burgerguy.hudtweaks.gui.HTOptionsScreen;
import com.github.burgerguy.hudtweaks.hud.element.HTIdentifier;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

public class MatrixCache {
	private final Map<HTIdentifier.ElementType, Matrix4f> matrixMap = new HashMap<>();
	private final Map<HTIdentifier.ElementType, Boolean> appliedMatrixMap = new HashMap<>();
	
	public Matrix4f getMatrix(HTIdentifier.ElementType elementType) {
		return matrixMap.get(elementType);
	}
	
	public void putMatrix(HTIdentifier.ElementType elementType, Matrix4f matrix) {
		matrixMap.put(elementType, matrix);
	}
	
	public void tryPushMatrix(HTIdentifier.ElementType elementType, @Nullable MatrixStack matrixStack) {
		Matrix4f matrix = getMatrix(elementType);
		if (matrix != null) {
			appliedMatrixMap.put(elementType, true);
			if (matrixStack != null) {
				matrixStack.push();
				matrixStack.peek().getModel().multiply(matrix);
			} else {
				RenderSystem.pushMatrix();
				RenderSystem.multMatrix(matrix);
			}
			
			if (HTOptionsScreen.isOpen()) HudContainer.getElementRegistry().getActiveEntry(elementType).startDrawTest(); // we only care about visibility when HudElementWidgets have to be displayed
		}
	}
	
	public void tryPopMatrix(HTIdentifier.ElementType elementType, @Nullable MatrixStack matrixStack) { // TODO: fix draw tests when element changed, also update matrix on entry swap
		if (appliedMatrixMap.get(elementType)) {
			if (matrixStack != null) {
				matrixStack.pop();
			} else {
				RenderSystem.popMatrix();
			}
			appliedMatrixMap.put(elementType, false);
			
			HudContainer.getElementRegistry().getActiveEntry(elementType).endDrawTest(); // this won't error out if the draw test isn't active, so it's ok
		}
	}
}
