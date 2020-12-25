package com.github.burgerguy.hudtweaks.gui;

import com.github.burgerguy.hudtweaks.config.ConfigHelper;
import com.github.burgerguy.hudtweaks.gui.HudElement.HudElementWidget;
import com.github.burgerguy.hudtweaks.gui.widget.SidebarWidget;

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
	
	private static boolean isOpen = false;
	
	private final Screen prevScreen;
	private final SidebarWidget sidebar;
	
	private HudElementWidget focusedHudElement;
	
	public HTOptionsScreen(Screen prevScreen) {
		super(new TranslatableText("hudtweaks.options"));
		this.prevScreen = prevScreen;
		
		sidebar = new SidebarWidget(this, SIDEBAR_WIDTH, SIDEBAR_COLOR);
	}
	
	@Override
	protected void init() {
		super.init();
		
		isOpen = true;

		for (HudElement element : HudContainer.getElements()) {
			Element widget = element.createWidget(this);
			if (widget != null) {
				children.add(widget);
			}
		}
		
		// This makes sure that the smallest elements get selected first if there are multiple on top of eachother.
		// We also want normal elements to be the first to be selected.
		children.sort((e1, e2) -> {
			boolean isHudElement1 = e1 instanceof HudElementWidget;
			boolean isHudElement2 = e2 instanceof HudElementWidget;
			if (isHudElement1 && !isHudElement2) {
				return 1;
			} else if (!isHudElement1 && isHudElement2 || (!isHudElement1 || !isHudElement2)) {
				return -1;
			} else {
				HudElement he1 = ((HudElementWidget) e1).getParent();
				HudElement he2 = ((HudElementWidget) e2).getParent();
				return Double.compare(
						he1.getWidth(client) * he1.getHeight(client),
						he2.getWidth(client) * he2.getHeight(client)
						);
			}
		});
		
		// This is added last to make sure it's selected as a last resort.
		this.addChild(sidebar);
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
		super.renderBackground(matrixStack);
		
		for (Element element : children) {
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
		boolean clickedOnElement = super.mouseClicked(mouseX, mouseY, button);
		if (!clickedOnElement) {
			setFocused(null);
		}
		return clickedOnElement;
	}
	
	@Override
	public void onClose() {
		ConfigHelper.trySaveConfig();
		if (client.world == null) {
			client.openScreen(prevScreen);
		} else {
			client.openScreen(null);
		}
		isOpen = false;
	}
	
	@Override
	public void setFocused(Element focused) {
		if (focused instanceof HudElementWidget && !focused.equals(focusedHudElement)) {
			focusedHudElement = (HudElementWidget) focused;
			sidebar.clearDrawables();
			((HudElementWidget) focused).getParent().fillSidebar(sidebar);
		}
		
		if (focused == null) {
			focusedHudElement = null;
			sidebar.clearDrawables();
		}
		
		super.setFocused(focused);
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
	
	public boolean isHudElementFocused(HudElementWidget element) {// TODO: allow changing focus of elements with arrows, make tab only change focus for sidebar
		if (element == null || focusedHudElement == null) {
			return false;
		}
		return focusedHudElement.equals(element);
	}
	
	public void updateSidebarValues() {
		sidebar.updateValues();
	}
	
	public static boolean isOpen() {
		return isOpen;
	}
	
}
