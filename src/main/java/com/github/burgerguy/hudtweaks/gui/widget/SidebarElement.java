package com.github.burgerguy.hudtweaks.gui.widget;

import java.util.Iterator;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.google.common.collect.Lists;

import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;

public class SidebarElement extends AbstractParentElement implements Drawable {
	protected final List<AbstractButtonWidget> buttons = Lists.newArrayList();
	private final Screen parentScreen;
	public int width;
	public int color;
	
	public SidebarElement(Screen parentScreen, int width, int color) {
		this.parentScreen = parentScreen;
		this.width = width;
		this.color = color;
	}

	@Override
	public List<? extends Element> children() {
		return buttons;
	}
	
	public void addButton(AbstractButtonWidget button) {
		this.buttons.add(button);
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
		DrawableHelper.fill(matrixStack, 0, 0, width, parentScreen.height, color);
		
		for (AbstractButtonWidget button : buttons) {
			button.render(matrixStack, mouseX, mouseY, delta);
		}
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		Iterator<? extends Element> iterator = this.children().iterator();
		
		Element currentElement;
		do {
			if (!iterator.hasNext()) {
				if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
					if (isMouseOver(mouseX, mouseY)) {
						return true;
					}
				}
				return false;
			}
			
			currentElement = iterator.next();
		} while (!currentElement.mouseClicked(mouseX, mouseY, button));
		
		this.setFocused(currentElement);
		if (button == 0) {
			this.setDragging(true);
		}
		
		return true;
	}
	
	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return mouseX > 0 && mouseX < width && mouseY > 0 && mouseY < parentScreen.height;
	}
	
}
