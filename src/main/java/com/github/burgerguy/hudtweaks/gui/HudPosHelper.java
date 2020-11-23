package com.github.burgerguy.hudtweaks.gui;

import com.google.gson.annotations.SerializedName;

public class HudPosHelper {
	/**
	 * Either width or height
	 */
	private transient final int elementDimension;
	
	/**
	 * The anchor point for calculation. Defaults to DEFAULT, which
	 * keeps the position in the unmodified spot.
	 */
	private Anchor anchor = Anchor.DEFAULT;
	
	/**
	 * The offset from the anchor point.
	 */
	private double offset;
	
	/**
	 * The relative position of the element from 0 to 1 on the screen,
	 * with 1 being the far side and 0 being the close side.
	 */
	private double relativePos;
	
	/**
	 * Set to true if any of the properties have been changed. This
	 * signals that the entire HudElement needs to be recalculated.
	 */
	private transient boolean requiresUpdate;
	
	public HudPosHelper(int elementDimension) {
		this.elementDimension = elementDimension;
	}
	
	public Anchor getAnchor() {
		return anchor;
	}

	public void setAnchor(Anchor type) {
		this.anchor = type;
		this.requiresUpdate = true;
	}
	
	public enum Anchor {
		@SerializedName(value = "minimum", alternate = "MINIMUM")
		MINIMUM,
		@SerializedName(value = "maximum", alternate = "MAXIMUM")
		MAXIMUM,
		@SerializedName(value = "center", alternate = "CENTER")
		CENTER,
		@SerializedName(value = "default", alternate = "DEFAULT")
		DEFAULT
	}

	public double getOffset() {
		return offset;
	}

	public void setOffset(double offset) {
		this.offset = offset;
		this.requiresUpdate = true;
	}

	public double getRelativePos() {
		return relativePos;
	}

	public void setRelativePos(double relativePos) {
		this.relativePos = relativePos;
		this.requiresUpdate = true;
	}
	
	public boolean requiresUpdate() {
		return requiresUpdate;
	}
	
	/**
	 * Don't call this unless you know what you're doing.
	 */
	void setUpdated() {
		this.requiresUpdate = false;
	}
	
	public void reset() {
		this.anchor = null;
		this.offset = 0;
		this.relativePos = 0;
		this.requiresUpdate = true;
	}
	
	public int calculateScreenPos(int screenDimension, int defaultPos) {
		if (anchor.equals(Anchor.DEFAULT)) {
			return (int) (defaultPos + offset);
		}
		
		int negativeAnchorPos = (int) (screenDimension * relativePos + offset);
		
		switch(anchor) {
			case MINIMUM:
				return negativeAnchorPos;
			case CENTER:
				return negativeAnchorPos - (int) (elementDimension / 2F);
			case MAXIMUM:
				return negativeAnchorPos - elementDimension;
			default:
				throw new UnsupportedOperationException("Unexpected anchor value");
		}
	}
	
}
