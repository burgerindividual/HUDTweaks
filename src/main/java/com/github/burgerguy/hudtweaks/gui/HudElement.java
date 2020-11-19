package com.github.burgerguy.hudtweaks.gui;

import java.awt.Point;
import java.util.Map;
import java.util.Map.Entry;

import com.github.burgerguy.hudtweaks.util.Util;
import com.github.burgerguy.hudtweaks.util.gui.HudCoordinatesSupplier;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

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
		xPosHelper.setOffset(xPosJson.get("offset").getAsInt());
		xPosHelper.setRelativePos(xPosJson.get("relativePos").getAsDouble());
		
		JsonObject yPosJson = elementJson.get("yPos").getAsJsonObject();
		yPosHelper.setAnchor(Util.GSON.fromJson(yPosJson.get("anchor"), HudPosHelper.Anchor.class));
		yPosHelper.setOffset(yPosJson.get("offset").getAsInt());
		yPosHelper.setRelativePos(yPosJson.get("relativePos").getAsDouble());
		
		JsonElement optionsJson = elementJson.get("elementOptions");
		if (optionsJson != null) {
			Map<String, Object> optionsFromJson = Util.GSON.fromJson(optionsJson, TypeToken.getParameterized(Map.class, String.class, Object.class).getType());
			for (Entry<String, Object> entry : optionsFromJson.entrySet()) {
				setElementOption(entry.getKey(), entry.getValue());
			}
		}
	}
	
}
