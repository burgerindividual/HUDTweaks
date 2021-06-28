package com.github.burgerguy.hudtweaks.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class LabeledFieldWidget<T extends TextFieldWidget> implements Element, Drawable, ValueUpdatable {
    protected final Text text;
    protected final int color;
    protected final T textField;
    protected final int textX;
    protected final int textY;

    public LabeledFieldWidget(int x, int y, int width, int height, int color, int padding, Text text, TextFieldFactory<T> textFieldFactory) {
        this.text = text;
        this.color = color;
        this.textX = x;
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        this.textY = MathHelper.ceil((float) y + (height / 2.0F) - (textRenderer.fontHeight / 2.0F));
        int labelWidth = textRenderer.getWidth(text);
        this.textField = textFieldFactory.createTextField(x + labelWidth + padding, width - labelWidth - padding);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, text, textX, textY, color);
        textField.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return textField.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return textField.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return textField.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return textField.charTyped(chr, modifiers);
    }

    @Override
    public boolean changeFocus(boolean lookForwards) {
        return textField.changeFocus(lookForwards);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return textField.isMouseOver(mouseX, mouseY);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        textField.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return textField.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return textField.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return textField.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public void updateValue() {
        if (textField instanceof ValueUpdatable updatableTextField) {
            updatableTextField.updateValue();
        }
    }

    public void tick() {
        textField.tick();
    }

    public T getInnerTextField() {
        return textField;
    }

    @FunctionalInterface
    public interface TextFieldFactory<T extends TextFieldWidget> {
        T createTextField(int x, int width);
    }
}
