package com.github.burgerguy.hudtweaks.hud;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import com.github.burgerguy.hudtweaks.gui.HTOptionsScreen;
import com.github.burgerguy.hudtweaks.hud.element.HudElementContainer;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;


public class MatrixCache {
	private final Map<HTIdentifier, Matrix4f> matrixMap = new HashMap<>();
	private final Set<HTIdentifier> appliedElements = new HashSet<>();

	public Matrix4f getMatrix(HTIdentifier containerIdentifier) {
		return matrixMap.get(containerIdentifier);
	}

	public void putMatrix(HTIdentifier containerIdentifier, Matrix4f matrix) {
		matrixMap.put(containerIdentifier, matrix);
	}

	public void tryPushMatrix(HTIdentifier identifier, @Nullable MatrixStack matrixStack) {
		tryPushMatrix(identifier, identifier, matrixStack);
	}

	public void tryPopMatrix(HTIdentifier identifier, @Nullable MatrixStack matrixStack) {
		tryPopMatrix(identifier, identifier, matrixStack);
	}

	/**
	 * The elementIdentifier is needed to validate the active element
	 */
	public void tryPushMatrix(HTIdentifier containerIdentifier, HTIdentifier elementIdentifier, @Nullable MatrixStack matrixStack) {
		Matrix4f matrix = getMatrix(containerIdentifier);
		if (matrix != null) {
			HudElementContainer elementContainer = HudContainer.getElementRegistry().getElementContainer(containerIdentifier);
			// only push if the entry is active
			// this ignores pushes in InGameHudMixin when an override is active because the override will do it itself
			if (elementContainer.getActiveElement().getIdentifier().equals(elementIdentifier)) {
				appliedElements.add(containerIdentifier);
				if (matrixStack != null) {
					matrixStack.push();
					matrixStack.peek().getModel().multiply(matrix);
				} else {
					RenderSystem.pushMatrix();
					RenderSystem.multMatrix(matrix);
				}

				if (HTOptionsScreen.isOpen()) elementContainer.startDrawTest(); // we only care about visibility when HudElementWidgets have to be displayed
			}
		}
	}

	/**
	 * The elementIdentifier is needed to validate the active element
	 */
	public void tryPopMatrix(HTIdentifier containerIdentifier, HTIdentifier elementIdentifier, @Nullable MatrixStack matrixStack) {
		if (appliedElements.contains(containerIdentifier)) {
			HudElementContainer elementContainer = HudContainer.getElementRegistry().getElementContainer(containerIdentifier);
			// only pop if the entry is active
			// this ignores pops in InGameHudMixin when an override is active because the override will do it itself
			if (elementContainer.getActiveElement().getIdentifier().equals(elementIdentifier)) {
				if (matrixStack != null) {
					matrixStack.pop();
				} else {
					RenderSystem.popMatrix();
				}
				appliedElements.remove(containerIdentifier);

				elementContainer.endDrawTest();
			}
		}
	}
}
