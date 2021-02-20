package com.github.burgerguy.hudtweaks.hud.tree;

import java.util.HashSet;
import java.util.Set;

import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import com.github.burgerguy.hudtweaks.hud.HudContainer;
import com.github.burgerguy.hudtweaks.hud.UpdateEvent;
import com.github.burgerguy.hudtweaks.util.Util;

import net.minecraft.client.MinecraftClient;

public abstract class AbstractTypeNodeEntry {
	private transient final HTIdentifier identifier;
	protected transient AbstractTypeNode parentNode;
	protected transient AbstractTypeNode xTreeParent;
	protected transient AbstractTypeNode yTreeParent;
	protected transient final Set<UpdateEvent> updateEvents = new HashSet<>();
	
	public AbstractTypeNodeEntry(HTIdentifier identifier, String... updateEvents) {
		this.identifier = identifier;
		for (String eventIdentifier : updateEvents) {
			UpdateEvent event = HudContainer.getEventRegistry().get(eventIdentifier);
			if (event != null) {
				this.updateEvents.add(event);
			}
		}
		
		moveXUnder(HudContainer.getScreenRoot());
		moveYUnder(HudContainer.getScreenRoot());
	}
	
	public void setParentNode(AbstractTypeNode parentNode) {
		this.parentNode = parentNode;
	}
	
	public AbstractTypeNode getParentNode() {
		return parentNode;
	}
	
	public final HTIdentifier getIdentifier() {
		return identifier;
	}
	
	public AbstractTypeNode getXParent() {
		return xTreeParent;
	}
	
	public AbstractTypeNode getYParent() {
		return yTreeParent;
	}
	
	public void moveXUnder(AbstractTypeNode newXParent) {
		if (xTreeParent != null) {
			if (newXParent.equals(xTreeParent)) return;
			xTreeParent.getXChildren().remove(parentNode);
		}
		newXParent.xTreeChildren.add(parentNode);
		xTreeParent = newXParent;
		parentNode.setRequiresUpdate();
	}
	
	public void moveYUnder(AbstractTypeNode newYParent) {
		if (yTreeParent != null) {
			if (newYParent.equals(yTreeParent)) return;
			yTreeParent.getYChildren().remove(parentNode);
		}
		newYParent.yTreeChildren.add(parentNode);
		yTreeParent = newYParent;
		parentNode.setRequiresUpdate();
	}
	
	public boolean shouldUpdateOnEvent(UpdateEvent event) {
		return Util.containsNotNull(updateEvents, event);
	}
	
	public abstract double getX(MinecraftClient client);
	
	public abstract double getWidth(MinecraftClient client);
	
	public abstract double getY(MinecraftClient client);
	
	public abstract double getHeight(MinecraftClient client);
	
	public abstract void updateSelfX(MinecraftClient client);
	
	public abstract void updateSelfY(MinecraftClient client);
}
