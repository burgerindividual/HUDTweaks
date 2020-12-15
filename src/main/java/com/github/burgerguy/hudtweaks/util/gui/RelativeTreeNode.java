package com.github.burgerguy.hudtweaks.util.gui;

import java.util.HashSet;
import java.util.Set;

import com.github.burgerguy.hudtweaks.gui.HudContainer;
import com.github.burgerguy.hudtweaks.util.gui.MatrixCache.UpdateEvent;

import net.minecraft.client.MinecraftClient;

public abstract class RelativeTreeNode implements XAxisNode, YAxisNode {
	private transient final String identifier;
	protected transient final Set<UpdateEvent> updateEvents = new HashSet<>();
	protected transient XAxisNode xParent;
	protected transient YAxisNode yParent;
	protected transient final Set<XAxisNode> xChildren = new HashSet<>();
	protected transient final Set<YAxisNode> yChildren = new HashSet<>();
	
	protected transient boolean requiresUpdate;
	
	public RelativeTreeNode(String identifier, UpdateEvent... updateEvents) {
		this.identifier = identifier;
		for (UpdateEvent event : updateEvents) {
			this.updateEvents.add(event);
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
	public void updateX(UpdateEvent event, MinecraftClient client, boolean parentUpdated, Set<XAxisNode> excludedElements, Set<XAxisNode> updatedElements) {
		boolean selfUpdated = false;
		if (!excludedElements.contains(this)) {
			if (requiresUpdate || parentUpdated) {
				updateSelfX(client);
				selfUpdated = true;
			} else {
				for (UpdateEvent e : updateEvents) {
					if (event.equals(e)) {
						updateSelfX(client);
						selfUpdated = true;
					}
				}
			}
			
			if (selfUpdated) {
				excludedElements.add(this);
				updatedElements.add(this);
			}
		}
		
		for (XAxisNode child : xChildren) {
			child.updateX(event, client, selfUpdated, excludedElements, updatedElements);
		}
	}
	
	@Override
	public void updateY(UpdateEvent event, MinecraftClient client, boolean parentUpdated, Set<YAxisNode> excludedElements, Set<YAxisNode> updatedElements) {
		boolean selfUpdated = false;
		if (!excludedElements.contains(this)) {
			if (requiresUpdate || parentUpdated) {
				updateSelfY(client);
				selfUpdated = true;
			} else {
				for (UpdateEvent e : updateEvents) {
					if (event.equals(e)) {
						updateSelfY(client);
						selfUpdated = true;
					}
				}
			}
			
			if (selfUpdated) {
				excludedElements.add(this);
				updatedElements.add(this);
			}
		}
		
		for (YAxisNode child : yChildren) {
			child.updateY(event, client, selfUpdated, excludedElements, updatedElements);
		}
	}
	
	public abstract void updateSelfX(MinecraftClient client);
	
	public abstract void updateSelfY(MinecraftClient client);
	
}
