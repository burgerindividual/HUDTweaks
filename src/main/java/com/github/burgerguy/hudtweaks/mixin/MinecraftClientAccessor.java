package com.github.burgerguy.hudtweaks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.MinecraftClient;

@Mixin(MinecraftClient.class)
public interface MinecraftClientAccessor {
	@Accessor("pausedTickDelta")
	float getPausedTickDelta();
}
