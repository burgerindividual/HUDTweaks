package com.github.burgerguy.hudtweaks.hud;

import net.minecraft.client.MinecraftClient;

public interface UpdateEvent {
	public String getIdentifier();
	public boolean shouldUpdate(MinecraftClient client);
}
