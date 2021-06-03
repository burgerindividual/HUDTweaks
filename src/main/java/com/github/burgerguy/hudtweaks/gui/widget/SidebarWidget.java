package com.github.burgerguy.hudtweaks.gui.widget;

import com.github.burgerguy.hudtweaks.util.UnmodifiableMergedList;
import com.github.burgerguy.hudtweaks.util.Util;
import com.github.burgerguy.hudtweaks.util.gl.GLUtil;
import com.github.burgerguy.hudtweaks.util.gl.ScissorStack;
import com.github.burgerguy.hudtweaks.util.gui.ScrollableWrapperElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TickableElement;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;

public class SidebarWidget extends AbstractParentElement implements Drawable, TickableElement {
	private static final int SCROLLBAR_WIDTH = 2;
	private static final int SCROLLBAR_COLOR_1 = 0x20A0A0A0;
	private static final int SCROLLBAR_COLOR_2 = 0x905F5F5F;
	private static final int SCROLL_PIXEL_MULTIPLIER = 8;
	public int cutoffFromBottom = 25;

	private final List<Element> globalElements = new ArrayList<>();
	private final List<Drawable> globalDrawables = new ArrayList<>();
	private final List<Element> elements = new ArrayList<>();
	private final List<Drawable> drawables = new ArrayList<>();

	private final Screen parentScreen;
	public int width;
	public int color;
	private IntSupplier optionsHeightSupplier;
	private float scrolledDist;

	public SidebarWidget(Screen parentScreen, int width, int color) {
		this.parentScreen = parentScreen;
		this.width = width;
		this.color = color;
	}

	public void addDrawable(Drawable drawable) {
		drawables.add(drawable);
		if (drawable instanceof Element) {
			elements.add(new ScrollableWrapperElement((Element) drawable, () -> scrolledDist));
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

	public void setSidebarOptionsHeightSupplier(IntSupplier optionsHeightSupplier) {
		this.optionsHeightSupplier = optionsHeightSupplier;
		updateScrolledDist();
	}

	public void updateScrolledDist() {
		if (optionsHeightSupplier != null) {
			scrolledDist = Util.minClamp(scrolledDist, 0, optionsHeightSupplier.getAsInt() - parentScreen.height + cutoffFromBottom);
		} else {
			scrolledDist = 0;
		}
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

		float optionsVisibleHeight = parentScreen.height - cutoffFromBottom;
		if (optionsVisibleHeight > 0) {
			boolean scrollable = false;
			boolean matrixPushed = false;
			if (optionsHeightSupplier != null) {
				int optionsFullHeight = optionsHeightSupplier.getAsInt();
				if (optionsVisibleHeight < optionsFullHeight) {
					float scale = (float) MinecraftClient.getInstance().getWindow().getScaleFactor();
					int x = width - 2;
					GLUtil.drawFillColor(matrixStack, x, 0, x + SCROLLBAR_WIDTH, optionsVisibleHeight, SCROLLBAR_COLOR_1);
					GLUtil.drawFillColor(matrixStack, x, scrolledDist / optionsFullHeight * optionsVisibleHeight, x + SCROLLBAR_WIDTH, (optionsVisibleHeight + scrolledDist) / optionsFullHeight * optionsVisibleHeight, SCROLLBAR_COLOR_2);
					ScissorStack.pushScissorArea(0, (int)(cutoffFromBottom * scale), (int) (width * scale), (int)(optionsVisibleHeight * scale));
					scrollable = true;
					if (scrolledDist > 0) {
						matrixStack.push();
						matrixStack.translate(0, -scrolledDist, 0);
						matrixPushed = true;
					}
				}
			}

			for (Drawable drawable : drawables) {
				drawable.render(matrixStack, mouseX, (int) (mouseY + scrolledDist), delta); // TODO: make sure this rounding is ok, pretty sure it causes a cutoff when scrolling all the way down
			}

			if (scrollable) {
				if (matrixPushed) matrixStack.pop();
				ScissorStack.popScissorArea();
			}
		}

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
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		boolean childScrolled = super.mouseScrolled(mouseX, mouseY, amount);
		if (childScrolled) {
			return true;
		} else {
			if (optionsHeightSupplier == null || mouseY > parentScreen.height - cutoffFromBottom) {
				return false;
			} else {
				scrolledDist = Util.minClamp(scrolledDist - (float) amount * SCROLL_PIXEL_MULTIPLIER, 0, optionsHeightSupplier.getAsInt() - parentScreen.height + cutoffFromBottom);
				return true;
			}
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
