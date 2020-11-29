package com.github.burgerguy.hudtweaks.gui.widget;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.github.burgerguy.hudtweaks.gui.HudElement;
import com.github.burgerguy.hudtweaks.gui.HudElement.HudElementWidget;
import com.github.burgerguy.hudtweaks.gui.HudPosHelper.Anchor;
import com.github.burgerguy.hudtweaks.gui.HudTweaksOptionsScreen;
import com.google.common.collect.Lists;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

public class SidebarWidget extends AbstractParentElement implements Drawable {
	private static final NumberFormat RELATIVE_FORMATTER = new DecimalFormat("#.000");
	@SuppressWarnings("unused")
	private static final NumberFormat OFFSET_FORMATTER = new DecimalFormat("####");
	
	private final List<AbstractButtonWidget> buttons = Lists.newArrayList();
	private final HudTweaksOptionsScreen optionsScreen;
	public int width;
	public int color;
	
	private final CustomSliderWidget xRelativeSlider;
	private final CustomSliderWidget yRelativeSlider;
	private final AnchorButtonWidget xAnchorButton;
	private final AnchorButtonWidget yAnchorButton;

	
	public SidebarWidget(HudTweaksOptionsScreen optionsScreen, int width, int color) {
		this.optionsScreen = optionsScreen;
		this.width = width;
		this.color = color;
		
		this.xRelativeSlider = new CustomSliderWidget(4, 70, this.width - 8, 14, 0.0) {
			@Override
			protected void updateMessage() {
				this.setMessage(new TranslatableText("hudtweaks.options.relative_pos.display", RELATIVE_FORMATTER.format(this.value)));
			}
			
			@Override
			public void applyValue() {
				optionsScreen.getFocusedHudElement().getParent().getXPosHelper().setRelativePos(value);
			}
			
			@Override
			public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
				boolean bl = keyCode == 263;
				if (bl || keyCode == 262) {
					this.setValue(this.value + (bl ? -0.001 : 0.001));
					return true;
				}
				return false;
			}
		};
		
		this.yRelativeSlider = new CustomSliderWidget(4, 110, this.width - 8, 14, 0.0) {
			@Override
			protected void updateMessage() {
				this.setMessage(new TranslatableText("hudtweaks.options.relative_pos.display", RELATIVE_FORMATTER.format(this.value)));
			}
			
			@Override
			public void applyValue() {
				optionsScreen.getFocusedHudElement().getParent().getYPosHelper().setRelativePos(value);
			}
			
			@Override
			public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
				boolean bl = keyCode == 263;
				if (bl || keyCode == 262) {
					this.setValue(this.value + (bl ? -0.001 : 0.001));
					return true;
				}
				return false;
			}
		};
		
		this.xAnchorButton = new AnchorButtonWidget(80, 50, true, a -> {
			optionsScreen.getFocusedHudElement().getParent().getXPosHelper().setAnchor(a);
			xRelativeSlider.active = !a.equals(Anchor.DEFAULT);
		});
		
		this.yAnchorButton = new AnchorButtonWidget(80, 90, false, a -> {
			optionsScreen.getFocusedHudElement().getParent().getYPosHelper().setAnchor(a);
			yRelativeSlider.active = !a.equals(Anchor.DEFAULT);
		});
		
		this.addButton(xRelativeSlider);
		this.addButton(yRelativeSlider);
		this.addButton(xAnchorButton);
		this.addButton(yAnchorButton);
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
		Anchor xAnchor = focusedElement.getXPosHelper().getAnchor();
		Anchor yAnchor = focusedElement.getYPosHelper().getAnchor();
		xAnchorButton.setAnchor(xAnchor);
		yAnchorButton.setAnchor(yAnchor);
		xRelativeSlider.setValue(focusedElement.getXPosHelper().getRelativePos());
		yRelativeSlider.setValue(focusedElement.getYPosHelper().getRelativePos());
		xRelativeSlider.active = !xAnchor.equals(Anchor.DEFAULT);
		yRelativeSlider.active = !yAnchor.equals(Anchor.DEFAULT);
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
		DrawableHelper.fill(matrixStack, 0, 0, width, optionsScreen.height, color);
		
		@SuppressWarnings("resource")
		TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
		HudElementWidget focusedHudElement = optionsScreen.getFocusedHudElement();
		if (focusedHudElement != null) {
			textRenderer.drawWithShadow(matrixStack, I18n.translate("hudtweaks.options.anchor_type.display"), 8, 50 + 4, 0xCCFFFFFF);
			xAnchorButton.render(matrixStack, mouseX, mouseY, delta);
			textRenderer.drawWithShadow(matrixStack, I18n.translate("hudtweaks.options.anchor_type.display"), 8, 90 + 4, 0xCCFFFFFF);
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
				this.setFocused(null);
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
