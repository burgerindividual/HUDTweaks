package com.github.burgerguy.hudtweaks.gui;

import java.awt.Point;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.glfw.GLFW;

import com.github.burgerguy.hudtweaks.gui.HudPosHelper.Anchor;
import com.github.burgerguy.hudtweaks.util.Util;
import com.github.burgerguy.hudtweaks.util.gui.HudCoordinatesSupplier;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;

public class HudElement {
	private transient final String identifier;
	private transient final int elementWidth;
	private transient final int elementHeight;
	private transient final HudCoordinatesSupplier defaultCoordsSupplier;
	
	@SerializedName(value = "xPos")
	private final HudPosHelper xPosHelper;
	@SerializedName(value = "yPos")
	private final HudPosHelper yPosHelper;
	
	/**
	 * The element specific options are only used by subclasses that want
	 * to take advantage of it.
	 */
	private final Map<String, Object> elementOptions;
	
	/**
	 * The extraSettingsMap can be null.
	 */
	public HudElement(String identifier, int elementWidth, int elementHeight, HudCoordinatesSupplier defaultCoordsSupplier, Map<String, Object> defaultElementOptions) {
		this.identifier = identifier;
		this.elementWidth = elementWidth;
		this.elementHeight = elementHeight;
		this.defaultCoordsSupplier = defaultCoordsSupplier;
		this.xPosHelper = new HudPosHelper(elementWidth);
		this.yPosHelper = new HudPosHelper(elementHeight);
		this.elementOptions = defaultElementOptions;
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
	
	public int getWidth() {
		return elementWidth;
	}

	public int getHeight() {
		return elementHeight;
	}
	
	public boolean requiresUpdate() {
		return xPosHelper.requiresUpdate() || yPosHelper.requiresUpdate();
	}
	
	public void setElementOption(String identifier, Object value) {
		if (elementOptions == null) Util.LOGGER.error("Element doesn't have any options");
		if (elementOptions.replace(identifier, value) == null) {
			Util.LOGGER.error("Element option " + identifier + " doesn't exist in element " + this.identifier);
		}
	}
	
	public Object getElementOption(String identifier) {
		if (elementOptions == null) Util.LOGGER.error("Element doesn't have any options");
		if (!elementOptions.containsKey(identifier)) Util.LOGGER.error("Element option " + identifier + " doesn't exist in element " + this.identifier);
		return elementOptions.get(identifier);
	}
	
	private void setUpdated() {
		yPosHelper.setUpdated();
		xPosHelper.setUpdated();
	}
	
	public Point calculateDefaultCoords(int screenWidth, int screenHeight) {
		return defaultCoordsSupplier.getPos(screenWidth, screenHeight);	// top left coordinate of default
	}
	
	public Matrix4f calculateMatrix(int screenWidth, int screenHeight) {
		Point defaultCoords = calculateDefaultCoords(screenWidth, screenHeight);
		int calculatedX = xPosHelper.calculateScreenPos(screenWidth, defaultCoords.x);
		int calculatedY = yPosHelper.calculateScreenPos(screenHeight, defaultCoords.y);
		
		Matrix4f matrix = Matrix4f.translate(calculatedX - defaultCoords.x,
											 calculatedY - defaultCoords.y,
											 0);
		
		setUpdated();
		return matrix;
	}
	
	public void updateFromJson(JsonElement json) {
		JsonObject elementJson = json.getAsJsonObject();
		
		JsonObject xPosJson = elementJson.get("xPos").getAsJsonObject();
		xPosHelper.setAnchor(Util.GSON.fromJson(xPosJson.get("anchor"), HudPosHelper.Anchor.class));
		xPosHelper.setOffset(xPosJson.get("offset").getAsDouble());
		xPosHelper.setRelativePos(xPosJson.get("relativePos").getAsDouble());
		
		JsonObject yPosJson = elementJson.get("yPos").getAsJsonObject();
		yPosHelper.setAnchor(Util.GSON.fromJson(yPosJson.get("anchor"), HudPosHelper.Anchor.class));
		yPosHelper.setOffset(yPosJson.get("offset").getAsDouble());
		yPosHelper.setRelativePos(yPosJson.get("relativePos").getAsDouble());
		
		JsonElement optionsJson = elementJson.get("elementOptions");
		if (optionsJson != null) {
			Map<String, Object> optionsFromJson = Util.GSON.fromJson(optionsJson, TypeToken.getParameterized(Map.class, String.class, Object.class).getType());
			for (Entry<String, Object> entry : optionsFromJson.entrySet()) {
				setElementOption(entry.getKey(), entry.getValue());
			}
		}
	}
	
	public HudElementWidget createWidget(HudTweaksOptionsScreen optionsScreen) {
		return new HudElementWidget(optionsScreen);
	}
	
	public class HudElementWidget implements Drawable, Element {
		private static final int OUTLINE_COLOR_NORMAL = 0xFFFF0000;
		private static final int OUTLINE_COLOR_SELECTED = 0xFF0000FF;
		
		private final HudTweaksOptionsScreen optionsScreen;
		
		private HudElementWidget(HudTweaksOptionsScreen optionsScreen) {
			this.optionsScreen = optionsScreen;
		}

		@Override
		public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
			Point defaultCoords = calculateDefaultCoords(optionsScreen.width, optionsScreen.height);
			int x1 = xPosHelper.calculateScreenPos(optionsScreen.width, defaultCoords.x);
			int y1 = yPosHelper.calculateScreenPos(optionsScreen.height, defaultCoords.y);
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
				if (!xPosHelper.getAnchor().equals(Anchor.DEFAULT)) xPosHelper.setRelativePos(MathHelper.clamp(xPosHelper.getRelativePos() + (deltaX / optionsScreen.width), 0.0D, 1.0D));
				if (!yPosHelper.getAnchor().equals(Anchor.DEFAULT)) yPosHelper.setRelativePos(MathHelper.clamp(yPosHelper.getRelativePos() + (deltaY / optionsScreen.height), 0.0D, 1.0D));
			} else {
				xPosHelper.setOffset(xPosHelper.getOffset() + deltaX);
				yPosHelper.setOffset(yPosHelper.getOffset() + deltaY);
			}
			optionsScreen.updateSidebarValues();
			return true;
			// TODO: implement dragging relative normally and dragging offset with shift
			// make sure when it's implemented to only check the bounds when it's initially clicked, and then
			// don't check again until release
		}
		
		@Override
		public boolean isMouseOver(double mouseX, double mouseY) {
			Point defaultCoords = calculateDefaultCoords(optionsScreen.width, optionsScreen.height);
			int x1 = xPosHelper.calculateScreenPos(optionsScreen.width, defaultCoords.x);
			int y1 = yPosHelper.calculateScreenPos(optionsScreen.height, defaultCoords.y);
			int x2 = x1 + elementWidth;
			int y2 = y1 + elementHeight;
			return mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2;
		}
		
		public HudElement getParent() {
			return HudElement.this;
		}
		
	}
	
}
