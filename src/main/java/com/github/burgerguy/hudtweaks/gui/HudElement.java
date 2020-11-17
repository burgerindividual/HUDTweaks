package com.github.burgerguy.hudtweaks.gui;

import java.awt.Point;

import com.github.burgerguy.hudtweaks.util.gui.HudCoordinatesSupplier;

import net.minecraft.util.math.Matrix4f;

public class HudElement {
	private transient final String identifier;
	private final HudPosHelper xPos;
	private final HudPosHelper yPos;
	private transient final int elementWidth;
	private transient final int elementHeight;
	private transient final HudCoordinatesSupplier defaultCoordsSupplier;
	
	public HudElement(String identifier, int elementWidth, int elementHeight, HudCoordinatesSupplier defaultCoordsSupplier) {
		this.identifier = identifier;
		this.xPos = new HudPosHelper(elementWidth);
		this.yPos = new HudPosHelper(elementHeight);
		this.elementWidth = elementWidth;
		this.elementHeight = elementHeight;
		this.defaultCoordsSupplier = defaultCoordsSupplier;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public HudPosHelper getXPosHelper() {
		return xPos;
	}
	
	public HudPosHelper getYPosHelper() {
		return yPos;
	}
	
	public int getWidth() {
		return elementWidth;
	}

	public int getHeight() {
		return elementHeight;
	}
	
	public boolean requiresUpdate() {
		return xPos.requiresUpdate() || yPos.requiresUpdate();
	}
	
	private void setUpdated() {
		yPos.setUpdated();
		xPos.setUpdated();
	}
	
	public Matrix4f calculateMatrix(int screenWidth, int screenHeight) {
		int calculatedX = xPos.calculateScreenPos(screenWidth);
		int calculatedY = yPos.calculateScreenPos(screenHeight);
		Point defaultCoords = defaultCoordsSupplier.getPos(screenWidth, screenHeight);	// top left coordinate
		
		Matrix4f matrix = Matrix4f.translate(calculatedX == Integer.MIN_VALUE ? 0 : calculatedX - defaultCoords.x,
											 calculatedY == Integer.MIN_VALUE ? 0 : calculatedY - defaultCoords.y,
											 0);
		
		setUpdated();
		return matrix;
	}
}
