package com.github.burgerguy.hudtweaks.gui;

import java.util.List;
import java.util.ListIterator;
import java.util.function.Supplier;

import com.github.burgerguy.hudtweaks.config.ConfigHelper;
import com.github.burgerguy.hudtweaks.gui.widget.ArrowButtonWidget;
import com.github.burgerguy.hudtweaks.gui.widget.ElementLabelWidget;
import com.github.burgerguy.hudtweaks.gui.widget.SidebarWidget;
import com.github.burgerguy.hudtweaks.hud.HudContainer;
import com.github.burgerguy.hudtweaks.hud.element.HudElement;
import com.github.burgerguy.hudtweaks.hud.element.HudElementWidget;
import com.github.burgerguy.hudtweaks.util.Util;

import io.netty.util.BooleanSupplier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TickableElement;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

public class HTOptionsScreen extends Screen {
	private static final int SIDEBAR_WIDTH = 116;
	private static final int SIDEBAR_COLOR = 0x60424242;
	
	private static int screensOpened = 0;
	
	private final Screen prevScreen;
	private final SidebarWidget sidebar;
	private ElementLabelWidget elementLabel;
	
	private HudElementWidget focusedHudElement;
	
	public HTOptionsScreen(Screen prevScreen) {
		super(new TranslatableText("hudtweaks.options"));
		this.prevScreen = prevScreen;
		
		sidebar = new SidebarWidget(this, SIDEBAR_WIDTH, SIDEBAR_COLOR);
	}
	
	@Override
	protected void init() {
		super.init();
		
		// normal drawables are cleared already when setFocused(null) is invoked before
		sidebar.clearGlobalDrawables();
		// scrolledDist needs to be updated when screen is resized, and init is called on resize
		sidebar.updateScrolledDist();
		
		screensOpened++;

		for (HudElement element : HudContainer.getElements()) {
			Element widget = element.createWidget(sidebar::updateValues);
			if (widget != null) {
				children.add(widget);
			}
		}
		
		// We want normal elements to be the first to be selected. If they're both HudElementWidgets, use their compareTos.
		// TODO: This doesn't work with the new scale stuff. Instead, do these checks on click.
		children.sort((e1, e2) -> {
			boolean isHudElement1 = e1 instanceof HudElementWidget;
			boolean isHudElement2 = e2 instanceof HudElementWidget;
			if (isHudElement1 && !isHudElement2) {
				return 1;
			} else if (isHudElement1 && isHudElement2) {
				return ((HudElementWidget) e1).compareTo((HudElementWidget) e2);
			} else {
				return 0; 
			}
		});
		
		// this is added to the start of the list so it is selected before anything else
		children.add(0, sidebar);
		
		elementLabel = new ElementLabelWidget(sidebar.width / 2, height - 17, sidebar.width - 42);
		ArrowButtonWidget leftArrow = new ArrowButtonWidget(5, height - 21, true, new TranslatableText("hudtweaks.options.previous_element.name"), b -> {
			changeHudElementFocus(false);
		});
		ArrowButtonWidget rightArrow = new ArrowButtonWidget(sidebar.width - 21, height - 21, false, new TranslatableText("hudtweaks.options.next_element.name"), b -> {
			changeHudElementFocus(true);
		});
		sidebar.addGlobalDrawable(elementLabel);
		sidebar.addGlobalDrawable(leftArrow);
		sidebar.addGlobalDrawable(rightArrow);
	}
	
	@Override
	/**
	 * This is done to prevent one screen from incrementing the counter more than once.
	 */
	public void resize(MinecraftClient client, int width, int height) {
		screensOpened--;
		this.init(client, width, height);
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
		super.renderBackground(matrixStack);
		
		// reverse order
		for (int i = children.size() - 1; i >= 0; i--) {
			Element element = children.get(i);
			if (element instanceof Drawable && !(element instanceof AbstractButtonWidget)) {
				((Drawable) element).render(matrixStack, mouseX, mouseY, delta);
			}
		}
		
		super.render(matrixStack, mouseX, mouseY, delta);
	}
	
	@Override
	public void renderBackground(MatrixStack matrixStack, int vOffset) {
		if (client.world == null) {
			renderBackgroundTexture(vOffset);
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
		
		// remove focus from the previous hud element if the clicked element can replace it
		if (focusedHudElement != null && clickedElement instanceof HudElementWidget && !focusedHudElement.equals(clickedElement)) {
			while (focusedHudElement.changeFocus(true));
		}
		
		setFocused(clickedElement); // sets the parent's focused element (can be set to null)
		
		if (clickedElement != null) {			
			if (button == 0) {
				setDragging(true);
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void onClose() {
		ConfigHelper.trySaveConfig();
		for(Element child : children) {
			if (child instanceof AutoCloseable) {
				try {
					((AutoCloseable) child).close();
				} catch (Exception e) {
					Util.LOGGER.error("Error closing HUDTweaks options screen", e);
				}
			}
		}
		if (client.world == null) {
			client.openScreen(prevScreen);
		} else {
			client.openScreen(null);
		}
		screensOpened--;
	}
	
	@Override
	public void setFocused(Element focused) {
		if (focused instanceof HudElementWidget && !focused.equals(focusedHudElement)) {
			focusedHudElement = (HudElementWidget) focused;
			sidebar.clearDrawables();
			HudElement element = focusedHudElement.getElement();
			element.fillSidebar(sidebar);
			sidebar.setSidebarOptionsHeightSupplier(() -> element.getSidebarOptionsHeight());
			elementLabel.setHudElement(element);
		}
		
		if (focused == null) {
			focusedHudElement = null;
			sidebar.clearDrawables();
			sidebar.setSidebarOptionsHeightSupplier(null);
			if (elementLabel != null) elementLabel.setHudElement(null);
		}
		
		super.setFocused(focused);
	}
	
	@Override
	// when pressing tab and shift-tab, we only want it to change the sidebar.
	public boolean changeFocus(boolean lookForwards) {
		return sidebar.changeFocus(lookForwards);
	}
	
	private void changeHudElementFocus(boolean lookForwards) {
		List<? extends Element> children = children();
		
		int newIdx = 0;
		int curIdx;
		if (focusedHudElement != null && (curIdx = children.indexOf(focusedHudElement)) >= 0) {
			newIdx = curIdx + (lookForwards ? 1 : 0);
		} else {
			if (lookForwards) {
				newIdx = 0;
			} else {
				newIdx = children.size();
			}
		}
		
		ListIterator<? extends Element> listIterator = children.listIterator(newIdx);
		BooleanSupplier hasNearbySupplier = lookForwards ? listIterator::hasNext : listIterator::hasPrevious;
		Supplier<? extends Element> elementSupplier = lookForwards ? listIterator::next : listIterator::previous;
		
		Element currentElement = null;
		do {
			try {
				if (!hasNearbySupplier.get()) {
					return; // keep focus if none nearby
				}
			} catch (Exception ignored) {
				return;
			}
			
			currentElement = elementSupplier.get();
		} while (!(currentElement instanceof HudElementWidget) || !currentElement.changeFocus(lookForwards));
		
		// remove focus from the previous hud element
		if (focusedHudElement != null) {
			while (focusedHudElement.changeFocus(true));
		}
		
		setFocused(currentElement);
	}
	
	@Override
	public void tick() {
		for (Element element : children()) {
			if (element instanceof TickableElement) {
				((TickableElement) element).tick();
			}
			if (element instanceof TextFieldWidget) {
				((TextFieldWidget) element).tick();
			}
		}
	}
	
	public static boolean isOpen() {
		return screensOpened > 0;
	}
	
}
