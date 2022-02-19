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
import net.minecraft.client.gui.Drawable;
import net.minecraft.util.math.Vec3f;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import org.jetbrains.annotations.Nullable;

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
	protected transient float xRotationAnchor = .5f;
	protected transient float yRotationAnchor = .5f;
	protected transient float rotationDegrees = 0.0F;

	// These are marked transient because we don't want them serialized at all
	protected transient float cachedWidth;
	protected transient float cachedHeight;
	protected transient float cachedDefaultX;
	protected transient float cachedDefaultY;
	protected transient float cachedX;
	protected transient float cachedY;
	protected transient Matrix4f cachedMatrix;

	public HudElement(HTIdentifier identifier, String... updateEvents) {
		super(identifier, updateEvents);
	}

	public enum PosType {
		/**
		 * Keeps the position in the unmodified spot, but allows for offset.
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

	@Override
	public HudElementContainer getContainerNode() {
		return (HudElementContainer) containerNode;
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
		switch (xPosType) {
			case DEFAULT -> cachedX = getDefaultX() + xOffset;
			case RELATIVE -> cachedX = getXParent().getActiveElement().getWidth() * xRelativePos + xOffset + getXParent().getActiveElement().getX() - getWidth() * xAnchorPos;
		}
	}

	@Override
	public void updateSelfY(MinecraftClient client) {
		cachedHeight = calculateHeight(client) * yScale;
		cachedDefaultY = calculateDefaultY(client);
		switch (yPosType) {
			case DEFAULT -> cachedY = getDefaultY() + yOffset;
			case RELATIVE -> cachedY = getYParent().getActiveElement().getHeight() * yRelativePos + yOffset + getYParent().getActiveElement().getY() - getHeight() * yAnchorPos;
		}
	}

	protected void createMatrix() { // TODO: fix weird offset when scaling and rotating
		Quaternion quaternion = new Quaternion(Vec3f.POSITIVE_Z, rotationDegrees, true);
		Matrix4f matrix = Matrix4f.translate(getX(), getY(), 0);
		matrix.multiply(Matrix4f.translate(getXRotationAnchor() * getWidth(), getYRotationAnchor() * getHeight(), 0));
		matrix.multiply(quaternion);
		matrix.multiply(Matrix4f.translate(-getXRotationAnchor() * getWidth(), -getYRotationAnchor() * getHeight(), 0));
		matrix.multiply(Matrix4f.scale(xScale, yScale, 1));
		matrix.multiply(Matrix4f.translate(-getDefaultX(), -getDefaultY(), 0));
		cachedMatrix = matrix;
	}

	public Matrix4f getMatrix() {
		return cachedMatrix;
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

	public float getXRotationAnchor() {
		return xRotationAnchor;
	}

	public float getYRotationAnchor() {
		return yRotationAnchor;
	}

	public float getRotationDegrees() {
		return rotationDegrees;
	}

	@Override
	public String toString() {
		return getIdentifier().toString();
	}

	/**
	 * Override if any extra options are added to the element.
	 * Make sure to call super before anything else.
	 */
	public void resetToDefaults() {
		containerNode.setRequiresUpdate();
		this.xPosType = PosType.DEFAULT;
		this.yPosType = PosType.DEFAULT;
		this.xAnchorPos = 0.0f;
		this.yAnchorPos = 0.0f;
		this.xRelativePos = 0.0f;
		this.yRelativePos = 0.0f;
		this.xOffset = 0.0f;
		this.yOffset = 0.0f;
		this.xScale = 1.0f;
		this.yScale = 1.0f;
		this.xRotationAnchor = .5f;
		this.yRotationAnchor = .5f;
		this.rotationDegrees = 0.0F;
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
		SidebarWidget.DrawableEntry<ParentButtonWidget> xRelativeParentButton = new SidebarWidget.DrawableEntry<>(y -> {
			ParentButtonWidget inner = new ParentButtonWidget(4, y, sidebar.width - 8, 14, getXParent(), getContainerNode(), this::moveXUnder);
			inner.active = !xPosType.equals(PosType.DEFAULT);
			return inner;
		}, 14);

		SidebarWidget.DrawableEntry<ParentButtonWidget> yRelativeParentButton = new SidebarWidget.DrawableEntry<>(y -> {
			ParentButtonWidget inner = new ParentButtonWidget(4, y, sidebar.width - 8, 14, getYParent(), getContainerNode(), this::moveYUnder);
			inner.active = !yPosType.equals(PosType.DEFAULT);
			return inner;
		}, 14);

		SidebarWidget.DrawableEntry<HTSliderWidget> xRelativeSlider = new SidebarWidget.DrawableEntry<>(y -> {
			HTSliderWidget inner = new HTSliderWidget(4, y, sidebar.width - 8, 14, xRelativePos) {
				@Override
				protected void updateMessage() {
					setMessage(new TranslatableText("hudtweaks.options.relative_pos.display", Util.RELATIVE_POS_FORMATTER.format(value)));
				}

				@Override
				public void applyValue() {
					xRelativePos = (float) value;
					containerNode.setRequiresUpdate();
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
			inner.active = !xPosType.equals(PosType.DEFAULT);
			return inner;
		}, 14);

		SidebarWidget.DrawableEntry<HTSliderWidget> yRelativeSlider = new SidebarWidget.DrawableEntry<>(y -> {
			HTSliderWidget inner = new HTSliderWidget(4, y, sidebar.width - 8, 14, yRelativePos) {
				@Override
				protected void updateMessage() {
					setMessage(new TranslatableText("hudtweaks.options.relative_pos.display", Util.RELATIVE_POS_FORMATTER.format(value)));
				}

				@Override
				public void applyValue() {
					yRelativePos = (float) value;
					containerNode.setRequiresUpdate();
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
			inner.active = !yPosType.equals(PosType.DEFAULT);
			return inner;
		}, 14);

		SidebarWidget.DrawableEntry<HTSliderWidget> xAnchorSlider = new SidebarWidget.DrawableEntry<>(y -> {
			HTSliderWidget inner = new HTSliderWidget(4, y, sidebar.width - 8, 14, xAnchorPos) {
				@Override
				protected void updateMessage() {
					setMessage(new TranslatableText("hudtweaks.options.anchor_pos.display", Util.ANCHOR_POS_FORMATTER.format(value)));
				}

				@Override
				public void applyValue() {
					xAnchorPos = (float) value;
					containerNode.setRequiresUpdate();
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
			inner.active = !xPosType.equals(PosType.DEFAULT);
			return inner;
		}, 14);

		SidebarWidget.DrawableEntry<HTSliderWidget> yAnchorSlider = new SidebarWidget.DrawableEntry<>(y -> {
			HTSliderWidget inner = new HTSliderWidget(4, y, sidebar.width - 8, 14, yAnchorPos) {
				@Override
				protected void updateMessage() {
					setMessage(new TranslatableText("hudtweaks.options.anchor_pos.display", Util.ANCHOR_POS_FORMATTER.format(value)));
				}

				@Override
				public void applyValue() {
					yAnchorPos = (float) value;
					containerNode.setRequiresUpdate();
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
			inner.active = !yPosType.equals(PosType.DEFAULT);
			return inner;
		}, 14);

		SidebarWidget.DrawableEntry<PosTypeButtonWidget> xPosTypeButton = new SidebarWidget.DrawableEntry<>(y -> new PosTypeButtonWidget(4, y, sidebar.width - 8, 14,  xPosType, t -> {
			xPosType = t;
			containerNode.setRequiresUpdate();
			xAnchorSlider.getDrawable().active = !t.equals(PosType.DEFAULT);
			xRelativeSlider.getDrawable().active = !t.equals(PosType.DEFAULT);
			xRelativeParentButton.getDrawable().active = !t.equals(PosType.DEFAULT);
		}), 14);

		SidebarWidget.DrawableEntry<PosTypeButtonWidget> yPosTypeButton = new SidebarWidget.DrawableEntry<>(y -> new PosTypeButtonWidget(4, y, sidebar.width - 8, 14,  yPosType, t -> {
			yPosType = t;
			containerNode.setRequiresUpdate();
			yAnchorSlider.getDrawable().active = !t.equals(PosType.DEFAULT);
			yRelativeSlider.getDrawable().active = !t.equals(PosType.DEFAULT);
			yRelativeParentButton.getDrawable().active = !t.equals(PosType.DEFAULT);
		}), 14);

		SidebarWidget.DrawableEntry<LabeledFieldWidget<?>> xOffsetField = new SidebarWidget.DrawableEntry<>(y ->
			new LabeledFieldWidget<>(5, y, sidebar.width - 9, 14, 0xCCFFFFFF, 3, new TranslatableText("hudtweaks.options.offset.display"), (x, width) -> {
				NumberFieldWidget inner = new NumberFieldWidget(MinecraftClient.getInstance().textRenderer, 43, y, sidebar.width - 47, 14, new TranslatableText("hudtweaks.options.offset.name")) {
					@Override
					public void updateValue() {
						setText(Float.toString(xOffset));
					}
				};
				inner.setText(Float.toString(xOffset));
				inner.setChangedListener(s -> {
					if (s.equals("")) {
						xOffset = 0.0f;
						containerNode.setRequiresUpdate();
					} else {
						try {
							xOffset = Float.parseFloat(s);
							containerNode.setRequiresUpdate();
						} catch(NumberFormatException ignored) {}
					}
				});
				return inner;
			}), 14);

		SidebarWidget.DrawableEntry<LabeledFieldWidget<?>> yOffsetField = new SidebarWidget.DrawableEntry<>(y ->
			new LabeledFieldWidget<>(5, y, sidebar.width - 9, 14, 0xCCFFFFFF, 3, new TranslatableText("hudtweaks.options.offset.display"), (x, width) -> {
				NumberFieldWidget inner = new NumberFieldWidget(MinecraftClient.getInstance().textRenderer, x, y, width, 14, new TranslatableText("hudtweaks.options.offset.name")) {
					@Override
					public void updateValue() {
						setText(Float.toString(yOffset));
					}
				};
				inner.setText(Float.toString(yOffset));
				inner.setChangedListener(s -> {
					if (s.equals("")) {
						yOffset = 0.0f;
						containerNode.setRequiresUpdate();
					} else {
						try {
							yOffset = Float.parseFloat(s);
							containerNode.setRequiresUpdate();
						} catch (NumberFormatException ignored) {
						}
					}
				});
				return inner;
			}), 14);

		SidebarWidget.DrawableEntry<LabeledFieldWidget<?>> xScaleField = new SidebarWidget.DrawableEntry<>(y ->
			new LabeledFieldWidget<>(5, y, sidebar.width - 9, 14, 0xCCFFFFFF, 3, new TranslatableText("hudtweaks.options.x_scale.display"), (x, width) -> {
				NumberFieldWidget inner = new NumberFieldWidget(MinecraftClient.getInstance().textRenderer, x, y, width, 14, new TranslatableText("hudtweaks.options.x_scale.name")) {
					@Override
					public void updateValue() {
						setText(Float.toString(xScale));
					}
				};
				inner.setText(Float.toString(xScale));
				inner.setChangedListener(s -> {
					if (s.equals("")) {
						xScale = 0.0f;
						containerNode.setRequiresUpdate();
					} else {
						try {
							float value = Float.parseFloat(s);
							float lastValue = xScale;
							xScale = Math.max(value, 0.0f);
							if (xScale != lastValue) containerNode.setRequiresUpdate();
						} catch(NumberFormatException ignored) {}
					}
				});
				return inner;
			}), 14);

		SidebarWidget.DrawableEntry<LabeledFieldWidget<?>> yScaleField = new SidebarWidget.DrawableEntry<>(y ->
			new LabeledFieldWidget<>(5, y, sidebar.width - 9, 14, 0xCCFFFFFF, 3, new TranslatableText("hudtweaks.options.y_scale.display"), (x, width) -> {
				NumberFieldWidget inner = new NumberFieldWidget(MinecraftClient.getInstance().textRenderer, x, y, width, 14, new TranslatableText("hudtweaks.options.y_scale.name")) {
					@Override
					public void updateValue() {
						setText(Float.toString(yScale));
					}
				};
				inner.setText(Float.toString(yScale));
				inner.setChangedListener(s -> {
					if (s.equals("")) {
						yScale = 0.0f;
						containerNode.setRequiresUpdate();
					} else {
						try {
							float value = Float.parseFloat(s);
							float lastValue = yScale;
							yScale = Math.max(value, 0.0f);
							if (yScale != lastValue) containerNode.setRequiresUpdate();
						} catch (NumberFormatException ignored) {
						}
					}
				});
				return inner;
			}), 14);

		int labelHeight = MinecraftClient.getInstance().textRenderer.fontHeight;
		sidebar.addPadding(4);
		sidebar.addEntry(new SidebarWidget.DrawableEntry<>(y -> new HTLabelWidget(new TranslatableText("hudtweaks.options.x_pos.display"), 5, y, 0xCCB0B0B0, false), labelHeight));
		sidebar.addPadding(3);
		sidebar.addEntry(xPosTypeButton);
		sidebar.addPadding(3);
		sidebar.addEntry(xRelativeParentButton);
		sidebar.addPadding(3);
		sidebar.addEntry(xRelativeSlider);
		sidebar.addPadding(3);
		sidebar.addEntry(xAnchorSlider);
		sidebar.addPadding(3);
		sidebar.addEntry(xOffsetField);
		sidebar.addPadding(5);
		sidebar.addEntry(new SidebarWidget.DrawableEntry<>(y -> new HTLabelWidget(new TranslatableText("hudtweaks.options.y_pos.display"), 5, y, 0xCCB0B0B0, false), labelHeight));
		sidebar.addPadding(3);
		sidebar.addEntry(yPosTypeButton);
		sidebar.addPadding(3);
		sidebar.addEntry(yRelativeParentButton);
		sidebar.addPadding(3);
		sidebar.addEntry(yRelativeSlider);
		sidebar.addPadding(3);
		sidebar.addEntry(yAnchorSlider);
		sidebar.addPadding(3);
		sidebar.addEntry(yOffsetField);
		sidebar.addPadding(5);
		sidebar.addEntry(new SidebarWidget.DrawableEntry<>(y -> new HTLabelWidget(new TranslatableText("hudtweaks.options.scale.display"), 5, y, 0xCCB0B0B0, false), labelHeight));
		sidebar.addPadding(3);
		sidebar.addEntry(xScaleField);
		sidebar.addPadding(3);
		sidebar.addEntry(yScaleField);
	}
}
