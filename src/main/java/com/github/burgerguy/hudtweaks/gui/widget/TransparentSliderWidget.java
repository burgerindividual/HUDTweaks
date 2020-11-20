package com.github.burgerguy.hudtweaks.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.MathHelper;

public abstract class TransparentSliderWidget extends SliderWidget {
	private static final int HANDLE_WIDTH = 7;

	public TransparentSliderWidget(int x, int y, int width, int height, double value) {
		super(x, y, width, height, LiteralText.EMPTY, value);
		this.setAlpha(0.8F);
	}
	
	@Override
	public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		TextRenderer textRenderer = minecraftClient.textRenderer;
		int x1 = this.x;
		int y1 = this.y;
		int x2 = this.x + this.width;
		int y2 = this.y + this.height;
		DrawableHelper.fill(matrixStack, x1,     y1,     x2,     y1 + 1, 0xFF000000);
		DrawableHelper.fill(matrixStack, x1,     y2,     x2,     y2 - 1, 0xFF000000);
		DrawableHelper.fill(matrixStack, x1,     y1 + 1, x1 + 1, y2 - 1, 0xFF000000);
		DrawableHelper.fill(matrixStack, x2,     y1 + 1, x2 - 1, y2 - 1, 0xFF000000);
		this.renderBg(matrixStack, minecraftClient, mouseX, mouseY);
		int j = this.active ? 0x00FFFFFF : 0x00A0A0A0;
		drawCenteredText(matrixStack, textRenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
	}
	
	@Override
	protected void renderBg(MatrixStack matrixStack, MinecraftClient client, int mouseX, int mouseY) {
		int x1 = this.x + (int) (this.value * (double) (this.width - HANDLE_WIDTH));
		int y1 = this.y;
		int x2 = x1 + HANDLE_WIDTH;
		int y2 = y1 + this.height;
		int color = this.isHovered() ? 0xFFFFFFFF : 0xFF000000;
		DrawableHelper.fill(matrixStack, x1,     y1,     x2,     y1 + 1, color);
		DrawableHelper.fill(matrixStack, x1,     y2,     x2,     y2 - 1, color);
		DrawableHelper.fill(matrixStack, x1,     y1 + 1, x1 + 1, y2 - 1, color);
		DrawableHelper.fill(matrixStack, x2,     y1 + 1, x2 - 1, y2 - 1, color);
		DrawableHelper.fill(matrixStack, x1 + 1, y1 + 1, x2 - 1, y2 - 1, 0x7F9F9F9F);
	}
	
	@Override
	public void onClick(double mouseX, double mouseY) {
		this.setValueFromMouse(mouseX);
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		boolean bl = keyCode == 263;
		if (bl || keyCode == 262) {
			double f = bl ? -1.0 : 1.0;
			this.setValue(this.value + f / (this.width - HANDLE_WIDTH));
		}
		
		return false;
	}
	
	private void setValueFromMouse(double mouseX) {
		this.setValue((mouseX - (double) (this.x + (HANDLE_WIDTH / 2.0D - 1.0D))) / (double) (this.width - HANDLE_WIDTH));
	}
	
	private void setValue(double mouseX) {
		double oldValue = this.value;
		this.value = MathHelper.clamp(mouseX, 0.0D, 1.0D);
		if (oldValue != this.value) {
			this.applyValue();
		}
		
		this.updateMessage();
	}
	
	@Override
	protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
		this.setValueFromMouse(mouseX);
	}
}
