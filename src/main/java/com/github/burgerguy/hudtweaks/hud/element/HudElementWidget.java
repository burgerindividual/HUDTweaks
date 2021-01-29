package com.github.burgerguy.hudtweaks.hud.element;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import com.github.burgerguy.hudtweaks.hud.XAxisNode;
import com.github.burgerguy.hudtweaks.hud.YAxisNode;
import com.github.burgerguy.hudtweaks.hud.element.HudElement.PosType;
import com.github.burgerguy.hudtweaks.util.gl.DashedBoxOutline;
import com.github.burgerguy.hudtweaks.util.gl.GLUtil;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class HudElementWidget implements Drawable, Element, AutoCloseable, Comparable<HudElementWidget> {
	private static final int OUTLINE_COLOR_NORMAL = 0xFFFF0000;
	private static final int OUTLINE_COLOR_SELECTED = 0xFF0000FF;
	private static final float TICKS_PER_SHIFT = (20.0F / 4.0F);
	private static final byte PATTERN_LENGTH = 4;
	
	private final HudElement element;
	private final Runnable valueUpdater;
	private final DashedBoxOutline dashedBoxOutline = new DashedBoxOutline();
	
	private int dashPattern = 0xC;
	private float tickCounter;
	private boolean focused;
	private boolean lastChildFocused;
	private boolean lastElementRendered;
	
	protected HudElementWidget(HudElement element, @Nullable Runnable valueUpdater) {
		this.element = element;
		this.valueUpdater = valueUpdater;
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
		MinecraftClient client = MinecraftClient.getInstance();
		
		boolean draw = false;
		boolean dashed = false;
		if ((lastElementRendered = element.isRendered())) {
			draw = true;
		} else if (focused || (lastChildFocused = isChildFocused())) {
			draw = true;
			dashed = true;
		}
		
		if (draw) {
			double x1 = element.getX(client);
			double y1 = element.getY(client);
			double x2 = x1 + element.getWidth(client);
			double y2 = y1 + element.getHeight(client);
			
			int color = focused ? OUTLINE_COLOR_SELECTED : OUTLINE_COLOR_NORMAL;
			if (dashed) {
				if (focused) cyclePattern(delta, TICKS_PER_SHIFT);
				dashedBoxOutline.draw(matrixStack, color, dashPattern, PATTERN_LENGTH, x1 - .5, y1 - .5, x2 + .5, y2 + .5, (float) client.getWindow().getScaleFactor());
			} else {
				GLUtil.drawBoxOutline(matrixStack, x1 - .5, y1 - .5, x2 + .5, y2 + .5, color, (float) client.getWindow().getScaleFactor());
			}
		}
	}
	
	private void cyclePattern(float delta, float ticksPerShift) {
		tickCounter += delta;
		if (tickCounter >= ticksPerShift) {
			tickCounter = 0;
			dashPattern = ((1 << PATTERN_LENGTH) - 1) & ((dashPattern << (PATTERN_LENGTH - 1)) | (dashPattern >>> 1));
		}
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			return focused = isMouseOver(mouseX, mouseY);
		}
		return false;
	}
	
	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (Screen.hasShiftDown()) {
			MinecraftClient client = MinecraftClient.getInstance();
			
			if (!element.xPosType.equals(PosType.DEFAULT)) {
				element.xRelativePos = MathHelper.clamp(element.xRelativePos + deltaX / element.getXParent().getWidth(client), 0.0D, 1.0D);
			}
			if (!element.yPosType.equals(PosType.DEFAULT)) {
				element.yRelativePos = MathHelper.clamp(element.yRelativePos + deltaY / element.getYParent().getHeight(client), 0.0D, 1.0D);
			}
		} else {
			element.xOffset += deltaX;
			element.yOffset += deltaY;
		}
		element.setRequiresUpdate();
		if (valueUpdater != null) valueUpdater.run();
		return true;
	}
	
	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		MinecraftClient client = MinecraftClient.getInstance();
		
		if (lastElementRendered || focused || lastChildFocused) {
			double x1 = element.getX(client);
			double y1 = element.getY(client);
			double x2 = x1 + element.getWidth(client);
			double y2 = y1 + element.getHeight(client);
			return mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean changeFocus(boolean lookForward) {
		return focused = !focused;
	}
	
	private boolean isChildFocused() {
		for(XAxisNode node : element.getXChildren()) {
			if (node instanceof HudElement) {
				HudElement element = ((HudElement) node);
				if (element.getWidget().isFocused() && element.getXPosType().equals(PosType.RELATIVE)) return true;
			}
		}
		
		for(YAxisNode node : element.getYChildren()) {
			if (node instanceof HudElement) {
				HudElement element = ((HudElement) node);
				if (element.getWidget().isFocused() && element.getYPosType().equals(PosType.RELATIVE)) return true;
			}
		}
		
		return false;
	}
	
	public boolean isFocused() {
		return focused;
	}
	
	public HudElement getElement() {
		return element;
	}
	
	// This makes sure that the smallest elements get selected first if there are multiple on top of eachother.
	@Override
	public int compareTo(HudElementWidget other) {
		MinecraftClient client = MinecraftClient.getInstance();
		HudElement otherElement = other.getElement();
		return Double.compare(
				element.getWidth(client) * element.getHeight(client),
				otherElement.getWidth(client) * otherElement.getHeight(client)
				);
	}
	
	@Override
	public void close() {
		dashedBoxOutline.close();
		// disassociate from main element so this widget can be gc'd
		element.widget = null;
	}
	
}
