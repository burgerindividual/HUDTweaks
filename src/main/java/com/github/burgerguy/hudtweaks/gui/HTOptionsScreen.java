package com.github.burgerguy.hudtweaks.gui;

import com.github.burgerguy.hudtweaks.HudTweaksMod;
import com.github.burgerguy.hudtweaks.gui.widget.*;
import com.github.burgerguy.hudtweaks.hud.HudContainer;
import com.github.burgerguy.hudtweaks.hud.element.HudElement;
import com.github.burgerguy.hudtweaks.hud.element.HudElementContainer;
import com.github.burgerguy.hudtweaks.hud.element.HudElementWidget;
import com.github.burgerguy.hudtweaks.util.Util;
import io.netty.util.BooleanSupplier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Supplier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class HTOptionsScreen extends Screen {
	private static final int SIDEBAR_WIDTH = 116;
	private static final int SIDEBAR_CUTOFF_FROM_BOTTOM = 25;
	private static final int SIDEBAR_COLOR = 0x60424242;

	private static final Comparator<Element> SMALLEST_FIRST_COMPARATOR = (e1, e2) -> {
		boolean isHudElement1 = e1 instanceof HudElementWidget;
		boolean isHudElement2 = e2 instanceof HudElementWidget;
		if (isHudElement1 && !isHudElement2) {
			return 1;
		} else if (isHudElement1) {
			HudElement h1 = ((HudElementWidget) e1).getElementContainer().getActiveElement();
			HudElement h2 = ((HudElementWidget) e2).getElementContainer().getActiveElement();
			return Float.compare(
					h1.getWidth() * h1.getHeight(),
					h2.getWidth() * h2.getHeight()
			);
		} else {
			return 0;
		}
	};

	private static final Comparator<Element> LARGEST_FIRST_COMPARATOR = (e1, e2) -> {
		boolean isHudElement1 = e1 instanceof HudElementWidget;
		boolean isHudElement2 = e2 instanceof HudElementWidget;
		if (isHudElement1 && !isHudElement2) {
			return 1;
		} else if (isHudElement1) {
			HudElement h1 = ((HudElementWidget) e1).getElementContainer().getActiveElement();
			HudElement h2 = ((HudElementWidget) e2).getElementContainer().getActiveElement();
			return Float.compare(
					h2.getWidth() * h2.getHeight(),
					h1.getWidth() * h1.getHeight()
			);
		} else {
			return 0;
		}
	};

	private static final Comparator<Element> ALPHABETICAL_COMPARATOR = (e1, e2) -> {
		boolean isHudElement1 = e1 instanceof HudElementWidget;
		boolean isHudElement2 = e2 instanceof HudElementWidget;
		if (isHudElement1 && !isHudElement2) {
			return 1;
		} else if (isHudElement1) {
			String name1 = ((HudElementWidget) e1).getElementContainer().getActiveElement().getIdentifier().elementId().toDisplayableString();
			String name2 = ((HudElementWidget) e2).getElementContainer().getActiveElement().getIdentifier().elementId().toDisplayableString();
			return String.CASE_INSENSITIVE_ORDER.compare(name1, name2);
		} else {
			return 0;
		}
	};

	private static int screensOpened = 0;

	private final Screen previousScreen;
	private final SidebarWidget sidebar;
	private ElementLabelWidget elementLabelWidget;

	private HudElementWidget focusedHudElement;

	private boolean worldExists = false;

	public HTOptionsScreen(Screen previousScreen) {
		super(Text.translatable("hudtweaks.options"));
		this.previousScreen = previousScreen;

		sidebar = new SidebarWidget(this, SIDEBAR_WIDTH, SIDEBAR_COLOR, SIDEBAR_CUTOFF_FROM_BOTTOM);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void init() {
		super.init();

		// normal drawables are cleared already when setFocused(null) is invoked before
		sidebar.clearGlobalDrawables();
		// scrolledDist needs to be updated when screen is resized, and init is called on resize
		sidebar.updateScrolledDist();

		screensOpened++;

		worldExists = client.world != null;

		if (worldExists) {
			for (HudElementContainer elementContainer : HudContainer.getElementRegistry().getElementContainers()) {
				HudElementWidget widget = elementContainer.createWidget(sidebar::updateValues);
				if (widget != null) {
					addDrawableChild(widget);
				}
			}

			// Sort alphabetically internally and when cycling, but sort by size when rendering and selecting
			children().sort(ALPHABETICAL_COMPARATOR);

			// this is added to the start of the list so it is selected before anything else
			((List<Element>) children()).add(0, sidebar); // only way to do this is with unchecked casts

			setFocused(sidebar); // we basically always want the sidebar to be focused, the things inside the sidebar are the ones that are

			elementLabelWidget = new ElementLabelWidget(sidebar.width / 2, height - 17, sidebar.width - 42);
			ArrowButtonWidget leftArrowButton = new ArrowButtonWidget(5, height - 21, true, Text.translatable("hudtweaks.options.previous_element.name"), b -> changeHudElementFocus(false));
			ArrowButtonWidget rightArrowButton = new ArrowButtonWidget(sidebar.width - 21, height - 21, false, Text.translatable("hudtweaks.options.next_element.name"), b -> changeHudElementFocus(true));

			sidebar.addGlobalDrawable(elementLabelWidget);
			sidebar.addGlobalDrawable(leftArrowButton);
			sidebar.addGlobalDrawable(rightArrowButton);
		}
	}

	/**
	 * This is done to prevent one screen from incrementing the counter more than once.
	 */
	@Override
	public void resize(MinecraftClient client, int width, int height) {
		screensOpened--;
		this.init(client, width, height);
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
		super.renderBackground(matrixStack);

		if (worldExists) {
			for(Element element : getLargestFirstChildren()) {
				if (element instanceof Drawable drawable) {
					drawable.render(matrixStack, mouseX, mouseY, delta);
				}
			}
		} else {
			Text text = Text.translatable("hudtweaks.options.no_world_prompt");
			List<OrderedText> wrappedLines = textRenderer.wrapLines(text, width);
			int textHeight = wrappedLines.size() * textRenderer.fontHeight;
			int drawYOffset = 0;
			for(OrderedText line : wrappedLines) {
				textRenderer.drawWithShadow(matrixStack, line, (float) width / 2 - (float) textRenderer.getWidth(line) / 2, (float) height / 2 - (float) textHeight / 2 + drawYOffset, 0xFFFFFFFF);
				drawYOffset += textRenderer.fontHeight;
			}
		}
	}

	@Override
	public void renderBackground(MatrixStack matrixStack, int vOffset) {
		if (!worldExists) {
			renderBackgroundTexture(vOffset);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		Element clickedElement = null;

		for (Element element : getSmallestFirstChildren()) {
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
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (focusedHudElement != null && isDragging() && focusedHudElement.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
			return true;
		} else {
			return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
		}
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (focusedHudElement != null && focusedHudElement.mouseReleased(mouseX, mouseY, button)) {
			this.setDragging(false);
			return true;
		} else {
			return super.mouseReleased(mouseX, mouseY, button);
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		// ctrl-r = reset element
		// ctrl-shift-r = reset all
		if (keyCode == GLFW.GLFW_KEY_R && Screen.hasControlDown()) {
			if (Screen.hasShiftDown()) {
				showResetAllPopup();
				return true;
			} else if (focusedHudElement != null) {
				showResetElementPopup();
				return true;
			}
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	private void showResetElementPopup() {
		if (focusedHudElement == null) return;

		PopupBoxScreen popupBoxScreen = new PopupBoxScreen(
				HTOptionsScreen.this,
				Text.translatable("hudtweaks.options.popup.confirm.name"),
				Text.translatable("hudtweaks.options.popup.reset_element"),
				new PopupBoxScreen.Option(
						Text.translatable("hudtweaks.options.popup.option.reset"),
						(s, b) -> {
							focusedHudElement.getElementContainer().getActiveElement().resetToDefaults();
							sidebar.updateValues();
							s.close();
						}
				),
				new PopupBoxScreen.Option(
						Text.translatable("hudtweaks.options.popup.option.cancel"),
						(s, b) -> s.close()
				)
		);
		PopupBoxScreen.overlayPopupBox(client, popupBoxScreen);
	}

	private void showResetAllPopup() {
		PopupBoxScreen popupBoxScreen1 = new PopupBoxScreen(
				HTOptionsScreen.this,
				Text.translatable("hudtweaks.options.popup.confirm.name"),
				Text.translatable("hudtweaks.options.popup.reset_all_1"),
				new PopupBoxScreen.Option(
						Text.translatable("hudtweaks.options.popup.option.reset"),
						(s, b) -> {
							s.close();
							PopupBoxScreen popupBoxScreen2 = new PopupBoxScreen(
									HTOptionsScreen.this,
									Text.translatable("hudtweaks.options.popup.confirm.name"),
									Text.translatable("hudtweaks.options.popup.reset_all_2"),
									new PopupBoxScreen.Option(
											Text.translatable("hudtweaks.options.popup.option.reset"),
											(s2, b2) -> {
												HudContainer.getElementRegistry().resetToDefaults();
												s2.close();
											}
									),
									new PopupBoxScreen.Option(
											Text.translatable("hudtweaks.options.popup.option.cancel"),
											(s2, b2) -> s2.close()
									)
							);
							PopupBoxScreen.overlayPopupBox(client, popupBoxScreen2);
						}
				),
				new PopupBoxScreen.Option(
						Text.translatable("hudtweaks.options.popup.option.cancel"),
						(s, b) -> s.close()
				)
		);
		PopupBoxScreen.overlayPopupBox(client, popupBoxScreen1);
	}

	@Override
	public void removed() {
		screensOpened--;
	}

	@Override
	public void close() {
		// TODO: move to separate button
		HudTweaksMod.getConfig().trySaveConfig();
		for(Element child : children()) {
			if (child instanceof AutoCloseable) {
				try {
					((AutoCloseable) child).close();
				} catch (Exception e) {
					Util.LOGGER.error("Error closing HUDTweaks options screen", e);
				}
			}
		}
		if (client.world == null) {
			client.setScreen(previousScreen);
		} else {
			client.setScreen(null);
		}
	}

	@Override
	public void setFocused(Element focused) {
		if (focused instanceof HudElementWidget) {
			if (!focused.equals(focusedHudElement)) {
				focusedHudElement = (HudElementWidget) focused;
				sidebar.clearEntries();
				HudElement element = focusedHudElement.getElementContainer().getActiveElement();
				element.fillSidebar(sidebar);
				sidebar.setupEntries();
				elementLabelWidget.setHudElementContainer(focusedHudElement.getElementContainer());
			}
		} else if (focused == null) {
			focusedHudElement = null;
			sidebar.clearEntries();
			if (elementLabelWidget != null) elementLabelWidget.setHudElementContainer(null);
		} else {
			super.setFocused(focused);
		}
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
			if (!lookForwards) {
				newIdx = children.size();
			}
		}

		ListIterator<? extends Element> listIterator = children.listIterator(newIdx);
		BooleanSupplier hasNearbySupplier = lookForwards ? listIterator::hasNext : listIterator::hasPrevious;
		Supplier<? extends Element> elementSupplier = lookForwards ? listIterator::next : listIterator::previous;

		Element currentElement;
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
			if (element instanceof Tickable) {
				((Tickable) element).tick();
			}
			if (element instanceof TextFieldWidget) {
				((TextFieldWidget) element).tick();
			}
		}
	}

	public static boolean isOpen() {
		return screensOpened > 0;
	}

	private List<Element> getSmallestFirstChildren() { // TODO: maybe cache these or something
		List<Element> sizeSortedElements = new ArrayList<>(children());
		sizeSortedElements.sort(SMALLEST_FIRST_COMPARATOR);
		return sizeSortedElements;
	}

	private List<Element> getLargestFirstChildren() {
		List<Element> sizeSortedElements = new ArrayList<>(children());
		sizeSortedElements.sort(LARGEST_FIRST_COMPARATOR);
		return sizeSortedElements;
	}

}
