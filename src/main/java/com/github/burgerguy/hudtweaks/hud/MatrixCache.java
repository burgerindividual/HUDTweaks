package com.github.burgerguy.hudtweaks.hud;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import com.github.burgerguy.hudtweaks.gui.HTOptionsScreen;
import com.github.burgerguy.hudtweaks.hud.element.HudElementType;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

public class MatrixCache {
	private final Map<HTIdentifier.ElementType, Matrix4f> matrixMap = new HashMap<>();
	private final Set<HTIdentifier.ElementType> appliedElements = new HashSet<>();
	
	public Matrix4f getMatrix(HTIdentifier.ElementType elementType) {
		return matrixMap.get(elementType);
	}
	
	public void putMatrix(HTIdentifier.ElementType elementType, Matrix4f matrix) {
		matrixMap.put(elementType, matrix);
	}
	
//	public void tryPushMatrix(HTIdentifier.ElementType elementType, @Nullable MatrixStack matrixStack) {
//		Matrix4f matrix = getMatrix(elementType);
//		if (matrix != null) {
//			appliedElements.add(elementType);
//			if (matrixStack != null) {
//				matrixStack.push();
//				matrixStack.peek().getModel().multiply(matrix);
//			} else {
//				RenderSystem.pushMatrix();
//				RenderSystem.multMatrix(matrix);
//			}
//			
//			if (HTOptionsScreen.isOpen()) HudContainer.getElementRegistry().getElementType(elementType).startDrawTest(); // we only care about visibility when HudElementWidgets have to be displayed
//		}
//	}
//	
//	public void tryPopMatrix(HTIdentifier.ElementType elementType, @Nullable MatrixStack matrixStack) { // TODO: fix draw tests when element changed, also update matrix on entry swap
//		if (appliedElements.contains(elementType)) {
//			if (matrixStack != null) {
//				matrixStack.pop();
//			} else {
//				RenderSystem.popMatrix();
//			}
//			appliedElements.remove(elementType);
//			
//			HudContainer.getElementRegistry().getElementType(elementType).endDrawTest(); // this won't error out if the draw test isn't active, so it's ok
//		}
//	}
	
	public void tryPushMatrix(HTIdentifier entryIdentifier, @Nullable MatrixStack matrixStack) {
		HTIdentifier.ElementType elementTypeIdentifier = entryIdentifier.getElementType();
		Matrix4f matrix = getMatrix(elementTypeIdentifier);
		if (matrix != null) {
			HudElementType elementType = HudContainer.getElementRegistry().getElementType(elementTypeIdentifier);
			if (elementType.getActiveEntry().getIdentifier().equals(entryIdentifier)) { // only push if the entry is active
				appliedElements.add(elementTypeIdentifier);
				if (matrixStack != null) {
					matrixStack.push();
					matrixStack.peek().getModel().multiply(matrix);
				} else {
					RenderSystem.pushMatrix();
					RenderSystem.multMatrix(matrix);
				}
				
				if (HTOptionsScreen.isOpen()) elementType.startDrawTest(); // we only care about visibility when HudElementWidgets have to be displayed
			}
		}
	}
	
	public void tryPopMatrix(HTIdentifier entryIdentifier, @Nullable MatrixStack matrixStack) {
		HTIdentifier.ElementType elementTypeIdentifier = entryIdentifier.getElementType();
		if (appliedElements.contains(elementTypeIdentifier)) {
			HudElementType elementType = HudContainer.getElementRegistry().getElementType(elementTypeIdentifier);
			if (elementType.getActiveEntry().getIdentifier().equals(entryIdentifier)) { // only pop if the entry is active
				if (matrixStack != null) {
					matrixStack.pop();
				} else {
					RenderSystem.popMatrix();
				}
				appliedElements.remove(elementTypeIdentifier);
				
				elementType.endDrawTest();
			}
		}
	}
}
