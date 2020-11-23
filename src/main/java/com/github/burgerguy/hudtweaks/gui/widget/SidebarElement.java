package com.github.burgerguy.hudtweaks.gui.widget;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.github.burgerguy.hudtweaks.gui.HudElement;
import com.github.burgerguy.hudtweaks.gui.HudElement.HudElementWidget;
import com.github.burgerguy.hudtweaks.gui.HudPosHelper;
import com.github.burgerguy.hudtweaks.gui.HudPosHelper.Anchor;
import com.github.burgerguy.hudtweaks.gui.HudTweaksOptionsScreen;
import com.google.common.collect.Lists;

import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class SidebarElement extends AbstractParentElement implements Drawable {
	private static final NumberFormat RELATIVE_FORMATTER = new DecimalFormat("#.000");
	private static final NumberFormat OFFSET_FORMATTER = new DecimalFormat("####");
	
	private final List<AbstractButtonWidget> buttons = Lists.newArrayList();
	private final HudTweaksOptionsScreen optionsScreen;
	public int width;
	public int color;
	
	private final AnchorButtonWidget xAnchorButton;
	private final AnchorButtonWidget yAnchorButton;
	private final CustomSliderWidget xRelativeSlider;
	private final CustomSliderWidget yRelativeSlider;

	
	public SidebarElement(HudTweaksOptionsScreen optionsScreen, int width, int color) {
		this.optionsScreen = optionsScreen;
		this.width = width;
		this.color = color;
		
		this.xAnchorButton = new AnchorButtonWidget(80, 50, true, a -> {
			HudElementWidget focusedWidget = optionsScreen.getFocusedHudElement();
			if (focusedWidget != null) {
				focusedWidget.getParent().getXPosHelper().setAnchor(a);
			}
		});
		
		this.yAnchorButton = new AnchorButtonWidget(80, 90, false, a -> {
			HudElementWidget focusedWidget = optionsScreen.getFocusedHudElement();
			if (focusedWidget != null) {
				focusedWidget.getParent().getYPosHelper().setAnchor(a);
			}
		});
		
		this.xRelativeSlider = new CustomSliderWidget(4, 70, this.width - 8, 14, 0.0) {
			@Override
			protected void updateMessage() {
				this.setMessage(new LiteralText("Relative Pos: " + RELATIVE_FORMATTER.format(this.value)));
			}
			
			@Override
			public void applyValue() {
				HudElementWidget focusedWidget = optionsScreen.getFocusedHudElement();
				if (focusedWidget != null) {
					focusedWidget.getParent().getXPosHelper().setRelativePos(value);
				}
			}
		};
		
		this.yRelativeSlider = new CustomSliderWidget(4, 110, this.width - 8, 14, 0.0) {
			@Override
			protected void updateMessage() {
				this.setMessage(new LiteralText("Relative Pos: " + RELATIVE_FORMATTER.format(this.value)));
			}
			
			@Override
			public void applyValue() {
				HudElementWidget focusedWidget = optionsScreen.getFocusedHudElement();
				if (focusedWidget != null) {
					focusedWidget.getParent().getYPosHelper().setRelativePos(value);
				}
			}
		};
		
		this.addButton(xAnchorButton);
		this.addButton(yAnchorButton);
		this.addButton(xRelativeSlider);
		this.addButton(yRelativeSlider);
	}

	@Override
	public List<? extends Element> children() {
		return optionsScreen.getFocusedHudElement() != null ? buttons : Collections.emptyList();
	}
	
	public void addButton(AbstractButtonWidget button) {
		this.buttons.add(button);
	}
	
	public void updateValues() {
		HudElement focusedElement = optionsScreen.getFocusedHudElement().getParent();
		xAnchorButton.setAnchor(focusedElement.getXPosHelper().getAnchor());
		yAnchorButton.setAnchor(focusedElement.getYPosHelper().getAnchor());
		xRelativeSlider.setValue(focusedElement.getXPosHelper().getRelativePos());
		yRelativeSlider.setValue(focusedElement.getYPosHelper().getRelativePos());
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
		DrawableHelper.fill(matrixStack, 0, 0, width, optionsScreen.height, color);
		
		HudElementWidget focusedHudElement = optionsScreen.getFocusedHudElement();
		if (focusedHudElement != null) {
			xAnchorButton.render(matrixStack, mouseX, mouseY, delta);
			yAnchorButton.render(matrixStack, mouseX, mouseY, delta);
			xRelativeSlider.render(matrixStack, mouseX, mouseY, delta);
			yRelativeSlider.render(matrixStack, mouseX, mouseY, delta);
		} else {
			
		}
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		Iterator<? extends Element> iterator = this.children().iterator();
		
		Element currentElement;
		do {
			if (!iterator.hasNext()) {
				if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
					if (isMouseOver(mouseX, mouseY)) {
						return true;
					}
				}
				return false;
			}
			
			currentElement = iterator.next();
		} while (!currentElement.mouseClicked(mouseX, mouseY, button));
		
		this.setFocused(currentElement);
		if (button == 0) {
			this.setDragging(true);
		}
		
		return true;
	}
	
	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return mouseX >= 0 && mouseX <= width && mouseY >= 0 && mouseY <= optionsScreen.height;
	}
	
}
