package com.github.burgerguy.hudtweaks.hud.element;

import com.github.burgerguy.hudtweaks.hud.tree.AbstractContainerNode;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

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
	private static final float TICKS_PER_SHIFT = 20.0F / 4.0F;
	private static final byte PATTERN_LENGTH = 4;

	private final HudElementContainer elementContainer;
	private final Runnable valueUpdater;
	private final DashedBoxOutline dashedBoxOutline = new DashedBoxOutline();

	private int dashPattern = 0xC;
	private float tickCounter;
	private boolean focused;
	private boolean lastChildFocused;
	private boolean lastElementRendered;

	protected HudElementWidget(HudElementContainer elementContainer, @Nullable Runnable valueUpdater) {
		this.elementContainer = elementContainer;
		this.valueUpdater = valueUpdater;
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
		MinecraftClient client = MinecraftClient.getInstance();

		boolean draw = false;
		boolean dashed = false;
		if (lastElementRendered = elementContainer.isRendered()) {
			draw = true;
		} else if (focused || (lastChildFocused = isChildFocused())) {
			draw = true;
			dashed = true;
		}

		if (draw) {
			HudElement element = elementContainer.getActiveElement();
			double x1 = element.getX();
			double y1 = element.getY();
			double x2 = x1 + element.getWidth();
			double y2 = y1 + element.getHeight();

			int color = focused ? OUTLINE_COLOR_SELECTED : OUTLINE_COLOR_NORMAL;
			if (dashed) {
				if (focused) cyclePattern(delta);
				dashedBoxOutline.draw(matrixStack, color, dashPattern, PATTERN_LENGTH, x1 - .5, y1 - .5, x2 + .5, y2 + .5, (float) client.getWindow().getScaleFactor());
			} else {
				GLUtil.drawBoxOutline(matrixStack, x1 - .5, y1 - .5, x2 + .5, y2 + .5, color, (float) client.getWindow().getScaleFactor());
			}
		}
	}

	private void cyclePattern(float delta) {
		tickCounter += delta;
		if (tickCounter >= TICKS_PER_SHIFT) {
			tickCounter = 0;
			dashPattern = (1 << PATTERN_LENGTH) - 1 & (dashPattern << PATTERN_LENGTH - 1 | dashPattern >>> 1);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (isMouseOver(mouseX, mouseY)) {
			return focused = button == GLFW.GLFW_MOUSE_BUTTON_LEFT;
		}
		return focused = false;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		HudElement element = elementContainer.getActiveElement();
		if (Screen.hasShiftDown()) {
			if (!element.xPosType.equals(PosType.DEFAULT)) {
				element.xRelativePos = MathHelper.clamp(element.xRelativePos + deltaX / element.getXParent().getActiveElement().getWidth(), 0.0D, 1.0D);
			}
			if (!element.yPosType.equals(PosType.DEFAULT)) {
				element.yRelativePos = MathHelper.clamp(element.yRelativePos + deltaY / element.getYParent().getActiveElement().getHeight(), 0.0D, 1.0D);
			}
		} else {
			element.xOffset += deltaX;
			element.yOffset += deltaY;
		}
		element.getParentNode().setRequiresUpdate();
		if (valueUpdater != null) valueUpdater.run();
		return true;
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		if (lastElementRendered || focused || lastChildFocused) {
			HudElement element = elementContainer.getActiveElement();
			double x1 = element.getX();
			double y1 = element.getY();
			double x2 = x1 + element.getWidth();
			double y2 = y1 + element.getHeight();
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
		for(AbstractContainerNode node : elementContainer.getXChildren()) {
			if (node instanceof HudElementContainer) {
				HudElement element = node.getActiveElement();
				if (elementContainer.getWidget().isFocused() && element.getXPosType().equals(PosType.RELATIVE)) return true;
			}
		}

		for(AbstractContainerNode node : elementContainer.getYChildren()) {
			if (node instanceof HudElementContainer) {
				HudElement element = node.getActiveElement();
				if (elementContainer.getWidget().isFocused() && element.getYPosType().equals(PosType.RELATIVE)) return true;
			}
		}

		return false;
	}

	public boolean isFocused() {
		return focused;
	}

	public HudElementContainer getElementContainer() {
		return elementContainer;
	}

	// This makes sure that the smallest elements get selected first if there are multiple on top of another.
	@Override
	public int compareTo(HudElementWidget other) {
		HudElement thisElement = elementContainer.getActiveElement();
		HudElement otherElement = other.getElementContainer().getActiveElement();
		return Double.compare(
				thisElement.getWidth() * thisElement.getHeight(),
				otherElement.getWidth() * otherElement.getHeight()
				);
	}

	@Override
	public void close() {
		dashedBoxOutline.close();
		// disassociate from main element so this widget can be gc'd
		elementContainer.widget = null;
	}

}
