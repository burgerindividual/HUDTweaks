package com.github.burgerguy.hudtweaks.mixin;

import com.github.burgerguy.hudtweaks.hud.element.DefaultBossBarElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.burgerguy.hudtweaks.hud.HudContainer;

import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(BossBarHud.class)
public abstract class BossBarHudMixin {

	@Inject(method = "render", at = @At(value = "HEAD"))
	private void renderBossBarHead(MatrixStack matrices, CallbackInfo ci) {
		HudContainer.getMatrixCache().tryPushMatrix(DefaultBossBarElement.IDENTIFIER, matrices);
	}

	@Inject(method = "render", at = @At(value = "RETURN"))
	private void renderBossBarReturn(MatrixStack matrices, CallbackInfo ci) {
		HudContainer.getMatrixCache().tryPopMatrix(DefaultBossBarElement.IDENTIFIER, matrices);
	}
}
