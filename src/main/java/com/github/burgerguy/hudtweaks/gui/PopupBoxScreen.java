package com.github.burgerguy.hudtweaks.gui;

import com.github.burgerguy.hudtweaks.gui.widget.HTButtonWidget;
import com.github.burgerguy.hudtweaks.util.gl.GLUtil;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

public class PopupBoxScreen extends Screen {
    private static final int MINIMUM_WIDTH = 140;
    private static final int MINIMUM_HEIGHT = 80;
    private static final int INNER_COLOR = 0x60424242;
    private static final int BORDER_COLOR = 0x60808080;

    private final Screen previousScreen;
    private final List<OrderedText> wrappedMessage;
    private final Option[] options;
    private final int optionY;
    private final int popupWidth;
    private final int popupHeight;

    protected PopupBoxScreen(Screen previousScreen, Text title, Text message, Option... options) {
        super(title);
        this.previousScreen = previousScreen;

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        //// calculate x sizes
        int x = 7; // left margin
        for (Option option : options) {
            option.xPosLeft = x;
            x += 3; // button internal left margin
            x += textRenderer.getWidth(option.message); // text inside button
            x += 3; // button internal right margin
            x += 4; // post-button spacing
            option.xPosRight = x;
        }
        x += 3; // add 3 to the post-button spacing for the final right margin of 7
        this.popupWidth = Math.max(x, MINIMUM_WIDTH);

        //// calculate y sizes
        int y = 10; // top margin
        this.wrappedMessage = textRenderer.wrapLines(message, popupWidth - 14); // left and right margins for text total to 14
        y += wrappedMessage.size() * textRenderer.fontHeight; // lines height
        y += 10; // bottom margin
        this.optionY = y;
        y += 14; // button height
        y += 5; // button bottom margin
        this.popupHeight = Math.max(y, MINIMUM_HEIGHT);

        this.options = options;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void init() {
        //// add buttons
        for (Option option : options) {
            int popupX = (int) (width / 2.0f - popupWidth / 2.0f);
            int popupY = (int) (height / 2.0f - popupHeight / 2.0f);
            this.addDrawableChild(new OptionWidget(popupX + option.xPosLeft, popupY + optionY, option.xPosRight - option.xPosLeft, 14, option.getMessage(), option.getOnPressAction())); // button height 14
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        previousScreen.render(matrixStack, mouseX, mouseY, delta);

        float x1 = width / 2.0f - popupWidth / 2.0f;
        float y1 = height / 2.0f - popupHeight / 2.0f;
        float x2 = width / 2.0f + popupWidth / 2.0f;
        float y2 = height / 2.0f + popupHeight / 2.0f;
        GLUtil.drawFillColor(matrixStack, x1, y1, x2, y1 + 1.0f, BORDER_COLOR);
        GLUtil.drawFillColor(matrixStack, x1, y2, x2, y2 - 1.0f, BORDER_COLOR);
        GLUtil.drawFillColor(matrixStack, x1, y1 + 1.0f, x1 + 1.0f, y2 - 1.0f, BORDER_COLOR);
        GLUtil.drawFillColor(matrixStack, x2, y1 + 1.0f, x2 - 1.0f, y2 - 1.0f, BORDER_COLOR);

        GLUtil.drawFillColor(matrixStack, x1, y1, x2, y2, INNER_COLOR);

        int drawYOffset = (int) y1 + 10; // top margin for text
        for(OrderedText line : wrappedMessage) {
            textRenderer.drawWithShadow(matrixStack, line, (float) width / 2 - (float) textRenderer.getWidth(line) / 2, drawYOffset, 0xFFFFFFFF);
            drawYOffset += textRenderer.fontHeight;
        }

        super.render(matrixStack, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        this.client.setScreen(previousScreen);
    }

    public static final class Option {
        private final Text message;
        private final Runnable onPressAction;

        private int xPosLeft;
        private int xPosRight;

        public Option(Text message, Runnable onPressAction) {
            this.message = message;
            this.onPressAction = onPressAction;
        }

        public Text getMessage() {
            return message;
        }

        public Runnable getOnPressAction() {
            return onPressAction;
        }
    }

    private static class OptionWidget extends HTButtonWidget {
        private final Runnable onPressAction;

        public OptionWidget(int x, int y, int width, int height, Text message, Runnable onPressAction) {
            super(x, y, width, height, message);
            this.onPressAction = onPressAction;
        }

        @Override
        public void onPress() {
            onPressAction.run();
        }
    }
}
