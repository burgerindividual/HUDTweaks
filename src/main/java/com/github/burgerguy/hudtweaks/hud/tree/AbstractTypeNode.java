package com.github.burgerguy.hudtweaks.hud.tree;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import com.github.burgerguy.hudtweaks.hud.UpdateEvent;

import net.minecraft.client.MinecraftClient;

public abstract class AbstractTypeNode {
	private transient final HTIdentifier.ElementType elementIdentifier;
	protected transient final Set<AbstractTypeNode> xTreeChildren = new HashSet<>();
	protected transient final Set<AbstractTypeNode> yTreeChildren = new HashSet<>();
	
	private transient boolean requiresUpdate;
	
	public AbstractTypeNode(HTIdentifier.ElementType elementIdentifier) {
		this.elementIdentifier = elementIdentifier;
	}
	
	public final HTIdentifier.ElementType getElementIdentifier() {
		return elementIdentifier;
	}
	
	public Set<AbstractTypeNode> getXChildren() {
		return xTreeChildren;
	}
	
	public Set<AbstractTypeNode> getYChildren() {
		return yTreeChildren;
	}
	
	public abstract AbstractTypeNodeEntry getActiveEntry();
	
	public abstract List<AbstractTypeNodeEntry> getRawEntryList();
	
	/**
	 * Passing null to the UpdateEvent will try a manual update.
	 */
	public void tryUpdateX(@Nullable UpdateEvent event, MinecraftClient client, boolean parentUpdated, Set<HTIdentifier.ElementType> updatedElementsX) {
		boolean selfUpdated = false;
		if (!updatedElementsX.contains(elementIdentifier)) {
			if (parentUpdated || requiresUpdate) {
				getActiveEntry().updateSelfX(client);
				updatedElementsX.add(elementIdentifier);
				selfUpdated = true;
			} else {
				AbstractTypeNodeEntry activeEntry = getActiveEntry();
				if (activeEntry.shouldUpdateOnEvent(event)) {
					activeEntry.updateSelfX(client);
					updatedElementsX.add(elementIdentifier);
					selfUpdated = true;
				}
			}
		}
		
		for (AbstractTypeNode child : xTreeChildren) {
			child.tryUpdateX(event, client, selfUpdated, updatedElementsX);
		}
	}
	
	/**
	 * Passing null to the UpdateEvent will try a manual update.
	 */
	public void tryUpdateY(@Nullable UpdateEvent event, MinecraftClient client, boolean parentUpdated, Set<HTIdentifier.ElementType> updatedElementsY) {
		boolean selfUpdated = false;
		if (!updatedElementsY.contains(elementIdentifier)) {
			if (parentUpdated || requiresUpdate) {
				getActiveEntry().updateSelfY(client);
				updatedElementsY.add(elementIdentifier);
				selfUpdated = true;
			} else {
				AbstractTypeNodeEntry activeEntry = getActiveEntry();
				if (activeEntry.shouldUpdateOnEvent(event)) {
					activeEntry.updateSelfY(client);
					updatedElementsY.add(elementIdentifier);
					selfUpdated = true;
				}
			}
		}
		
		for (AbstractTypeNode child : yTreeChildren) {
			child.tryUpdateY(event, client, selfUpdated, updatedElementsY);
		}
	}
	
	public void setRequiresUpdate() {
		requiresUpdate = true;
	}
	
	/**
	 * Do not touch unless you know what you're doing.
	 */
	public void setUpdated() {
		requiresUpdate = false;
	}
}
