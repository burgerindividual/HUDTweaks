package com.github.burgerguy.hudtweaks.gui;

import com.github.burgerguy.hudtweaks.config.ConfigHelper;
import com.github.burgerguy.hudtweaks.gui.HudElement.HudElementWidget;
import com.github.burgerguy.hudtweaks.gui.widget.SidebarElement;
import com.github.burgerguy.hudtweaks.gui.widget.TransparentSliderWidget;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class HudTweaksOptionsScreen extends Screen {
	private static final int SIDEBAR_WIDTH = 108;
	private static final int SIDEBAR_COLOR = 0x60424242;
	
	private static boolean isOpen = false;
	
	private final Screen prevScreen;
	
	private HudElementWidget focusedHudElement;
	
	public HudTweaksOptionsScreen(Screen prevScreen) {
		super(new TranslatableText("HUDTweaks Config"));
		this.prevScreen = prevScreen;
	}
	
	@Override
	protected void init() {
		super.init();
		
		isOpen = true;
		for (HudElement element : HudContainer.getElements()) {
			this.children.add(element.createWidget(this));
		}
		
		SidebarElement sidebar = new SidebarElement(this, SIDEBAR_WIDTH, SIDEBAR_COLOR);
		this.addChild(sidebar);
		
		TransparentSliderWidget slider = new TransparentSliderWidget(4, 20, SIDEBAR_WIDTH - 8, 14, 0.0) {
			@Override
			protected void updateMessage() {
//				Element focused = HudTweaksOptionsScreen.this.getFocused();
//				if (focused != null) {
//					if (focused instanceof HudElementWidget) {
//						this.setMessage(new LiteralText(((HudElementWidget) focused).getParent().getIdentifier()));
//					}
//				}
				this.setMessage(new LiteralText(Double.toString(this.value)));
			}

			@Override
			protected void applyValue() {
				//this.setAlpha((float) this.value);
			}
		};
		
		sidebar.addButton(slider);
		
		// This makes sure that the smallest elements get selected first if there are multiple on top of eachother
		// We also want normal elements to be the first to be selected
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
				return Integer.compare(he1.getWidth() * he1.getHeight(), he2.getWidth() * he2.getHeight());
			} else {
				return -1;
			}
		});
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
		ConfigHelper.saveConfig();
		this.client.openScreen(this.prevScreen);
		isOpen = false;
	}
	
	@Override
	public void setFocused(Element focused) {
		if (focused instanceof HudElementWidget || focused == null) {
			this.focusedHudElement = (HudElementWidget) focused;
		}
		super.setFocused(focused);
	}
	
	public boolean isHudElementFocused(HudElementWidget element) {// TODO: allow changing focus of elements with arrows, make tab only change focus for sidebar
		if (element == null || this.focusedHudElement == null) return false;
		return this.focusedHudElement.equals(element);
	}
	
	public static boolean isOpen() {
		return isOpen;
	}
	
}
