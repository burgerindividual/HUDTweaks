package com.github.burgerguy.hudtweaks.gui.widget;

import java.util.ArrayList;
import java.util.List;

import com.github.burgerguy.hudtweaks.util.UnmodifiableMergedList;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TickableElement;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;

public class SidebarWidget extends AbstractParentElement implements Drawable, TickableElement {
	private static final int CUTOFF_FROM_BOTTOM = 25;
	
	private final List<Element> globalElements = new ArrayList<>();
	private final List<Drawable> globalDrawables = new ArrayList<>();
	private final List<Element> elements = new ArrayList<>();
	private final List<Drawable> drawables = new ArrayList<>();
	
	private final Screen parentScreen;
	public int width;
	public int color;
	
	public SidebarWidget(Screen parentScreen, int width, int color) {
		this.parentScreen = parentScreen;
		this.width = width;
		this.color = color;
	}
	
	public void addDrawable(Drawable drawable) {
		drawables.add(drawable);
		if (drawable instanceof Element) {
			elements.add((Element) drawable);
		}
	}
	
	public void clearDrawables() {
		Element focused = getFocused();
		if (focused != null && elements.contains(focused)) {
			while (focused.changeFocus(true));
			setFocused(null);
		}
		drawables.clear();
		elements.clear();
	}
	
	public void addGlobalDrawable(Drawable drawable) {
		globalDrawables.add(drawable);
		if (drawable instanceof Element) {
			globalElements.add((Element) drawable);
		}
	}
	
	public void clearGlobalDrawables() {
		Element focused = getFocused();
		if (focused != null && globalElements.contains(focused)) {
			while (focused.changeFocus(true));
			setFocused(null);
		}
		globalDrawables.clear();
		globalElements.clear();
	}

	@Override
	public List<? extends Element> children() {
		return new UnmodifiableMergedList<>(globalElements, elements);
	}
	
	public void updateValues() {
		for (Drawable drawable : drawables) {
			if (drawable instanceof ValueUpdatable) {
				((ValueUpdatable) drawable).updateValue();
			}
		}
		
		for (Drawable drawable : globalDrawables) {
			if (drawable instanceof ValueUpdatable) {
				((ValueUpdatable) drawable).updateValue();
			}
		}
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
		DrawableHelper.fill(matrixStack, 0, 0, width, parentScreen.height, color);
		
		double scale = MinecraftClient.getInstance().getWindow().getScaleFactor();
		int offset = (int) (CUTOFF_FROM_BOTTOM * scale);
		int scaledWindowHeight = (int) (parentScreen.height * scale);
		RenderSystem.enableScissor(0, offset, (int) (width * scale), scaledWindowHeight - offset);
		for (Drawable drawable : drawables) {
			drawable.render(matrixStack, mouseX, mouseY, delta);
		}
		RenderSystem.disableScissor();
		
		for (Drawable drawable : globalDrawables) {
			drawable.render(matrixStack, mouseX, mouseY, delta);
		}
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		Element clickedElement = null;
		
		for (Element element : children()) {
			if (element.mouseClicked(mouseX, mouseY, button)) {
				clickedElement = element;
				break;
			}
		}
		
		Element lastFocused = getFocused();
		if (lastFocused != null && !lastFocused.equals(clickedElement)) {
			while (lastFocused.changeFocus(true)); // removes focus from the previous element
		}
		setFocused(clickedElement); // sets the parent's focused element (can be set to null)
		
		if (clickedElement != null) {
			if (button == 0) {
				setDragging(true);
			}
			
			return true;
		} else {
			return isMouseOver(mouseX, mouseY); // we want the hud element focus to remain, even if empty space is clicked on the sidebar
		}
	}
	
	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return mouseX >= 0 && mouseX <= width && mouseY >= 0 && mouseY <= parentScreen.height;
	}

	@Override
	public void tick() {
		for (Drawable drawable : drawables) {
			if (drawable instanceof TickableElement) {
				((TickableElement) drawable).tick();
			}
			if (drawable instanceof TextFieldWidget) {
				((TextFieldWidget) drawable).tick();
			}
		}
		
		for (Drawable drawable : globalDrawables) {
			if (drawable instanceof TickableElement) {
				((TickableElement) drawable).tick();
			}
			if (drawable instanceof TextFieldWidget) {
				((TextFieldWidget) drawable).tick();
			}
		}
	}
	
}
