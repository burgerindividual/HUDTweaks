package com.github.burgerguy.hudtweaks.gui.widget;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.github.burgerguy.hudtweaks.gui.HTOptionsScreen;
import com.github.burgerguy.hudtweaks.util.UnmodifiableMergedList;

import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.TickableElement;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;

public class SidebarWidget extends AbstractParentElement implements Drawable, TickableElement {
	private final List<Element> globalElements = new ArrayList<>();
	private final List<Drawable> globalDrawables = new ArrayList<>();
	private final List<Element> elements = new ArrayList<>();
	private final List<Drawable> drawables = new ArrayList<>();
	
	private final HTOptionsScreen optionsScreen;
	public int width;
	public int color;

	
	public SidebarWidget(HTOptionsScreen optionsScreen, int width, int color) {
		this.optionsScreen = optionsScreen;
		this.width = width;
		this.color = color;
	}
	
	public void updateValues() {
		for (Drawable drawable : globalDrawables) {
			if (drawable instanceof ValueUpdatable) {
				((ValueUpdatable) drawable).updateValue();
			}
		}
		
		for (Drawable drawable : drawables) {
			if (drawable instanceof ValueUpdatable) {
				((ValueUpdatable) drawable).updateValue();
			}
		}
	}
	
	public void addDrawable(Drawable drawable) {
		drawables.add(drawable);
		if (drawable instanceof Element) {
			elements.add((Element) drawable);
		}
	}
	
	public void clearDrawables() {
		drawables.clear();
		elements.clear();
	}

	@Override
	public List<? extends Element> children() {
		return new UnmodifiableMergedList<>(globalElements, elements);
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
		DrawableHelper.fill(matrixStack, 0, 0, width, optionsScreen.height, color);
		
		for (Drawable drawable : globalDrawables) {
			drawable.render(matrixStack, mouseX, mouseY, delta);
		}
		
		for (Drawable drawable : drawables) {
			drawable.render(matrixStack, mouseX, mouseY, delta);
		}
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		Iterator<? extends Element> iterator = children().iterator();
		
		Element currentElement;
		do {
			if (!iterator.hasNext()) {
				setFocused(null);
				if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
					if (isMouseOver(mouseX, mouseY)) {
						return true;
					}
				}
				return false;
			}
			
			currentElement = iterator.next();
		} while (!currentElement.mouseClicked(mouseX, mouseY, button));
		
		setFocused(currentElement);
		if (button == 0) {
			setDragging(true);
		}
		
		return true;
	}
	
	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return mouseX >= 0 && mouseX <= width && mouseY >= 0 && mouseY <= optionsScreen.height;
	}

	@Override
	public void tick() {
		for (Drawable drawable : globalDrawables) {
			if (drawable instanceof TickableElement) {
				((TickableElement) drawable).tick();
			}
			if (drawable instanceof TextFieldWidget) {
				((TextFieldWidget) drawable).tick();
			}
		}
		
		for (Drawable drawable : drawables) {
			if (drawable instanceof TickableElement) {
				((TickableElement) drawable).tick();
			}
			if (drawable instanceof TextFieldWidget) {
				((TextFieldWidget) drawable).tick();
			}
		}
	}
	
	
	
}
