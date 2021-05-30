package com.github.burgerguy.hudtweaks.util.gui;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;

public class DisableableWrapperElement<T extends Element & Drawable> implements Element, Drawable {
	private final T innerElement;
	private boolean disabled = true;

	public DisableableWrapperElement(T innerElement) {
		this.innerElement = innerElement;
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (!disabled) innerElement.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		if (!disabled) innerElement.mouseMoved(mouseX, mouseY);
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return !disabled && innerElement.mouseClicked(mouseX, mouseY, button);
	}
	
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		return !disabled && innerElement.mouseReleased(mouseX, mouseY, button);
	}
	
	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return !disabled && innerElement.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}
	
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		return !disabled && innerElement.mouseScrolled(mouseX, mouseY, amount);
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return !disabled && innerElement.keyPressed(keyCode, scanCode, modifiers);
	}
	
	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		return !disabled && innerElement.keyReleased(keyCode, scanCode, modifiers);
	}
	
	@Override
	public boolean charTyped(char chr, int keyCode) {
		return !disabled && innerElement.charTyped(chr, keyCode);
	}
	
	@Override
	public boolean changeFocus(boolean lookForwards) {
		return !disabled && innerElement.changeFocus(lookForwards);
	}
	
	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return !disabled && innerElement.isMouseOver(mouseX, mouseY);
	}

	public T getInnerElement() {
		return innerElement;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
}
