package com.github.burgerguy.hudtweaks.util.gui;

import net.minecraft.client.MinecraftClient;

public interface UpdateEvent {
	public String getIdentifier();
	public boolean shouldUpdate(MinecraftClient client);
}
