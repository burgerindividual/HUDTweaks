package com.github.burgerguy.hudtweaks.hud.element;

import com.github.burgerguy.hudtweaks.gui.widget.HTLabelWidget;
import com.github.burgerguy.hudtweaks.gui.widget.HTSliderWidget;
import com.github.burgerguy.hudtweaks.gui.widget.NumberFieldWidget;
import com.github.burgerguy.hudtweaks.gui.widget.ParentButtonWidget;
import com.github.burgerguy.hudtweaks.gui.widget.PosTypeButtonWidget;
import com.github.burgerguy.hudtweaks.gui.widget.SidebarWidget;
import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import com.github.burgerguy.hudtweaks.hud.HudContainer;
import com.github.burgerguy.hudtweaks.hud.tree.AbstractTypeNodeEntry;
import com.github.burgerguy.hudtweaks.util.Util;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;

public abstract class HudElementEntry extends AbstractTypeNodeEntry {
	// These are all marked as transient so we can manually add them in our custom serializer
	protected transient PosType xPosType = PosType.DEFAULT;
	protected transient PosType yPosType = PosType.DEFAULT;
	protected transient double xAnchorPos;
	protected transient double yAnchorPos;
	protected transient double xRelativePos;
	protected transient double yRelativePos;
	protected transient double xOffset;
	protected transient double yOffset;
	protected transient double xScale = 1.0D;
	protected transient double yScale = 1.0D;
	
	protected transient double cachedWidth;
	protected transient double cachedHeight;
	protected transient double cachedDefaultX;
	protected transient double cachedDefaultY;
	protected transient double cachedX;
	protected transient double cachedY;
	// TODO: add rotation using the already existing anchor points.
	
	public HudElementEntry(HTIdentifier identifier, String... updateEvents) {
		super(identifier, updateEvents);
	}
	
	public enum PosType {
		@SerializedName(value = "default", alternate = "DEFAULT")
		/**
		 * Keeps the position in the unmodified spot, but allows for offset.
		 */
		DEFAULT,
		
		@SerializedName(value = "relative", alternate = "RELATIVE")
		/**
		 * Allows positioning anywhere relative to a bound element with a
		 * relative pos and offset. The bound element can also be the screen.
		 */
		RELATIVE
	}
	
	protected abstract double calculateWidth(MinecraftClient client);
	
	protected abstract double calculateHeight(MinecraftClient client);
	
	protected abstract double calculateDefaultX(MinecraftClient client);
	
	protected abstract double calculateDefaultY(MinecraftClient client);
	
	@Override
	public double getWidth(MinecraftClient client) {
		return cachedWidth;
	}

	@Override
	public double getHeight(MinecraftClient client) {
		return cachedHeight;
	}

	public double getDefaultX(MinecraftClient client) {
		return cachedDefaultX;
	}
	
	public double getDefaultY(MinecraftClient client) {
		return cachedDefaultY;
	}
	
	@Override
	public double getX(MinecraftClient client) {
		return cachedX;
	}
	
	@Override
	public double getY(MinecraftClient client) {
		return cachedY;
	}
	
	@Override
	public void updateSelfX(MinecraftClient client) {
		cachedWidth = calculateWidth(client) * xScale;
		cachedDefaultX = calculateDefaultX(client);
		switch(xPosType) {
		case DEFAULT:
			cachedX = getDefaultX(client) + xOffset;
			break;
		case RELATIVE:
			cachedX = (getXParent().getActiveEntry().getWidth(client) * xRelativePos + xOffset + getXParent().getActiveEntry().getX(client)) - (getWidth(client) * xAnchorPos);
			break;
		default:
			throw new UnsupportedOperationException("how");
		}
	}
	
	@Override
	public void updateSelfY(MinecraftClient client) {
		cachedHeight = calculateHeight(client) * yScale;
		cachedDefaultY = calculateDefaultY(client);
		switch(yPosType) {
		case DEFAULT:
			cachedY = getDefaultY(client) + yOffset;
			break;
		case RELATIVE:
			cachedY = (getYParent().getActiveEntry().getHeight(client) * yRelativePos + yOffset + getYParent().getActiveEntry().getY(client)) - (getHeight(client) * yAnchorPos);
			break;
		default:
			throw new UnsupportedOperationException("how");
		}
	}
	
	public Matrix4f createMatrix(MinecraftClient client) {
		Matrix4f matrix = Matrix4f.scale((float) xScale, (float) yScale, 1);
		matrix.multiply(Matrix4f.translate((float) ((getX(client) * (1 / xScale)) - getDefaultX(client)),
				(float) ((getY(client) * (1 / yScale)) - getDefaultY(client)), 1));
		parentNode.setUpdated();
		return matrix;
	}
	
	public PosType getXPosType() {
		return xPosType;
	}
	
	public PosType getYPosType() {
		return yPosType;
	}
	
	public double getXAnchorPos() {
		return xAnchorPos;
	}
	
	public double getYAnchorPos() {
		return yAnchorPos;
	}
	
	public double getXRelativePos() {
		return xRelativePos;
	}
	
	public double getYRelativePos() {
		return yRelativePos;
	}
	
	public double getXOffset() {
		return xOffset;
	}
	
	public double getYOffset() {
		return yOffset;
	}
	
	public double getXScale() {
		return xScale;
	}
	
	public double getYScale() {
		return yScale;
	}
	
	public String toString() {
		return getIdentifier().toString();
	}
	
	/**
	 * Override if any extra options are added to the element.
	 * Make sure to call super before anything else.
	 */
	public void updateFromJson(JsonElement json) {
		JsonObject elementJson = json.getAsJsonObject();
		
		JsonObject xPosJson = elementJson.get("xPos").getAsJsonObject();
		JsonElement parentIdentifier = xPosJson.get("parent");
		if (parentIdentifier != null && parentIdentifier.isJsonPrimitive() && parentIdentifier.getAsJsonPrimitive().isString()) {
			String relativeParentIdentifier = parentIdentifier.getAsString();
			HudElementType parentNode = HudContainer.getElementRegistry().getElementType(new HTIdentifier.ElementType(relativeParentIdentifier, null));
			if(parentNode != null) {
				moveXUnder(parentNode);
			}
		}
		xPosType = Util.GSON.fromJson(xPosJson.get("posType"), PosType.class);
		xAnchorPos = xPosJson.get("anchorPos").getAsDouble();
		xOffset = xPosJson.get("offset").getAsDouble();
		xRelativePos = xPosJson.get("relativePos").getAsDouble();
		
		JsonObject yPosJson = elementJson.get("yPos").getAsJsonObject();
		parentIdentifier = yPosJson.get("parent");
		if (parentIdentifier != null && parentIdentifier.isJsonPrimitive() && parentIdentifier.getAsJsonPrimitive().isString()) {
			String relativeParentIdentifier = parentIdentifier.getAsString();
			HudElementType parentNode = HudContainer.getElementRegistry().getElementType(new HTIdentifier.ElementType(relativeParentIdentifier, null));
			if(parentNode != null) {
				moveYUnder(parentNode);
			}
		}
		yPosType = Util.GSON.fromJson(yPosJson.get("posType"), PosType.class);
		yAnchorPos = yPosJson.get("anchorPos").getAsDouble();
		yOffset = yPosJson.get("offset").getAsDouble();
		yRelativePos = yPosJson.get("relativePos").getAsDouble();
		
		xScale = elementJson.get("xScale").getAsDouble();
		yScale = elementJson.get("yScale").getAsDouble();
	}
	
	/**
	 * Override if any extra options are added to the element.
	 * Make sure to call super before anything else.
	 */
	@SuppressWarnings("resource")
	public void fillSidebar(SidebarWidget sidebar) {
		ParentButtonWidget xRelativeParentButton = new ParentButtonWidget(4, 35, sidebar.width - 8, 14, getXParent(), getParentNode(), p -> moveXUnder(p), true);
		
		ParentButtonWidget yRelativeParentButton = new ParentButtonWidget(4, 143, sidebar.width - 8, 14, getYParent(), getParentNode(), p -> moveYUnder(p), false);
		
		xRelativeParentButton.active = !xPosType.equals(PosType.DEFAULT);
		yRelativeParentButton.active = !yPosType.equals(PosType.DEFAULT);
		
		HTSliderWidget xRelativeSlider = new HTSliderWidget(4, 54, sidebar.width - 8, 14, xRelativePos) {
			@Override
			protected void updateMessage() {
				setMessage(new TranslatableText("hudtweaks.options.relative_pos.display", Util.RELATIVE_POS_FORMATTER.format(value)));
			}
			
			@Override
			public void applyValue() {
				xRelativePos = value;
				parentNode.setRequiresUpdate();
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
				value = MathHelper.clamp(xRelativePos, 0.0D, 1.0D);
				updateMessage();
			}
		};
		
		HTSliderWidget yRelativeSlider = new HTSliderWidget(4, 162, sidebar.width - 8, 14, yRelativePos) {
			@Override
			protected void updateMessage() {
				setMessage(new TranslatableText("hudtweaks.options.relative_pos.display", Util.RELATIVE_POS_FORMATTER.format(value)));
			}
			
			@Override
			public void applyValue() {
				yRelativePos = value;
				parentNode.setRequiresUpdate();
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
				value = MathHelper.clamp(yRelativePos, 0.0D, 1.0D);
				updateMessage();
			}
		};
		
		xRelativeSlider.active = !xPosType.equals(PosType.DEFAULT);
		yRelativeSlider.active = !yPosType.equals(PosType.DEFAULT);
		
		HTSliderWidget xAnchorSlider = new HTSliderWidget(4, 73, sidebar.width - 8, 14, xAnchorPos) {
			@Override
			protected void updateMessage() {
				setMessage(new TranslatableText("hudtweaks.options.anchor_pos.display", Util.ANCHOR_POS_FORMATTER.format(value)));
			}
			
			@Override
			public void applyValue() {
				xAnchorPos = value;
				parentNode.setRequiresUpdate();
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
				value = MathHelper.clamp(xAnchorPos, 0.0D, 1.0D);
				updateMessage();
			}
		};
		
		HTSliderWidget yAnchorSlider = new HTSliderWidget(4, 181, sidebar.width - 8, 14, yAnchorPos) {
			@Override
			protected void updateMessage() {
				setMessage(new TranslatableText("hudtweaks.options.anchor_pos.display", Util.ANCHOR_POS_FORMATTER.format(value)));
			}
			
			@Override
			public void applyValue() {
				yAnchorPos = value;
				parentNode.setRequiresUpdate();
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
				value = MathHelper.clamp(yAnchorPos, 0.0D, 1.0D);
				updateMessage();
			}
		};
		
		xAnchorSlider.active = !xPosType.equals(PosType.DEFAULT);
		yAnchorSlider.active = !yPosType.equals(PosType.DEFAULT);
		
		PosTypeButtonWidget xPosTypeButton = new PosTypeButtonWidget(4, 16, sidebar.width - 8, 14,  xPosType, t -> {
			xPosType = t;
			parentNode.setRequiresUpdate();
			xAnchorSlider.active = !t.equals(PosType.DEFAULT);
			xRelativeSlider.active = !t.equals(PosType.DEFAULT);
			xRelativeParentButton.active = !t.equals(PosType.DEFAULT);
		});
		
		PosTypeButtonWidget yPosTypeButton = new PosTypeButtonWidget(4, 124, sidebar.width - 8, 14,  yPosType, t -> {
			yPosType = t;
			parentNode.setRequiresUpdate();
			yAnchorSlider.active = !t.equals(PosType.DEFAULT);
			yRelativeSlider.active = !t.equals(PosType.DEFAULT);
			yRelativeParentButton.active = !t.equals(PosType.DEFAULT);
		});
		
		NumberFieldWidget xOffsetField = new NumberFieldWidget(MinecraftClient.getInstance().textRenderer, 43, 92, sidebar.width - 47, 14, new TranslatableText("hudtweaks.options.offset.name")) {
			@Override
			public void updateValue() {
				setText(Double.toString(xOffset));
			}
		};
		xOffsetField.setText(Double.toString(xOffset));
		xOffsetField.setChangedListener(s -> {
			if (s.equals("")) {
				xOffset = 0.0D;
				parentNode.setRequiresUpdate();
			} else {
				try {
					xOffset = Double.parseDouble(s);
					parentNode.setRequiresUpdate();
				} catch(NumberFormatException ignored) {}
			}
		});
		
		NumberFieldWidget yOffsetField = new NumberFieldWidget(MinecraftClient.getInstance().textRenderer, 43, 200, sidebar.width - 47, 14, new TranslatableText("hudtweaks.options.offset.name")) {
			@Override
			public void updateValue() {
				setText(Util.NUM_FIELD_FORMATTER.format(yOffset));
			}
		};
		yOffsetField.setText(Util.NUM_FIELD_FORMATTER.format(yOffset));
		yOffsetField.setChangedListener(s -> {
			if (s.equals("")) {
				yOffset = 0.0D;
				parentNode.setRequiresUpdate();
			} else {
				try {
					yOffset = Double.parseDouble(s);
					parentNode.setRequiresUpdate();
				} catch(NumberFormatException ignored) {}
			}
		});
		
		
		NumberFieldWidget xScaleField = new NumberFieldWidget(MinecraftClient.getInstance().textRenderer, 48, 232, sidebar.width - 52, 14, new TranslatableText("hudtweaks.options.x_scale.name")) {
			@Override
			public void updateValue() {
				setText(Util.NUM_FIELD_FORMATTER.format(xScale));
			}
		};
		xScaleField.setText(Util.NUM_FIELD_FORMATTER.format(xScale));
		xScaleField.setChangedListener(s -> {
			if (s.equals("")) {
				xScale = 0.0D;
				parentNode.setRequiresUpdate();
			} else {
				try {
					double value = Double.parseDouble(s);
					double lastValue = xScale;
					xScale = value < 0.0D ? 0.0D : value;
					if (xScale != lastValue) parentNode.setRequiresUpdate();
				} catch(NumberFormatException ignored) {}
			}
		});
		
		NumberFieldWidget yScaleField = new NumberFieldWidget(MinecraftClient.getInstance().textRenderer, 48, 251, sidebar.width - 52, 14, new TranslatableText("hudtweaks.options.y_scale.name")) {
			@Override
			public void updateValue() {
				setText(Util.NUM_FIELD_FORMATTER.format(yScale));
			}
		};
		yScaleField.setText(Util.NUM_FIELD_FORMATTER.format(yScale));
		yScaleField.setChangedListener(s -> {
			if (s.equals("")) {
				yScale = 0.0D;
				parentNode.setRequiresUpdate();
			} else {
				try {
					double value = Double.parseDouble(s);
					double lastValue = yScale;
					yScale = value < 0.0D ? 0.0D : value;
					if (yScale != lastValue) parentNode.setRequiresUpdate();
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
		sidebar.addDrawable(xScaleField);
		sidebar.addDrawable(yScaleField);
		sidebar.addDrawable(new HTLabelWidget(I18n.translate("hudtweaks.options.offset.display"), 5, 95, 0xCCFFFFFF, false));
		sidebar.addDrawable(new HTLabelWidget(I18n.translate("hudtweaks.options.offset.display"), 5, 203, 0xCCFFFFFF, false));
		sidebar.addDrawable(new HTLabelWidget(I18n.translate("hudtweaks.options.x_pos.display"), 5, 5, 0xCCB0B0B0, false));
		sidebar.addDrawable(new HTLabelWidget(I18n.translate("hudtweaks.options.y_pos.display"), 5, 113, 0xCCB0B0B0, false));
		sidebar.addDrawable(new HTLabelWidget(I18n.translate("hudtweaks.options.scale.display"), 5, 221, 0xCCB0B0B0, false));
		sidebar.addDrawable(new HTLabelWidget(I18n.translate("hudtweaks.options.x_scale.display"), 5, 236, 0xCCFFFFFF, false));
		sidebar.addDrawable(new HTLabelWidget(I18n.translate("hudtweaks.options.y_scale.display"), 5, 254, 0xCCFFFFFF, false));
	}
	
	/**
	 * Override this when fillSidebar is overridden. It is recommended to
	 * call super and then add to that value, rather than completely
	 * overriding the value.
	 * 
	 * @return the height of all of the rendered items in fillSidebar
	 */
	public int getSidebarOptionsHeight() {
		return 265;
	}
	
}
