package com.github.burgerguy.hudtweaks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.MinecraftClient;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
	@Inject(method = "onResolutionChanged", at = @At(value = "TAIL"))
	private void onResolutionChanged() {
		//MatrixCache.requiresUpdate = true;
	}
}
