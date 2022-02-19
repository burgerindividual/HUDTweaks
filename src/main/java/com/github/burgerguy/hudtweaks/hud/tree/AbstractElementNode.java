package com.github.burgerguy.hudtweaks.hud.tree;

import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import com.github.burgerguy.hudtweaks.hud.HudContainer;
import com.github.burgerguy.hudtweaks.hud.UpdateEvent;
import com.github.burgerguy.hudtweaks.util.Util;
import net.minecraft.client.MinecraftClient;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractElementNode {
	protected transient final HTIdentifier identifier;
	protected transient final Set<UpdateEvent> updateEvents = new HashSet<>();

	protected transient AbstractContainerNode containerNode;
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
	 * Only call this after the container node has been set.
	 */
	public void init() {
		moveXUnder(HudContainer.getScreenRoot());
		moveYUnder(HudContainer.getScreenRoot());
	}

	public void setContainerNode(AbstractContainerNode containerNode) {
		this.containerNode = containerNode;
	}

	public AbstractContainerNode getContainerNode() {
		return containerNode;
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
			xTreeParent.getXChildren().remove(containerNode);
		}
		newXParent.xTreeChildren.add(containerNode);
		xTreeParent = newXParent;
		containerNode.setRequiresUpdate();
	}

	public void moveYUnder(AbstractContainerNode newYParent) {
		if (yTreeParent != null) {
			if (newYParent.equals(yTreeParent)) return;
			yTreeParent.getYChildren().remove(containerNode);
		}
		newYParent.yTreeChildren.add(containerNode);
		yTreeParent = newYParent;
		containerNode.setRequiresUpdate();
	}

	public boolean shouldUpdateOnEvent(UpdateEvent event) {
		return Util.containsNotNull(updateEvents, event);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		AbstractElementNode that = (AbstractElementNode) o;

		return identifier.equals(that.identifier);
	}

	@Override
	public int hashCode() {
		return identifier.hashCode();
	}

	public abstract float getX();

	public abstract float getWidth();

	public abstract float getY();

	public abstract float getHeight();

	public abstract void updateSelfX(MinecraftClient client);

	public abstract void updateSelfY(MinecraftClient client);
}
