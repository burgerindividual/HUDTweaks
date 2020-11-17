package com.github.burgerguy.hudtweaks.gui;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

public class HudTweaksOptionsScreen extends Screen {
	private static final int OUTLINE_COLOR = 0xFFFF0000;
	private static boolean isOpen = false;
	
	private final Screen prevScreen;
	
	public HudTweaksOptionsScreen(Screen prevScreen) {
		super(new TranslatableText("HUDTweaks Config"));
		this.prevScreen = prevScreen;
	}
	
	@Override
	protected void init() {
		isOpen = true;
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
		super.renderBackground(matrixStack);
		
		for (HudElement element : HudContainer.getElements()) {
			int x1 = element.getXPosHelper().calculateScreenPos(this.width);
			int y1 = element.getYPosHelper().calculateScreenPos(this.height);
			int x2 = x1 + element.getWidth();
			int y2 = y1 + element.getHeight();
			DrawableHelper.fill(matrixStack, x1 - 1, y1 - 1, x2 + 1, y1,     OUTLINE_COLOR);
			DrawableHelper.fill(matrixStack, x1 - 1, y2,     x2 + 1, y2 + 1, OUTLINE_COLOR);
			DrawableHelper.fill(matrixStack, x1 - 1, y1,     x1,     y2,     OUTLINE_COLOR);
			DrawableHelper.fill(matrixStack, x2,     y1,     x2 + 1, y2,     OUTLINE_COLOR);
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
		this.client.openScreen(this.prevScreen);
		isOpen = false;
	}
	
	public static boolean isOpen() {
		return isOpen;
	}
	
}
