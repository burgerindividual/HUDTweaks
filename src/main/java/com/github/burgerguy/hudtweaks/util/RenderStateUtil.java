package com.github.burgerguy.hudtweaks.util;

import com.github.burgerguy.hudtweaks.gui.HTOptionsScreen;
import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import com.github.burgerguy.hudtweaks.hud.HudContainer;
import com.github.burgerguy.hudtweaks.hud.element.HudElement;
import com.github.burgerguy.hudtweaks.hud.element.HudElementContainer;
import net.minecraft.client.util.math.MatrixStack;

public final class RenderStateUtil {
    private RenderStateUtil() {
        // no instantiation, all contents static
    }

    // utility method for default elements
    public static void tryStartRender(HTIdentifier identifier, MatrixStack... matrixStacks) {
        tryStartRender(identifier, identifier, matrixStacks);
    }

    public static void tryStartRender(HTIdentifier containerIdentifier, HTIdentifier elementIdentifier, MatrixStack... matrixStacks) {
        HudElementContainer hudElementContainer = HudContainer.getElementRegistry().getElementContainer(containerIdentifier);
        // only continue if element is active
        if (elementIdentifier.equals(hudElementContainer.getActiveElement().getIdentifier())) {
            startRender(hudElementContainer, matrixStacks);
        }
    }

    public static void tryStartRender(HudElement hudElement, MatrixStack... matrixStacks) {
        HudElementContainer hudElementContainer = hudElement.getContainerNode();
        // only continue if element is active
        if (hudElement.equals(hudElementContainer.getActiveElement())) {
            startRender(hudElementContainer, matrixStacks);
        }
    }

    // utility method for default elements
    public static void tryFinishRender(HTIdentifier identifier, MatrixStack... matrixStacks) {
        tryFinishRender(identifier, identifier, matrixStacks);
    }

    public static void tryFinishRender(HTIdentifier containerIdentifier, HTIdentifier elementIdentifier, MatrixStack... matrixStacks) {
        HudElementContainer hudElementContainer = HudContainer.getElementRegistry().getElementContainer(containerIdentifier);
        // only continue if element is active
        if (elementIdentifier.equals(hudElementContainer.getActiveElement().getIdentifier())) {
            finishRender(hudElementContainer, matrixStacks);
        }
    }

    public static void tryFinishRender(HudElement hudElement, MatrixStack... matrixStacks) {
        HudElementContainer hudElementContainer = hudElement.getContainerNode();
        // only continue if element is active
        if (hudElement.equals(hudElementContainer.getActiveElement())) {
            finishRender(hudElementContainer, matrixStacks);
        }
    }

    //// Direct container methods, uses container's active element

    private static void startRender(HudElementContainer hudElementContainer, MatrixStack... matrixStacks) {
        hudElementContainer.tryPushMatrix(matrixStacks);
        if (HTOptionsScreen.isOpen()) hudElementContainer.markDrawTestStart();
    }

    private static void finishRender(HudElementContainer hudElementContainer, MatrixStack... matrixStacks) {
        hudElementContainer.tryPopMatrix(matrixStacks);
        if (HTOptionsScreen.isOpen()) hudElementContainer.markDrawTestEnd();
    }
}
