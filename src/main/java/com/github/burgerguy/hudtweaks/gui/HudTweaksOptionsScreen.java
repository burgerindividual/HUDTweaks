package com.github.burgerguy.hudtweaks.gui;

import com.github.burgerguy.hudtweaks.config.ConfigHelper;
import com.github.burgerguy.hudtweaks.gui.HudElement.HudElementWidget;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

public class HudTweaksOptionsScreen extends Screen {
	private static boolean isOpen = false;
	
	private final Screen prevScreen;
	
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
		
		// this makes sure that the smallest elements go first, which should get selected first
		children.sort((e1, e2) -> {
			boolean isHudElement1 = e1 instanceof HudElementWidget;
			boolean isHudElement2 = e2 instanceof HudElementWidget;
			if (isHudElement1 && !isHudElement2) {
				return -1;
			} else if (!isHudElement1 && isHudElement2) {
				return 1;
			} else if (isHudElement1 && isHudElement2) {
				HudElement he1 = ((HudElementWidget) e1).getParent();
				HudElement he2 = ((HudElementWidget) e2).getParent();
				return Integer.compare(he1.getWidth() * he1.getHeight(), he2.getWidth() * he2.getHeight());
			} else {
				return 1;
			}
		});
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
		super.renderBackground(matrixStack);
		
		for (Element element : this.children) {
			if (element instanceof Drawable) {
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
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		boolean releasedOnElement = super.mouseReleased(mouseX, mouseY, button);
		if (!releasedOnElement) {
			this.setFocused(null);
		}
		return releasedOnElement;
	}
	
	@Override
	public void onClose() {
		ConfigHelper.saveConfig();
		this.client.openScreen(this.prevScreen);
		isOpen = false;
	}
	
	public boolean isFocused(Element element) {
		if (element == null || this.getFocused() == null) return false;
		return this.getFocused().equals(element);
	}
	
	public static boolean isOpen() {
		return isOpen;
	}
	
}
