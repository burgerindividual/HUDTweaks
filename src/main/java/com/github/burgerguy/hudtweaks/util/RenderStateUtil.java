package com.github.burgerguy.hudtweaks.util;

import com.github.burgerguy.hudtweaks.gui.HTOptionsScreen;
import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import com.github.burgerguy.hudtweaks.hud.HudContainer;
import com.github.burgerguy.hudtweaks.hud.element.HudElementContainer;
import net.minecraft.client.util.math.MatrixStack;

public final class RenderStateUtil {
    private RenderStateUtil() {
        // no instantiation, all contents static
    }

    // utility method for default elements
    public static void startRender(HTIdentifier identifier, MatrixStack... matrixStacks) {
        startRender(identifier, identifier, matrixStacks);
    }

    public static void startRender(HTIdentifier containerIdentifier, HTIdentifier elementIdentifier, MatrixStack... matrixStacks) {
        HudElementContainer hudElementContainer = HudContainer.getElementRegistry().getElementContainer(containerIdentifier);
        hudElementContainer.tryPushMatrix(elementIdentifier, matrixStacks);
        if (HTOptionsScreen.isOpen()) hudElementContainer.markDrawTestStart();
    }

    // utility method for default elements
    public static void finishRender(HTIdentifier identifier, MatrixStack... matrixStacks) {
        finishRender(identifier, identifier, matrixStacks);
    }

    public static void finishRender(HTIdentifier containerIdentifier, HTIdentifier elementIdentifier, MatrixStack... matrixStacks) {
        HudElementContainer hudElementContainer = HudContainer.getElementRegistry().getElementContainer(containerIdentifier);
        hudElementContainer.tryPopMatrix(elementIdentifier, matrixStacks);
        if (HTOptionsScreen.isOpen()) hudElementContainer.markDrawTestEnd();
    }
}
