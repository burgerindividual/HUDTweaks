package com.github.burgerguy.hudtweaks.gui.widget;

import com.github.burgerguy.hudtweaks.util.gui.OverflowTextRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public abstract class HTOverflowButtonWidget extends HTButtonWidget {
    protected final OverflowTextRenderer overflowTextRenderer;

    public HTOverflowButtonWidget(int x, int y, int width, int height, Text message) {
        super(x, y, width, height, message);
        // 8 = textRenderer.fontHeight - 1 (forgot why the -1 is there lol, maybe it looked better)
        this.overflowTextRenderer = new OverflowTextRenderer(40, 40, 4, x + width / 2, y + (height - 8) / 2, width - 2);
    }

    @Override
    protected void renderText(MatrixStack matrixStack, TextRenderer textRenderer, Text message, float tickDelta, int textColor) {
        overflowTextRenderer.render(matrixStack, textRenderer, message, tickDelta, textColor);
    }
}
