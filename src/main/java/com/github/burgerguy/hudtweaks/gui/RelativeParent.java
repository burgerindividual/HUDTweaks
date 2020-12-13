package com.github.burgerguy.hudtweaks.gui;

import java.util.Set;

import com.github.burgerguy.hudtweaks.util.gui.MatrixCache.UpdateEvent;

import net.minecraft.client.MinecraftClient;

public interface RelativeParent {
	public String getIdentifier();
	public int getPosition(MinecraftClient client);
	public int getDimension(MinecraftClient client);
	public Set<UpdateEvent> getUpdateEvents();
}
