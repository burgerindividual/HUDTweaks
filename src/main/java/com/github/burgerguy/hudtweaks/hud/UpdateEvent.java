package com.github.burgerguy.hudtweaks.hud;

import net.minecraft.client.MinecraftClient;

public interface UpdateEvent {
	String getIdentifier();
	boolean shouldUpdate(MinecraftClient client);
}
