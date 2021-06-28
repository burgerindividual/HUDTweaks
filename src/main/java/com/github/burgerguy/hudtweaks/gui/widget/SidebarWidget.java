package com.github.burgerguy.hudtweaks.gui.widget;

import com.github.burgerguy.hudtweaks.gui.Tickable;
import com.github.burgerguy.hudtweaks.util.UnmodifiableMergedList;
import com.github.burgerguy.hudtweaks.util.Util;
import com.github.burgerguy.hudtweaks.util.gl.GLUtil;
import com.github.burgerguy.hudtweaks.util.gl.ScissorStack;
import com.github.burgerguy.hudtweaks.util.gui.ScrollableWrapperElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SidebarWidget extends AbstractParentElement implements Drawable, Selectable, Tickable { // FIXME: allow for selectables/narration
	private static final int SCROLLBAR_WIDTH = 2;
	private static final int SCROLLBAR_COLOR_1 = 0x20A0A0A0;
	private static final int SCROLLBAR_COLOR_2 = 0x905F5F5F;
	private static final int SCROLL_PIXEL_MULTIPLIER = 8;
	public int cutoffFromBottom = 25;

	private final List<Element> globalElements = new ArrayList<>();
	private final List<Drawable> globalDrawables = new ArrayList<>();
	private final List<Element> elements = new ArrayList<>();
	private final List<Drawable> drawables = new ArrayList<>();

	private final List<Entry> tempEntries = new ArrayList<>();
	private final Map<Element, DrawableEntry<?>> elementEntryMap = new HashMap<>();

	private int currentDrawY;

	private final Screen parentScreen;
	public int width;
	public int color;
	private float scrolledDist;

	public SidebarWidget(Screen parentScreen, int width, int color) {
		this.parentScreen = parentScreen;
		this.width = width;
		this.color = color;
	}

	public void addEntry(Entry entry) {
		tempEntries.add(entry);
	}

	public void addEntry(int index, Entry entry) {
		tempEntries.add(index, entry);
	}

	/**
	 * Utility method for addEntry(new PaddingEntry(height))
	 */
	public void addPadding(int height) {
		tempEntries.add(new PaddingEntry(height));
	}

	/**
	 * Utility method for addEntry(index, new PaddingEntry(height))
	 */
	public void addPadding(int index, int height) {
		tempEntries.add(index, new PaddingEntry(height));
	}

	public void setupEntries() {
		for(Entry entry : tempEntries) {
			if (entry instanceof DrawableEntry<?> drawableEntry) {
				drawableEntry.provideCoord(currentDrawY);
				Drawable drawable = drawableEntry.getDrawable();
				if (drawable != null) {
					drawables.add(drawable);
					if (drawable instanceof Element element) {
						Element scrollableWrapper = new ScrollableWrapperElement(element, () -> scrolledDist);
						elements.add(scrollableWrapper);
						elementEntryMap.put(scrollableWrapper, drawableEntry);
					}
				}
			}
			currentDrawY += entry.getHeight();
		}
		tempEntries.clear();
		updateScrolledDist();
	}

	public void clearEntries() {
		Element focused = getFocused();
		if (focused != null && elements.contains(focused)) {
			while (focused.changeFocus(true));
			setFocused(null);
		}
		drawables.clear();
		elements.clear();
		elementEntryMap.clear();
		currentDrawY = 0;
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

	public void updateScrolledDist() {
		scrolledDist = Util.minClamp(scrolledDist, 0, currentDrawY - parentScreen.height + cutoffFromBottom);
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
			if (currentDrawY > 0) {
				int optionsFullHeight = currentDrawY;
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
				drawable.render(matrixStack, mouseX, MathHelper.ceil(mouseY + scrolledDist), delta); // ceil fixes weird cutoff
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

		if (mouseY <= parentScreen.height - cutoffFromBottom) {
			for (Element element : elements) {
				if (element.mouseClicked(mouseX, mouseY, button)) {
					clickedElement = element;
					break;
				}
			}
		}

		for (Element element : globalElements) {
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
	public void setFocused(@Nullable Element focused) {
		super.setFocused(focused);
		if (focused != null) {
			DrawableEntry<?> entry = elementEntryMap.get(focused);
			if (entry != null) {
				int entryTop = entry.getY();
				int entryBottom = entryTop + entry.getHeight();
				int sidebarArea = parentScreen.height - cutoffFromBottom;
				if (scrolledDist > entryTop) {
					scrolledDist = entryTop;
				} else if (scrolledDist + sidebarArea < entryBottom) {
					scrolledDist = entryBottom - sidebarArea;
				}
			}
		}
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		boolean childScrolled = super.mouseScrolled(mouseX, mouseY, amount);
		if (childScrolled) {
			return true;
		} else {
			if (currentDrawY <= 0 || mouseY > parentScreen.height - cutoffFromBottom) {
				return false;
			} else {
				scrolledDist = Util.minClamp(scrolledDist - (float) amount * SCROLL_PIXEL_MULTIPLIER, 0, currentDrawY - parentScreen.height + cutoffFromBottom);
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
			if (drawable instanceof Tickable) {
				((Tickable) drawable).tick();
			}
			if (drawable instanceof TextFieldWidget textField) {
				textField.tick();
			}
			if (drawable instanceof LabeledFieldWidget<?> labeledField) {
				labeledField.tick();
			}
		}

		for (Drawable drawable : globalDrawables) {
			if (drawable instanceof Tickable tickable) {
				tickable.tick();
			}
			if (drawable instanceof TextFieldWidget textField) {
				textField.tick();
			}
			if (drawable instanceof LabeledFieldWidget<?> labeledField) {
				labeledField.tick();
			}
		}
	}

	@Override
	public SelectionType getType() {
		return SelectionType.NONE; // TODO: is this ok?
	}

	@Override
	public void appendNarrations(NarrationMessageBuilder builder) {
		// TODO: fix narration
	}

	public interface Entry { // TODO: scroll around with tab
		int getHeight();
	}

	public static class DrawableEntry<T extends Drawable> implements Entry {
		private final DrawableFactory<T> drawableFactory;
		private final int height;
		private T drawable;
		private int y;

		public DrawableEntry(DrawableFactory<T> drawableFactory, int height) {
			this.drawableFactory = drawableFactory;
			this.height = height;
		}

		public void provideCoord(int y) {
			this.y = y;
			this.drawable = drawableFactory.createDrawable(y);
		}

		public T getDrawable() {
			return drawable;
		}

		public int getY() {
			return y;
		}

		@Override
		public int getHeight() {
			return height;
		}

		@FunctionalInterface
		public interface DrawableFactory<T extends Drawable> {
			T createDrawable(int y);
		}
	}

	public static class PaddingEntry implements Entry {
		private final int height;

		public PaddingEntry(int height) {
			this.height = height;
		}

		@Override
		public int getHeight() {
			return height;
		}
	}
}
