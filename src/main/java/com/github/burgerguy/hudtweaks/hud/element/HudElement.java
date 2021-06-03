package com.github.burgerguy.hudtweaks.hud.element;

import com.github.burgerguy.hudtweaks.gui.widget.*;
import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import com.github.burgerguy.hudtweaks.hud.HudContainer;
import com.github.burgerguy.hudtweaks.hud.tree.AbstractElementNode;
import com.github.burgerguy.hudtweaks.util.Util;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;

public abstract class HudElement extends AbstractElementNode {
	// These are all marked as transient so we can manually add them in our custom serializer
	protected transient PosType xPosType = PosType.DEFAULT;
	protected transient PosType yPosType = PosType.DEFAULT;
	protected transient float xAnchorPos;
	protected transient float yAnchorPos;
	protected transient float xRelativePos;
	protected transient float yRelativePos;
	protected transient float xOffset;
	protected transient float yOffset;
	protected transient float xScale = 1.0f;
	protected transient float yScale = 1.0f;
	protected transient float xRotationAnchor;
	protected transient float yRotationAnchor;
	protected transient float rotationDegrees = 1;

	// These are marked transient because we don't want them serialized at all
	protected transient float cachedWidth;
	protected transient float cachedHeight;
	protected transient float cachedDefaultX;
	protected transient float cachedDefaultY;
	protected transient float cachedX;
	protected transient float cachedY;

	public HudElement(HTIdentifier identifier, String... updateEvents) {
		super(identifier, updateEvents);
	}

	public enum PosType {
		/**
		 Keeps the position in the unmodified spot, but allows for offset.
		 */
		@SerializedName(value = "default", alternate = "DEFAULT")
		DEFAULT,

		/**
		 * Allows positioning anywhere relative to a bound element with a
		 * relative pos and offset. The bound element can also be the screen.
		 */
		@SerializedName(value = "relative", alternate = "RELATIVE")
		RELATIVE
	}

	protected abstract float calculateWidth(MinecraftClient client);

	protected abstract float calculateHeight(MinecraftClient client);

	protected abstract float calculateDefaultX(MinecraftClient client);

	protected abstract float calculateDefaultY(MinecraftClient client);

	@Override
	public float getWidth() {
		return cachedWidth;
	}
	
	@Override
	public float getHeight() {
		return cachedHeight;
	}
	
	public float getDefaultX() {
		return cachedDefaultX;
	}

	public float getDefaultY() {
		return cachedDefaultY;
	}

	@Override
	public float getX() {
		return cachedX;
	}

	@Override
	public float getY() {
		return cachedY;
	}

	@Override
	public void updateSelfX(MinecraftClient client) {
		cachedWidth = calculateWidth(client) * xScale;
		cachedDefaultX = calculateDefaultX(client);
		switch(xPosType) {
		case DEFAULT:
			cachedX = getDefaultX() + xOffset;
			break;
		case RELATIVE:
			cachedX = getXParent().getActiveElement().getWidth() * xRelativePos + xOffset + getXParent().getActiveElement().getX() - getWidth() * xAnchorPos;
			break;
		}
	}

	@Override
	public void updateSelfY(MinecraftClient client) {
		cachedHeight = calculateHeight(client) * yScale;
		cachedDefaultY = calculateDefaultY(client);
		switch(yPosType) {
		case DEFAULT:
			cachedY = getDefaultY() + yOffset;
			break;
		case RELATIVE:
			cachedY = getYParent().getActiveElement().getHeight() * yRelativePos + yOffset + getYParent().getActiveElement().getY() - getHeight() * yAnchorPos;
			break;
		}
	}

	public Matrix4f createMatrix() {
		//Quaternion quaternion = new Quaternion(new Vector3f(0.0f, 0.0f, 1.0f), rotationDegrees, true);
		Matrix4f matrix = Matrix4f.scale(xScale, yScale, 1);
		matrix.multiply(Matrix4f.translate(getX() / xScale - getDefaultX(),
				getY() / yScale - getDefaultY(), 1));
		//matrix.multiply(quaternion);
		parentNode.setUpdated();
		return matrix;
	}

	public PosType getXPosType() {
		return xPosType;
	}

	public PosType getYPosType() {
		return yPosType;
	}

	public float getXAnchorPos() {
		return xAnchorPos;
	}

	public float getYAnchorPos() {
		return yAnchorPos;
	}

	public float getXRelativePos() {
		return xRelativePos;
	}

	public float getYRelativePos() {
		return yRelativePos;
	}

	public float getXOffset() {
		return xOffset;
	}

	public float getYOffset() {
		return yOffset;
	}

	public float getXScale() {
		return xScale;
	}

	public float getYScale() {
		return yScale;
	}

	@Override
	public String toString() {
		return getIdentifier().toString();
	}

	// TODO: fix all of the garbage below here
	
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
			HudElementContainer parentNode = HudContainer.getElementRegistry().getElementContainer(HTIdentifier.fromString(relativeParentIdentifier));
			if(parentNode != null) {
				moveXUnder(parentNode);
			}
		}
		xPosType = Util.GSON.fromJson(xPosJson.get("posType"), PosType.class);
		xAnchorPos = xPosJson.get("anchorPos").getAsFloat();
		xOffset = xPosJson.get("offset").getAsFloat();
		xRelativePos = xPosJson.get("relativePos").getAsFloat();

		JsonObject yPosJson = elementJson.get("yPos").getAsJsonObject();
		parentIdentifier = yPosJson.get("parent");
		if (parentIdentifier != null && parentIdentifier.isJsonPrimitive() && parentIdentifier.getAsJsonPrimitive().isString()) {
			String relativeParentIdentifier = parentIdentifier.getAsString();
			HudElementContainer parentNode = HudContainer.getElementRegistry().getElementContainer(HTIdentifier.fromString(relativeParentIdentifier));
			if(parentNode != null) {
				moveYUnder(parentNode);
			}
		}
		yPosType = Util.GSON.fromJson(yPosJson.get("posType"), PosType.class);
		yAnchorPos = yPosJson.get("anchorPos").getAsFloat();
		yOffset = yPosJson.get("offset").getAsFloat();
		yRelativePos = yPosJson.get("relativePos").getAsFloat();

		xScale = elementJson.get("xScale").getAsFloat();
		yScale = elementJson.get("yScale").getAsFloat();
	}

	/**
	 * Override if any extra options are added to the element.
	 * Make sure to call super before anything else.
	 */
	public void fillSidebar(SidebarWidget sidebar) {
		ParentButtonWidget xRelativeParentButton = new ParentButtonWidget(4, 35, sidebar.width - 8, 14, getXParent(), getParentNode(), this::moveXUnder);

		ParentButtonWidget yRelativeParentButton = new ParentButtonWidget(4, 143, sidebar.width - 8, 14, getYParent(), getParentNode(), this::moveYUnder);

		xRelativeParentButton.active = !xPosType.equals(PosType.DEFAULT);
		yRelativeParentButton.active = !yPosType.equals(PosType.DEFAULT);

		HTSliderWidget xRelativeSlider = new HTSliderWidget(4, 54, sidebar.width - 8, 14, xRelativePos) {
			@Override
			protected void updateMessage() {
				setMessage(new TranslatableText("hudtweaks.options.relative_pos.display", Util.RELATIVE_POS_FORMATTER.format(value)));
			}

			@Override
			public void applyValue() {
				xRelativePos = (float) value;
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
				yRelativePos = (float) value;
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
				xAnchorPos = (float) value;
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
				yAnchorPos = (float) value;
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
				setText(Util.NUM_FIELD_FORMATTER.format(xOffset));
			}
		};
		xOffsetField.setText(Util.NUM_FIELD_FORMATTER.format(xOffset));
		xOffsetField.setChangedListener(s -> {
			if (s.equals("")) {
				xOffset = 0.0f;
				parentNode.setRequiresUpdate();
			} else {
				try {
					xOffset = Float.parseFloat(s);
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
				yOffset = 0.0f;
				parentNode.setRequiresUpdate();
			} else {
				try {
					yOffset = Float.parseFloat(s);
					parentNode.setRequiresUpdate();
				} catch(NumberFormatException ignored) {}
			}
		});


		NumberFieldWidget xScaleField = new NumberFieldWidget(MinecraftClient.getInstance().textRenderer, 48, 232, sidebar.width - 52, 14, new TranslatableText("hudtweaks.options.x_scale.name")) {
			@Override
			public void updateValue() {
				setText(Float.toString(xScale));
			}
		};
		xScaleField.setText(Float.toString(xScale));
		xScaleField.setChangedListener(s -> {
			if (s.equals("")) {
				xScale = 0.0f;
				parentNode.setRequiresUpdate();
			} else {
				try {
					float value = Float.parseFloat(s);
					float lastValue = xScale;
					xScale = Math.max(value, 0.0f);
					if (xScale != lastValue) parentNode.setRequiresUpdate();
				} catch(NumberFormatException ignored) {}
			}
		});

		NumberFieldWidget yScaleField = new NumberFieldWidget(MinecraftClient.getInstance().textRenderer, 48, 251, sidebar.width - 52, 14, new TranslatableText("hudtweaks.options.y_scale.name")) {
			@Override
			public void updateValue() {
				setText(Float.toString(yScale));
			}
		};
		yScaleField.setText(Float.toString(yScale));
		yScaleField.setChangedListener(s -> {
			if (s.equals("")) {
				yScale = 0.0f;
				parentNode.setRequiresUpdate();
			} else {
				try {
					float value = Float.parseFloat(s);
					float lastValue = yScale;
					yScale = Math.max(value, 0.0f);
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
