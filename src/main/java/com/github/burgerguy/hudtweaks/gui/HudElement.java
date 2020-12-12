package com.github.burgerguy.hudtweaks.gui;

import org.lwjgl.glfw.GLFW;

import com.github.burgerguy.hudtweaks.gui.widget.HTLabelWidget;
import com.github.burgerguy.hudtweaks.gui.widget.HTSliderWidget;
import com.github.burgerguy.hudtweaks.gui.widget.NumberFieldWidget;
import com.github.burgerguy.hudtweaks.gui.widget.PosTypeButtonWidget;
import com.github.burgerguy.hudtweaks.gui.widget.RelativeParentButtonWidget;
import com.github.burgerguy.hudtweaks.gui.widget.SidebarWidget;
import com.github.burgerguy.hudtweaks.util.Util;
import com.github.burgerguy.hudtweaks.util.gui.HudPosHelper;
import com.github.burgerguy.hudtweaks.util.gui.HudPosHelper.PosType;
import com.github.burgerguy.hudtweaks.util.gui.MatrixCache.UpdateEvent;
import com.github.burgerguy.hudtweaks.util.gui.RelativeParentCache;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;

public abstract class HudElement {
	private transient final String identifier;
	private transient final UpdateEvent[] updateEvents;
	
	@SerializedName(value = "xPos")
	protected final HudPosHelper xPosHelper;
	@SerializedName(value = "yPos")
	protected final HudPosHelper yPosHelper;
	// TODO: add rotation and scale. the anchor points of these should be retrieved from the HudPosHelpers
	
	public HudElement(String identifier, UpdateEvent... updateEvents) {
		this.identifier = identifier;
		this.updateEvents = updateEvents;
		xPosHelper = new HudPosHelper();
		yPosHelper = new HudPosHelper();
		xPosHelper.setRelativeParent(HudContainer.getRelativeParentCache().getOrCreate(RelativeParentCache.SCREEN_IDENTIFIER, true));
		yPosHelper.setRelativeParent(HudContainer.getRelativeParentCache().getOrCreate(RelativeParentCache.SCREEN_IDENTIFIER, false));
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public HudPosHelper getXPosHelper() {
		return xPosHelper;
	}
	
	public HudPosHelper getYPosHelper() {
		return yPosHelper;
	}
	
	public abstract int getWidth(MinecraftClient client);

	public abstract int getHeight(MinecraftClient client);

	public abstract int getDefaultX(MinecraftClient client);
	
	public abstract int getDefaultY(MinecraftClient client);
	
	public boolean shouldUpdateOnEvent(UpdateEvent event) {
		for (UpdateEvent e : updateEvents) {
			if (event.equals(e)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean requiresUpdate() {
		return xPosHelper.requiresUpdate() || yPosHelper.requiresUpdate();
	}
	
	private void setUpdated() {
		yPosHelper.setUpdated();
		xPosHelper.setUpdated();
	}
	
	public Matrix4f calculateMatrix(MinecraftClient client) {
		int defaultX = getDefaultX(client);
		int defaultY = getDefaultY(client);
		int calculatedX = xPosHelper.calculateScreenPos(getWidth(client), defaultX, client);
		int calculatedY = yPosHelper.calculateScreenPos(getHeight(client), defaultY, client);
		
		Matrix4f matrix = Matrix4f.translate(calculatedX - defaultX,
				calculatedY - defaultY,
				0);
		
		setUpdated();
		return matrix;
	}
	
	/**
	 * Override if any extra options are added to the element.
	 * Make sure to call super before anything else.
	 */
	public void updateFromJson(JsonElement json) {
		JsonObject elementJson = json.getAsJsonObject();
		
		JsonObject xPosJson = elementJson.get("xPos").getAsJsonObject();
		xPosHelper.setRelativeParent(getRelativeParent(xPosJson.get("relativeTo"), true));
		xPosHelper.setPosType(Util.GSON.fromJson(xPosJson.get("posType"), HudPosHelper.PosType.class));
		xPosHelper.setAnchorPos(xPosJson.get("anchorPos").getAsDouble());
		xPosHelper.setOffset(xPosJson.get("offset").getAsDouble());
		xPosHelper.setRelativePos(xPosJson.get("relativePos").getAsDouble());
		
		JsonObject yPosJson = elementJson.get("yPos").getAsJsonObject();
		yPosHelper.setRelativeParent(getRelativeParent(yPosJson.get("relativeTo"), false));
		yPosHelper.setPosType(Util.GSON.fromJson(yPosJson.get("posType"), HudPosHelper.PosType.class));
		yPosHelper.setAnchorPos(yPosJson.get("anchorPos").getAsDouble());
		yPosHelper.setOffset(yPosJson.get("offset").getAsDouble());
		yPosHelper.setRelativePos(yPosJson.get("relativePos").getAsDouble());
	}
	
	private RelativeParent getRelativeParent(JsonElement jsonElement, boolean isX) {
		if (jsonElement != null && jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isString()) {
			String relativeParentIdentifier = jsonElement.getAsString();
			RelativeParent parent = HudContainer.getRelativeParentCache().getOrCreate(relativeParentIdentifier, isX);
			if(parent != null) {
				return parent;
			} else {
				return HudContainer.getRelativeParentCache().getOrCreate(RelativeParentCache.SCREEN_IDENTIFIER, isX);
			}
		}
		
		Util.LOGGER.warn("No relative parent assigned for element " + identifier + ", falling back to screen...");
		return HudContainer.getRelativeParentCache().getOrCreate(RelativeParentCache.SCREEN_IDENTIFIER, isX);
	}
	
	/**
	 * Override if any extra options are added to the element.
	 * Make sure to call super before anything else.
	 */
	@SuppressWarnings("resource")
	public void fillSidebar(SidebarWidget sidebar) {
		RelativeParentButtonWidget xRelativeParentButton = new RelativeParentButtonWidget(4, 35, sidebar.width - 8, 14, true, getXPosHelper().getRelativeParent(), identifier, p -> {
			getXPosHelper().setRelativeParent(p);
		});
		
		RelativeParentButtonWidget yRelativeParentButton = new RelativeParentButtonWidget(4, 143, sidebar.width - 8, 14, false, getYPosHelper().getRelativeParent(), identifier, p -> {
			getYPosHelper().setRelativeParent(p);
		});
		
		xRelativeParentButton.active = !getXPosHelper().getPosType().equals(PosType.DEFAULT);
		yRelativeParentButton.active = !getYPosHelper().getPosType().equals(PosType.DEFAULT);
		
		HTSliderWidget xRelativeSlider = new HTSliderWidget(4, 54, sidebar.width - 8, 14, getXPosHelper().getRelativePos()) {
			@Override
			protected void updateMessage() {
				setMessage(new TranslatableText("hudtweaks.options.relative_pos.display", Util.RELATIVE_POS_FORMATTER.format(value)));
			}
			
			@Override
			public void applyValue() {
				getXPosHelper().setRelativePos(value);
			}
			
			@Override
			public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
				boolean bl = keyCode == 263;
				if (bl || keyCode == 262) {
					setValue(value + (bl ? -0.001 : 0.001));
					return true;
				}
				return false;
			}

			@Override
			public void updateValue() {
				value = MathHelper.clamp(getXPosHelper().getRelativePos(), 0.0D, 1.0D);
				updateMessage();
			}
		};
		
		HTSliderWidget yRelativeSlider = new HTSliderWidget(4, 162, sidebar.width - 8, 14, getYPosHelper().getRelativePos()) {
			@Override
			protected void updateMessage() {
				setMessage(new TranslatableText("hudtweaks.options.relative_pos.display", Util.RELATIVE_POS_FORMATTER.format(value)));
			}
			
			@Override
			public void applyValue() {
				getYPosHelper().setRelativePos(value);
			}
			
			@Override
			public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
				boolean bl = keyCode == 263;
				if (bl || keyCode == 262) {
					setValue(value + (bl ? -0.001 : 0.001));
					return true;
				}
				return false;
			}

			@Override
			public void updateValue() {
				value = MathHelper.clamp(getYPosHelper().getRelativePos(), 0.0D, 1.0D);
				updateMessage();
			}
		};
		
		xRelativeSlider.active = !getXPosHelper().getPosType().equals(PosType.DEFAULT);
		yRelativeSlider.active = !getYPosHelper().getPosType().equals(PosType.DEFAULT);
		
		HTSliderWidget xAnchorSlider = new HTSliderWidget(4, 73, sidebar.width - 8, 14, getXPosHelper().getAnchorPos()) {
			@Override
			protected void updateMessage() {
				setMessage(new TranslatableText("hudtweaks.options.anchor_pos.display", Util.ANCHOR_POS_FORMATTER.format(value)));
			}
			
			@Override
			public void applyValue() {
				getXPosHelper().setAnchorPos(value);
			}
			
			@Override
			public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
				boolean bl = keyCode == 263;
				if (bl || keyCode == 262) {
					setValue(value + (bl ? -0.001 : 0.001));
					return true;
				}
				return false;
			}

			@Override
			public void updateValue() {
				value = MathHelper.clamp(getXPosHelper().getAnchorPos(), 0.0D, 1.0D);
				updateMessage();
			}
		};
		
		HTSliderWidget yAnchorSlider = new HTSliderWidget(4, 181, sidebar.width - 8, 14, getYPosHelper().getAnchorPos()) {
			@Override
			protected void updateMessage() {
				setMessage(new TranslatableText("hudtweaks.options.anchor_pos.display", Util.ANCHOR_POS_FORMATTER.format(value)));
			}
			
			@Override
			public void applyValue() {
				getYPosHelper().setAnchorPos(value);
			}
			
			@Override
			public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
				boolean bl = keyCode == 263;
				if (bl || keyCode == 262) {
					setValue(value + (bl ? -0.001 : 0.001));
					return true;
				}
				return false;
			}

			@Override
			public void updateValue() {
				value = MathHelper.clamp(getYPosHelper().getAnchorPos(), 0.0D, 1.0D);
				updateMessage();
			}
		};
		
		xAnchorSlider.active = !getXPosHelper().getPosType().equals(PosType.DEFAULT);
		yAnchorSlider.active = !getYPosHelper().getPosType().equals(PosType.DEFAULT);
		
		PosTypeButtonWidget xPosTypeButton = new PosTypeButtonWidget(4, 16, sidebar.width - 8, 14,  getXPosHelper().getPosType(), t -> {
			getXPosHelper().setPosType(t);
			xAnchorSlider.active = !t.equals(PosType.DEFAULT);
			xRelativeSlider.active = !t.equals(PosType.DEFAULT);
			xRelativeParentButton.active = !t.equals(PosType.DEFAULT);
		});
		
		PosTypeButtonWidget yPosTypeButton = new PosTypeButtonWidget(4, 124, sidebar.width - 8, 14,  getYPosHelper().getPosType(), t -> {
			getYPosHelper().setPosType(t);
			yAnchorSlider.active = !t.equals(PosType.DEFAULT);
			yRelativeSlider.active = !t.equals(PosType.DEFAULT);
			yRelativeParentButton.active = !t.equals(PosType.DEFAULT);
		});
		
		NumberFieldWidget xOffsetField = new NumberFieldWidget(MinecraftClient.getInstance().textRenderer, 43, 92, sidebar.width - 47, 14, new TranslatableText("hudtweaks.options.offset.name")) {
			@Override
			public void updateValue() {
				setText(Double.toString(getXPosHelper().getOffset()));
			}
		};
		xOffsetField.setText(Double.toString(getXPosHelper().getOffset()));
		xOffsetField.setChangedListener(s -> {
			if (s.equals("")) {
				getXPosHelper().setOffset(0.0D);
			} else {
				try {
					getXPosHelper().setOffset(Double.parseDouble(s));
				} catch(NumberFormatException ignored) {}
			}
		});
		
		NumberFieldWidget yOffsetField = new NumberFieldWidget(MinecraftClient.getInstance().textRenderer, 43, 200, sidebar.width - 47, 14, new TranslatableText("hudtweaks.options.offset.name")) {
			@Override
			public void updateValue() {
				setText(Double.toString(getYPosHelper().getOffset()));
			}
		};
		yOffsetField.setText(Double.toString(getYPosHelper().getOffset()));
		yOffsetField.setChangedListener(s -> {
			if (s.equals("")) {
				getYPosHelper().setOffset(0.0D);
			} else {
				try {
					getYPosHelper().setOffset(Double.parseDouble(s));
				} catch(NumberFormatException ignored) {}
			}
		});
		
		sidebar.addDrawable(xPosTypeButton);
		sidebar.addDrawable(xRelativeParentButton);
		sidebar.addDrawable(xRelativeSlider);
		sidebar.addDrawable(xAnchorSlider);
		sidebar.addDrawable(xOffsetField);
		sidebar.addDrawable(yPosTypeButton);
		sidebar.addDrawable(yRelativeParentButton);
		sidebar.addDrawable(yRelativeSlider);
		sidebar.addDrawable(yAnchorSlider);
		sidebar.addDrawable(yOffsetField);
		sidebar.addDrawable(new HTLabelWidget(I18n.translate("hudtweaks.options.offset.display"), 5, 95, 0xCCFFFFFF, false));
		sidebar.addDrawable(new HTLabelWidget(I18n.translate("hudtweaks.options.offset.display"), 5, 203, 0xCCFFFFFF, false));
		sidebar.addDrawable(new HTLabelWidget(I18n.translate("hudtweaks.options.x_pos.display"), 5, 5, 0xCCB0B0B0, false));
		sidebar.addDrawable(new HTLabelWidget(I18n.translate("hudtweaks.options.y_pos.display"), 5, 113, 0xCCB0B0B0, false));
	}
	
	public HudElementWidget createWidget(HTOptionsScreen optionsScreen) {
		return new HudElementWidget(optionsScreen);
	}
	
	public class HudElementWidget implements Drawable, Element {
		private static final int OUTLINE_COLOR_NORMAL = 0xFFFF0000;
		private static final int OUTLINE_COLOR_SELECTED = 0xFF0000FF;
		
		private final HTOptionsScreen optionsScreen;
		
		private HudElementWidget(HTOptionsScreen optionsScreen) {
			this.optionsScreen = optionsScreen;
		}

		@Override
		public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
			MinecraftClient client = MinecraftClient.getInstance();
			
			int elementWidth = getWidth(client);
			int elementHeight = getHeight(client);
			
			int x1 = xPosHelper.calculateScreenPos(elementWidth, getDefaultX(client), client);
			int y1 = yPosHelper.calculateScreenPos(elementHeight, getDefaultY(client), client);
			int x2 = x1 + elementWidth;
			int y2 = y1 + elementHeight;
			
			int color = optionsScreen.isHudElementFocused(this) ? OUTLINE_COLOR_SELECTED : OUTLINE_COLOR_NORMAL;
			DrawableHelper.fill(matrixStack, x1 - 1, y1 - 1, x2 + 1, y1,     color);
			DrawableHelper.fill(matrixStack, x1 - 1, y2,     x2 + 1, y2 + 1, color);
			DrawableHelper.fill(matrixStack, x1 - 1, y1,     x1,     y2,     color);
			DrawableHelper.fill(matrixStack, x2,     y1,     x2 + 1, y2,     color);
		}
		
		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				if (isMouseOver(mouseX, mouseY)) {
					return true;
				}
			}
			return false;
		}
		
		@Override
		public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
			if (Screen.hasShiftDown()) {
				if (!xPosHelper.getPosType().equals(PosType.DEFAULT)) {
					xPosHelper.setRelativePos(MathHelper.clamp(xPosHelper.getRelativePos() + deltaX / optionsScreen.width, 0.0D, 1.0D));
				}
				if (!yPosHelper.getPosType().equals(PosType.DEFAULT)) {
					yPosHelper.setRelativePos(MathHelper.clamp(yPosHelper.getRelativePos() + deltaY / optionsScreen.height, 0.0D, 1.0D));
				}
			} else {
				xPosHelper.setOffset(xPosHelper.getOffset() + deltaX);
				yPosHelper.setOffset(yPosHelper.getOffset() + deltaY);
			}
			optionsScreen.updateSidebarValues();
			return true;
		}
		
		@Override
		public boolean isMouseOver(double mouseX, double mouseY) {
			MinecraftClient client = MinecraftClient.getInstance();
			
			// i don't think we can cache these because they might change in multiplayer
			int elementWidth = getWidth(client);
			int elementHeight = getHeight(client);
			
			int x1 = xPosHelper.calculateScreenPos(elementWidth, getDefaultX(client), client);
			int y1 = yPosHelper.calculateScreenPos(elementHeight, getDefaultY(client), client);
			int x2 = x1 + elementWidth;
			int y2 = y1 + elementHeight;
			return mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2;
		}
		
		public HudElement getParent() {
			return HudElement.this;
		}
		
	}
	
}
