package com.github.burgerguy.hudtweaks.util.gui;

import java.util.Set;

import com.github.burgerguy.hudtweaks.util.gui.MatrixCache.UpdateEvent;

import net.minecraft.client.MinecraftClient;

public interface XAxisNode {	
	public String getIdentifier();
	
	public XAxisNode getXParent();
	public Set<XAxisNode> getXChildren();
	public void moveXUnder(XAxisNode newXParent);
	
	public int getX(MinecraftClient client);
	public int getWidth(MinecraftClient client);
	
	public void updateX(UpdateEvent event, MinecraftClient client);
}
