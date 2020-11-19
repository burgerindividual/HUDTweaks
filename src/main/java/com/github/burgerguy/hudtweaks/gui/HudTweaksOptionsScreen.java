package com.github.burgerguy.hudtweaks.gui;

import com.github.burgerguy.hudtweaks.config.ConfigHelper;

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
	public void onClose() {
		ConfigHelper.saveConfig();
		this.client.openScreen(this.prevScreen);
		isOpen = false;
	}
	
	public static boolean isOpen() {
		return isOpen;
	}
	
}
