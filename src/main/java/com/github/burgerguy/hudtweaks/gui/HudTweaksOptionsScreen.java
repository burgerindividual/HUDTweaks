package com.github.burgerguy.hudtweaks.gui;

import com.github.burgerguy.hudtweaks.config.ConfigHelper;
import com.github.burgerguy.hudtweaks.gui.HudElement.HudElementWidget;
import com.github.burgerguy.hudtweaks.gui.widget.SidebarWidget;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

public class HudTweaksOptionsScreen extends Screen {
	private static final int SIDEBAR_WIDTH = 108;
	private static final int SIDEBAR_COLOR = 0x60424242;
	
	private static boolean isOpen = false;
	
	private final Screen prevScreen;
	private final SidebarWidget sidebar;
	
	private HudElementWidget focusedHudElement;
	
	public HudTweaksOptionsScreen(Screen prevScreen) {
		super(new TranslatableText("hudtweaks.options"));
		this.prevScreen = prevScreen;
		
		this.sidebar = new SidebarWidget(this, SIDEBAR_WIDTH, SIDEBAR_COLOR);
	}
	
	@Override
	protected void init() {
		super.init();
		
		isOpen = true;

		for (HudElement element : HudContainer.getElements()) {
			this.children.add(element.createWidget(this));
		}
		
		// This makes sure that the smallest elements get selected first if there are multiple on top of eachother.
		// We also want normal elements to be the first to be selected.
		children.sort((e1, e2) -> {
			boolean isHudElement1 = e1 instanceof HudElementWidget;
			boolean isHudElement2 = e2 instanceof HudElementWidget;
			if (isHudElement1 && !isHudElement2) {
				return 1;
			} else if (!isHudElement1 && isHudElement2) {
				return -1;
			} else if (isHudElement1 && isHudElement2) {
				HudElement he1 = ((HudElementWidget) e1).getParent();
				HudElement he2 = ((HudElementWidget) e2).getParent();
				return Integer.compare(
										he1.getWidth(this.client) * he1.getHeight(this.client),
										he2.getWidth(this.client) * he2.getHeight(this.client)
									  );
			} else {
				return -1;
			}
		});
		
		// This is added last to make sure it's selected as a last resort.
		this.addChild(sidebar);
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
		super.renderBackground(matrixStack);
		
		for (Element element : this.children) {
			if (element instanceof Drawable && !(element instanceof AbstractButtonWidget)) {
				((Drawable) element).render(matrixStack, mouseX, mouseY, delta);
			}
		}	
		
		super.render(matrixStack, mouseX, mouseY, delta);
	}
	
	@Override
	public void renderBackground(MatrixStack matrixStack, int vOffset) {
		if (this.client.world == null) {
			this.renderBackgroundTexture(vOffset);
		}
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		boolean clickedOnElement = super.mouseClicked(mouseX, mouseY, button);
		if (!clickedOnElement) {
			this.setFocused(null);
		}
		return clickedOnElement;
	}
	
	@Override
	public void onClose() {
		ConfigHelper.trySaveConfig();
		if (this.client.world == null) {
			this.client.openScreen(this.prevScreen);
		} else {
			this.client.openScreen(null);
		}
		isOpen = false;
	}
	
	@Override
	public void setFocused(Element focused) {
		if (focused instanceof HudElementWidget) {
			this.focusedHudElement = (HudElementWidget) focused;
			updateSidebarValues();
		}
		
		if (focused == null) {
			this.focusedHudElement = null;
		}
		
		super.setFocused(focused);
	}
	
	public void updateSidebarValues() {
		sidebar.updateValues();
	}
	
	public boolean isHudElementFocused(HudElementWidget element) {// TODO: allow changing focus of elements with arrows, make tab only change focus for sidebar
		if (element == null || this.focusedHudElement == null) return false;
		return this.focusedHudElement.equals(element);
	}
	
	public HudElementWidget getFocusedHudElement() {
		return this.focusedHudElement;
	}
	
	public static boolean isOpen() {
		return isOpen;
	}
	
}
