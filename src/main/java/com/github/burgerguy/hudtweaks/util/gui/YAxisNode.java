package com.github.burgerguy.hudtweaks.util.gui;

import java.util.Set;

import net.minecraft.client.MinecraftClient;

public interface YAxisNode {
	public String getIdentifier();
	
	public YAxisNode getYParent();
	public Set<YAxisNode> getYChildren();
	public void moveYUnder(YAxisNode newYParent);
	
	public int getY(MinecraftClient client);
	public int getHeight(MinecraftClient client);
	
	public void updateY(MinecraftClient client, boolean parentUpdated, Set<YAxisNode> updatedElements);
}
