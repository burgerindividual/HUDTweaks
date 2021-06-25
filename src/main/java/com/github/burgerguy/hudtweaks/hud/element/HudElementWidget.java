package com.github.burgerguy.hudtweaks.hud.element;

import com.github.burgerguy.hudtweaks.hud.element.HudElement.PosType;
import com.github.burgerguy.hudtweaks.hud.tree.AbstractContainerNode;
import com.github.burgerguy.hudtweaks.util.Util;
import com.github.burgerguy.hudtweaks.util.gl.GLUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public class HudElementWidget implements Drawable, Element, Selectable, AutoCloseable, Comparable<HudElementWidget> {
	private static final int OUTLINE_COLOR_NORMAL = 0xFFFF0000;
	private static final int OUTLINE_COLOR_SELECTED = 0xFF0000FF;

	private final HudElementContainer elementContainer;
	private final Runnable valueUpdater;

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
			double radians = Math.toRadians(element.getRotationDegrees());
			float rotateAnchorX = element.getX() + (element.getXRotationAnchor() * element.getWidth());
			float rotateAnchorY = element.getY() + (element.getYRotationAnchor() * element.getHeight());
			Vec2f p1 = Util.rotatePoint(element.getX() - 0.5f, element.getY() - 0.5f, rotateAnchorX, rotateAnchorY, radians);
			Vec2f p2 = Util.rotatePoint(element.getX() + element.getWidth() + 0.5f, element.getY() - 0.5f, rotateAnchorX, rotateAnchorY, radians);
			Vec2f p3 = Util.rotatePoint(element.getX() + element.getWidth() + 0.5f, element.getY() + element.getHeight() + 0.5f, rotateAnchorX, rotateAnchorY, radians);
			Vec2f p4 = Util.rotatePoint(element.getX() - 0.5f, element.getY() + element.getHeight() + 0.5f, rotateAnchorX, rotateAnchorY, radians);

			int color = focused ? OUTLINE_COLOR_SELECTED : OUTLINE_COLOR_NORMAL;
			if (dashed) {
				GLUtil.drawDashedBoxOutline(matrixStack, p1.x, p1.y, p2.x, p2.y, p3.x, p3.y, p4.x, p4.y, color, client.getWindow().getScaleFactor());
			} else {
				GLUtil.drawBoxOutline(matrixStack, p1.x, p1.y, p2.x, p2.y, p3.x, p3.y, p4.x, p4.y, color, client.getWindow().getScaleFactor());
			}
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
				element.xRelativePos = MathHelper.clamp((float) (element.xRelativePos + deltaX / element.getXParent().getActiveElement().getWidth()), 0.0f, 1.0f);
			}
			if (!element.yPosType.equals(PosType.DEFAULT)) {
				element.yRelativePos = MathHelper.clamp((float) (element.yRelativePos + deltaY / element.getYParent().getActiveElement().getHeight()), 0.0f, 1.0f);
			}
		} else {
			element.xOffset += deltaX;
			element.yOffset += deltaY;
		}
		element.getContainerNode().setRequiresUpdate();
		if (valueUpdater != null) valueUpdater.run();
		return true;
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		if (lastElementRendered || focused || lastChildFocused) {
			HudElement element = elementContainer.getActiveElement();
			float rotateAnchorX = element.getXRotationAnchor() * element.getWidth();
			float rotateAnchorY = element.getYRotationAnchor() * element.getHeight();
			float offsetMouseX = (float) mouseX - element.getX();
			float offsetMouseY = (float) mouseY - element.getY();
			Vec2f rotatedPoint = Util.rotatePoint(offsetMouseX, offsetMouseY, rotateAnchorX, rotateAnchorY, Math.toRadians(-element.getRotationDegrees()));
			return rotatedPoint.x >= 0 && rotatedPoint.x <= element.getWidth() && rotatedPoint.y >= 0 && rotatedPoint.y <= element.getHeight();
		} else {
			return false;
		}
	}

	@Override
	public boolean changeFocus(boolean lookForward) {
		return focused = !focused;
	}

	private boolean isChildFocused() {
		for(AbstractContainerNode containerNode : elementContainer.getXChildren()) {
			if (containerNode instanceof HudElementContainer hudElementContainer) {
				HudElement element = hudElementContainer.getActiveElement();
				if (elementContainer.getWidget().isFocused() && element.getXPosType().equals(PosType.RELATIVE)) return true;
			}
		}

		for(AbstractContainerNode containerNode : elementContainer.getYChildren()) {
			if (containerNode instanceof HudElementContainer hudElementContainer) {
				HudElement element = hudElementContainer.getActiveElement();
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
		return Float.compare(
				thisElement.getWidth() * thisElement.getHeight(),
				otherElement.getWidth() * otherElement.getHeight()
				);
	}

	@Override
	public void close() {
		// disassociate from main element so this widget can be gc'd
		elementContainer.widget = null;
	}

	public Selectable.SelectionType getType() {
		return this.focused ? Selectable.SelectionType.FOCUSED : Selectable.SelectionType.NONE;
	}

	@Override
	public void appendNarrations(NarrationMessageBuilder builder) {
		// TODO: add narration
	}
}
