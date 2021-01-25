package com.github.burgerguy.hudtweaks.hud;

import java.util.Set;

import net.minecraft.client.MinecraftClient;

public interface YAxisNode {
	public String getIdentifier();
	
	public YAxisNode getYParent();
	public Set<YAxisNode> getYChildren();
	public void moveYUnder(YAxisNode newYParent);
	
	public double getY(MinecraftClient client);
	public double getHeight(MinecraftClient client);
	
	public void tryUpdateY(UpdateEvent event, MinecraftClient client, boolean parentUpdated, Set<YAxisNode> updatedElementsY);
}
