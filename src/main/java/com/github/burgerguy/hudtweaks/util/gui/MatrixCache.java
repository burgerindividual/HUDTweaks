package com.github.burgerguy.hudtweaks.util.gui;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import com.github.burgerguy.hudtweaks.gui.HudContainer;
import com.github.burgerguy.hudtweaks.gui.HudElement;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Matrix4f;

public class MatrixCache {	
	private final Map<String, Matrix4f> matrixMap = new HashMap<>();
	private final Queue<HudElement> updateQueue = new ArrayDeque<>();
	
	public void calculateMatrix(HudElement element, MinecraftClient client) {
		matrixMap.put(element.getIdentifier(), element.calculateMatrix(client));
		updateQueue.remove(element);
	}
	
	public void calculateMatrix(String identifier, MinecraftClient client) {
		calculateMatrix(HudContainer.getElement(identifier), client);
	}
	
	public void calculateAllMatricies(MinecraftClient client) {
		for(HudElement element : HudContainer.getElements()) {
			calculateMatrix(element, client);
		}
	}
	
	public Matrix4f getMatrix(String identifier) {
		return matrixMap.get(identifier);
	}
	
	public void calculateQueued(MinecraftClient client) {
		while(!updateQueue.isEmpty()) {
			HudElement element = updateQueue.poll();
			matrixMap.put(element.getIdentifier(), element.calculateMatrix(client));
		}
	}
	
	public void queueUpdate(HudElement element) {
		updateQueue.add(element);
	}
	
	/**
	 * Each HudElement can have multiple update events, which determines if the matrix should be updated.
	 */
	public enum UpdateEvent {
		ON_RENDER,
		ON_HEALTH_ROWS_CHANGE,
		ON_SCREEN_BOUNDS_CHANGE,
		ON_RIDING_HEALTH_ROWS_CHANGE,
		ON_OFFHAND_STATUS_CHANGE,
		ON_STATUS_EFFECTS_CHANGE
		// TODO: add update events for exp/jump bar switch, food/ridable health switch, etc.
	}
}
