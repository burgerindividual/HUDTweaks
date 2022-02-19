package com.github.burgerguy.hudtweaks.gui;

import com.github.burgerguy.hudtweaks.gui.widget.HTButtonWidget;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

public class PopupBoxScreen extends Screen {
    private static final int MINIMUM_WIDTH = 200;
    private static final int INNER_COLOR = 0x90424242;
    private static final int BORDER_COLOR = 0xFF000000;

    private final Screen previousScreen;
    private final Text message;
    private final Option[] options;

    private int popupWidth;
    private int popupHeight;
    private int popupX;
    private int popupY;

    private List<OrderedText> wrappedMessage;

    protected PopupBoxScreen(Screen previousScreen, Text title, Text message, Option... options) {
        super(title);
        this.previousScreen = previousScreen;
        this.message = message;
        this.options = options;
    }

    @Override
    protected void init() {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        //// calculate x sizes
        int x = 8; // left margin
        for (Option option : this.options) {
            option.xPosLeft = x;
            x += 5; // button internal left margin
            x += textRenderer.getWidth(option.message); // text inside button
            x += 5; // button internal right margin
            option.xPosRight = x;
            x += 4; // post-button spacing
        }
        x += 4; // add 4 to the post-button spacing for the final right margin of 8
        int optionsWidth = x;
        this.popupWidth = Math.max(x, MINIMUM_WIDTH);

        //// calculate y sizes
        int y = 10; // top margin
        this.wrappedMessage = textRenderer.wrapLines(this.message, this.popupWidth - 14); // left and right margins for text total to 14
        y += this.wrappedMessage.size() * textRenderer.fontHeight; // lines height
        y += 10; // bottom margin
        int optionsY = y;
        y += 14; // button height
        y += 5; // button bottom margin
        this.popupHeight = y;

        this.popupX = (int) (width / 2.0f - popupWidth / 2.0f);
        this.popupY = (int) (height / 2.0f - popupHeight / 2.0f);

        //// add buttons
        int optionStartX = (int) (width / 2.0f - optionsWidth / 2.0f);
        for (Option option : this.options) {
            this.addDrawableChild(new OptionWidget(optionStartX + option.xPosLeft, this.popupY + optionsY, option.xPosRight - option.xPosLeft, 14, option.getMessage(), option.getOnPressAction())); // button height 14
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        if (previousScreen != null) previousScreen.render(matrixStack, mouseX, mouseY, delta);

        int x1 = popupX;
        int y1 = popupY;
        int x2 = popupX + popupWidth;
        int y2 = popupY + popupHeight;
        
        DrawableHelper.fill(matrixStack, x1, y1, x2, y2, INNER_COLOR);

        DrawableHelper.fill(matrixStack, x1, y1, x2, y1 + 1, BORDER_COLOR);
        DrawableHelper.fill(matrixStack, x1, y2, x2, y2 - 1, BORDER_COLOR);
        DrawableHelper.fill(matrixStack, x1, y1 + 1, x1 + 1, y2 - 1, BORDER_COLOR);
        DrawableHelper.fill(matrixStack, x2, y1 + 1, x2 - 1, y2 - 1, BORDER_COLOR);

        int drawYOffset = y1 + 10; // top margin for text
        for(OrderedText line : wrappedMessage) {
            textRenderer.drawWithShadow(matrixStack, line, (float) width / 2 - (float) textRenderer.getWidth(line) / 2, drawYOffset, 0xFFFFFFFF);
            drawYOffset += textRenderer.fontHeight;
        }

        super.render(matrixStack, mouseX, mouseY, delta);
    }

    /**
     * Should be used instead of {@link MinecraftClient#setScreen(Screen)} to avoid calling
     * {@link Screen#removed()} on the screen this will be overlaid on.
     */
    public static void overlayPopupBox(MinecraftClient client, PopupBoxScreen popupBoxScreen) {
        popupBoxScreen.init(client, client.getWindow().getScaledWidth(), client.getWindow().getScaledHeight());
        client.currentScreen = popupBoxScreen;
    }

    /**
     * Emulates a setScreen without calling init on the previous screen.
     */
    @Override
    public void onClose() {
        this.removed();
        MinecraftClient.getInstance().currentScreen = previousScreen;
    }

    @Override
    public void tick() {
        if (previousScreen != null) previousScreen.tick();
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        if (previousScreen != null) previousScreen.resize(client, width, height);
        super.resize(client, width, height);
    }

    public static final class Option {
        private final Text message;
        private final PressAction onPressAction;

        private int xPosLeft;
        private int xPosRight;

        public Option(Text message, PressAction onPressAction) {
            this.message = message;
            this.onPressAction = onPressAction;
        }

        public Text getMessage() {
            return message;
        }

        public PressAction getOnPressAction() {
            return onPressAction;
        }
    }

    private class OptionWidget extends HTButtonWidget {
        private final PressAction onPressAction;

        public OptionWidget(int x, int y, int width, int height, Text message, PressAction onPressAction) {
            super(x, y, width, height, message);
            this.onPressAction = onPressAction;
        }

        @Override
        public void onPress() {
            onPressAction.onPress(PopupBoxScreen.this, this);
        }
    }

    public interface PressAction {
        void onPress(Screen popupScreen, HTButtonWidget button);
    }
}
