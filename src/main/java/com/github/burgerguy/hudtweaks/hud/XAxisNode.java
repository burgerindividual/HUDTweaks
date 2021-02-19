package com.github.burgerguy.hudtweaks.hud;

import java.util.Set;

import com.github.burgerguy.hudtweaks.hud.element.HTIdentifier;

import net.minecraft.client.MinecraftClient;

public interface XAxisNode {	
	public HTIdentifier getIdentifier();
	
	public XAxisNode getXParent();
	public Set<XAxisNode> getXChildren();
	public void moveXUnder(XAxisNode newXParent);
	
	public double getX(MinecraftClient client);
	public double getWidth(MinecraftClient client);
	
	public void tryUpdateX(UpdateEvent event, MinecraftClient client, boolean parentUpdated, Set<XAxisNode> updatedElementsX);
}
