package com.github.burgerguy.hudtweaks.hud.tree;

import java.util.HashSet;
import java.util.Set;

import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import com.github.burgerguy.hudtweaks.hud.HudContainer;
import com.github.burgerguy.hudtweaks.hud.UpdateEvent;
import com.github.burgerguy.hudtweaks.util.Util;

import net.minecraft.client.MinecraftClient;

public abstract class AbstractElementNode {
	protected transient final HTIdentifier identifier;
	protected transient final Set<UpdateEvent> updateEvents = new HashSet<>();

	protected transient AbstractContainerNode parentNode;
	protected transient AbstractContainerNode xTreeParent;
	protected transient AbstractContainerNode yTreeParent;

	public AbstractElementNode(HTIdentifier identifier, String... updateEvents) {
		this.identifier = identifier;
		for (String eventIdentifier : updateEvents) {
			UpdateEvent event = HudContainer.getEventRegistry().get(eventIdentifier);
			if (event != null) {
				this.updateEvents.add(event);
			}
		}
	}

	/**
	 * Only call this after the parent node has been set.
	 */
	public void init() {
		moveXUnder(HudContainer.getScreenRoot());
		moveYUnder(HudContainer.getScreenRoot());
	}

	public void setParentNode(AbstractContainerNode parentNode) {
		this.parentNode = parentNode;
	}

	@SuppressWarnings("unchecked")
	public <T extends AbstractContainerNode> T getParentNode() {
		return (T) parentNode;
	}

	public final HTIdentifier getIdentifier() {
		return identifier;
	}

	public AbstractContainerNode getXParent() {
		return xTreeParent;
	}

	public AbstractContainerNode getYParent() {
		return yTreeParent;
	}

	public void moveXUnder(AbstractContainerNode newXParent) {
		if (xTreeParent != null) {
			if (newXParent.equals(xTreeParent)) return;
			xTreeParent.getXChildren().remove(parentNode);
		}
		newXParent.xTreeChildren.add(parentNode);
		xTreeParent = newXParent;
		parentNode.setRequiresUpdate();
	}

	public void moveYUnder(AbstractContainerNode newYParent) {
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

	public abstract double getX();

	public abstract double getWidth();

	public abstract double getY();

	public abstract double getHeight();

	public abstract void updateSelfX(MinecraftClient client);

	public abstract void updateSelfY(MinecraftClient client);
}
