package com.github.burgerguy.hudtweaks.util.gui;

import java.util.function.DoubleSupplier;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;

public class ScrollableWrapperElement<T extends Element & Drawable> implements Element, Drawable {
	private final T innerElement;
	private final DoubleSupplier scrolledDistSupplier;

	public ScrollableWrapperElement(T innerElement, DoubleSupplier scrolledDistSupplier) {
		this.innerElement = innerElement;
		this.scrolledDistSupplier = scrolledDistSupplier;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		innerElement.render(matrices, mouseX, (int)(mouseY + scrolledDistSupplier.getAsDouble()), delta);
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		innerElement.mouseMoved(mouseX, mouseY + scrolledDistSupplier.getAsDouble());
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return innerElement.mouseClicked(mouseX, mouseY + scrolledDistSupplier.getAsDouble(), button);
	}
	
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		return innerElement.mouseReleased(mouseX, mouseY + scrolledDistSupplier.getAsDouble(), button);
	}
	
	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return innerElement.mouseDragged(mouseX, mouseY + scrolledDistSupplier.getAsDouble(), button, deltaX, deltaY);
	}
	
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		return innerElement.mouseScrolled(mouseX, mouseY + scrolledDistSupplier.getAsDouble(), amount);
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return innerElement.keyPressed(keyCode, scanCode, modifiers);
	}
	
	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		return innerElement.keyReleased(keyCode, scanCode, modifiers);
	}
	
	@Override
	public boolean charTyped(char chr, int keyCode) {
		return innerElement.charTyped(chr, keyCode);
	}
	
	@Override
	public boolean changeFocus(boolean lookForwards) {
		return innerElement.changeFocus(lookForwards);
	}
	
	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return innerElement.isMouseOver(mouseX, mouseY + scrolledDistSupplier.getAsDouble());
	}
}
