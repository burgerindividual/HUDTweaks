package com.github.burgerguy.hudtweaks.util.gui;

import com.github.burgerguy.hudtweaks.gui.RelativeParent;
import com.github.burgerguy.hudtweaks.util.json.RelativeParentSerializer;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import net.minecraft.client.MinecraftClient;

public class HudPosHelper {
	
	/**
	 * The type of positioning to use. Defaults to DEFAULT, which keeps
	 * the position in the unmodified spot. RELATIVE allows you to
	 * position it anywhere on the screen with a relative screen pos and
	 * offset, and BOUND allows you to bind it to a different element.
	 */
	protected PosType posType = PosType.DEFAULT;
	
	@JsonAdapter(RelativeParentSerializer.class)
	@SerializedName(value = "parent")
	/**
	 * The relative parent that this pos helper calculates its final
	 * coords with.
	 */
	protected RelativeParent relativeParent;
	
	/**
	 * The anchor point for calculation. Defaults to DEFAULT, which
	 * keeps the position in the unmodified spot.
	 */
	protected double anchorPos;
	
	/**
	 * The relative position of the element from 0 to 1 on the screen,
	 * with 1 being the far side and 0 being the close side.
	 */
	protected double relativePos;
	
	/**
	 * The offset from the anchor point.
	 */
	protected double offset;
	
	/**
	 * Set to true if any of the properties have been changed. This
	 * signals that the entire HudElement needs to be recalculated.
	 */
	protected transient boolean requiresUpdate;
	
	public HudPosHelper() {
	}
	
	public PosType getPosType() {
		return posType;
	}
	
	public void setPosType(PosType posType) {
		this.posType = posType;
		requiresUpdate = true;
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
	
	public double getAnchorPos() {
		return anchorPos;
	}

	public void setAnchorPos(double anchorPos) {
		this.anchorPos = anchorPos;
		if (posType.equals(PosType.RELATIVE)) requiresUpdate = true;
	}

	public double getOffset() {
		return offset;
	}

	public void setOffset(double offset) {
		this.offset = offset;
		requiresUpdate = true;
	}

	public double getRelativePos() {
		return relativePos;
	}

	public void setRelativePos(double relativePos) {
		this.relativePos = relativePos;
		if (posType.equals(PosType.RELATIVE)) requiresUpdate = true;
	}
	
	public void setRelativeParent(RelativeParent relativeParent) {
		this.relativeParent = relativeParent;
		if (posType.equals(PosType.RELATIVE)) requiresUpdate = true;
	}
	
	public RelativeParent getRelativeParent() {
		return relativeParent;
	}
	
	public boolean requiresUpdate() {
		return requiresUpdate;
	}
	
	/**
	 * Don't call this unless you know what you're doing.
	 */
	public void setUpdated() {
		requiresUpdate = false;
	}
	
	public int calculateScreenPos(int thisElementDimension, int defaultPos, MinecraftClient client) {
		switch(posType) {
		case DEFAULT:
			return (int) (defaultPos + offset);
		case RELATIVE:
			return (int) ((relativeParent.getDimension(client) * relativePos + offset + relativeParent.getPosition(client)) - (thisElementDimension * anchorPos));
		default:
			throw new UnsupportedOperationException("how");
		}
	}
	
}
