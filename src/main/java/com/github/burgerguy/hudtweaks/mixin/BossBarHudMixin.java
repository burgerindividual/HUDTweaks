package com.github.burgerguy.hudtweaks.mixin;

import com.github.burgerguy.hudtweaks.hud.element.DefaultBossBarElement;
import com.github.burgerguy.hudtweaks.util.RenderStateUtil;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossBarHud.class)
public abstract class BossBarHudMixin {

	@Inject(method = "render", at = @At(value = "HEAD"))
	private void renderBossBarHead(MatrixStack matrices, CallbackInfo ci) {
		RenderStateUtil.startRender(DefaultBossBarElement.IDENTIFIER, matrices);
	}

	@Inject(method = "render", at = @At(value = "RETURN"))
	private void renderBossBarReturn(MatrixStack matrices, CallbackInfo ci) {
		RenderStateUtil.finishRender(DefaultBossBarElement.IDENTIFIER, matrices);
	}
}
