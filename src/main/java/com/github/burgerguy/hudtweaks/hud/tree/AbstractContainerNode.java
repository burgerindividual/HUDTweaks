package com.github.burgerguy.hudtweaks.hud.tree;

import com.github.burgerguy.hudtweaks.hud.UpdateEvent;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractContainerNode {
	protected final Set<AbstractContainerNode> xTreeChildren = new HashSet<>();
	protected final Set<AbstractContainerNode> yTreeChildren = new HashSet<>();

	private boolean requiresUpdate;

	public Set<? extends AbstractContainerNode> getXChildren() {
		return xTreeChildren;
	}

	public Set<? extends AbstractContainerNode> getYChildren() {
		return yTreeChildren;
	}

	public abstract <T extends AbstractElementNode> T getInitialElement();

	public abstract <T extends AbstractElementNode> T getActiveElement();

	/**
	 * Passing null to the UpdateEvent will try a manual update.
	 */
	public void tryUpdateX(@Nullable UpdateEvent event, MinecraftClient client, boolean parentUpdated, Set<AbstractContainerNode> updatedElementsX) {
		boolean selfUpdated = false;
		if (!updatedElementsX.contains(this)) {
			if (parentUpdated || requiresUpdate) {
				getActiveElement().updateSelfX(client);
				updatedElementsX.add(this);
				selfUpdated = true;
			} else {
				AbstractElementNode activeElement = getActiveElement();
				if (activeElement.shouldUpdateOnEvent(event)) {
					activeElement.updateSelfX(client);
					updatedElementsX.add(this);
					selfUpdated = true;
				}
			}
		}

		for (AbstractContainerNode child : xTreeChildren) {
			child.tryUpdateX(event, client, selfUpdated, updatedElementsX);
		}
	}

	/**
	 * Passing null to the UpdateEvent will try a manual update.
	 */
	public void tryUpdateY(@Nullable UpdateEvent event, MinecraftClient client, boolean parentUpdated, Set<AbstractContainerNode> updatedElementsY) {
		boolean selfUpdated = false;
		if (!updatedElementsY.contains(this)) {
			if (parentUpdated || requiresUpdate) {
				getActiveElement().updateSelfY(client);
				updatedElementsY.add(this);
				selfUpdated = true;
			} else {
				AbstractElementNode activeElement = getActiveElement();
				if (activeElement.shouldUpdateOnEvent(event)) {
					activeElement.updateSelfY(client);
					updatedElementsY.add(this);
					selfUpdated = true;
				}
			}
		}

		for (AbstractContainerNode child : yTreeChildren) {
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
