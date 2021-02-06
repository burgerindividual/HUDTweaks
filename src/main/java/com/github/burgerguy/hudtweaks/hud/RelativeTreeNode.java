package com.github.burgerguy.hudtweaks.hud;

import java.util.HashSet;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import com.github.burgerguy.hudtweaks.util.Util;

import net.minecraft.client.MinecraftClient;

public abstract class RelativeTreeNode implements XAxisNode, YAxisNode {
	private transient final String identifier;
	protected transient final Set<UpdateEvent> updateEvents = new HashSet<>();
	protected transient XAxisNode xParent;
	protected transient YAxisNode yParent;
	protected transient final Set<XAxisNode> xChildren = new HashSet<>();
	protected transient final Set<YAxisNode> yChildren = new HashSet<>();
	
	private transient boolean requiresUpdate;
	
	public RelativeTreeNode(String identifier, String... updateEvents) {
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
	
	@Override
	public String getIdentifier() {
		return identifier;
	}
	
	@Override
	public XAxisNode getXParent() {
		return xParent;
	}

	@Override
	public YAxisNode getYParent() {
		return yParent;
	}

	@Override
	public Set<XAxisNode> getXChildren() {
		return xChildren;
	}
	
	@Override
	public Set<YAxisNode> getYChildren() {
		return yChildren;
	}
	
	@Override
	public void moveXUnder(XAxisNode newXParent) {
		if (xParent != null) {
			if (newXParent.equals(xParent)) return;
			xParent.getXChildren().remove(this);
		}
		newXParent.getXChildren().add(this);
		xParent = newXParent;
		requiresUpdate = true;
	}
	
	@Override
	public void moveYUnder(YAxisNode newYParent) {
		if (yParent != null) {
			if (newYParent.equals(yParent)) return;
			yParent.getYChildren().remove(this);
		}
		newYParent.getYChildren().add(this);
		yParent = newYParent;
		requiresUpdate = true;
	}

	@Override
	/**
	 * Paassing null to the UpdateEvent will try a manual update.
	 */
	public void tryUpdateX(@Nullable UpdateEvent event, MinecraftClient client, boolean parentUpdated, Set<XAxisNode> updatedElementsX) {
		boolean selfUpdated = false;
		if (!updatedElementsX.contains(this) &&
			(parentUpdated || requiresUpdate || Util.containsNotNull(updateEvents, event))) {
			updateSelfX(client);
			updatedElementsX.add(this);
			selfUpdated = true;
		}
		
		for (XAxisNode child : xChildren) {
			child.tryUpdateX(event, client, selfUpdated, updatedElementsX);
		}
	}
	
	@Override
	/**
	 * Paassing null to the UpdateEvent will try a manual update.
	 */
	public void tryUpdateY(@Nullable UpdateEvent event, MinecraftClient client, boolean parentUpdated, Set<YAxisNode> updatedElementsY) {
		boolean selfUpdated = false;
		if (!updatedElementsY.contains(this) &&
			(parentUpdated || requiresUpdate || Util.containsNotNull(updateEvents, event))) {
			updateSelfY(client);
			updatedElementsY.add(this);
			selfUpdated = true;
		}
		
		for (YAxisNode child : yChildren) {
			child.tryUpdateY(event, client, selfUpdated, updatedElementsY);
		}
	}
	
	public void setRequiresUpdate() {
		requiresUpdate = true;
	}
	
	protected void setUpdated() {
		requiresUpdate = false;
	}
	
	public abstract void updateSelfX(MinecraftClient client);
	
	public abstract void updateSelfY(MinecraftClient client);
	
}
