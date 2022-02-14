package com.github.burgerguy.hudtweaks.mixin;

import com.github.burgerguy.hudtweaks.gui.HTOptionsScreen;
import com.github.burgerguy.hudtweaks.hud.HudContainer;
import com.github.burgerguy.hudtweaks.hud.UpdateEvent;
import com.github.burgerguy.hudtweaks.hud.element.*;
import com.github.burgerguy.hudtweaks.hud.tree.AbstractContainerNode;
import com.github.burgerguy.hudtweaks.util.RenderStateUtil;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin extends DrawableHelper {
	@Final
	@Shadow
	private MinecraftClient client;

	// the updatedElements sets are used to see what matrices should be updated after
	@Unique
	private final Set<AbstractContainerNode> updatedElementsX = new HashSet<>();
	@Unique
	private final Set<AbstractContainerNode> updatedElementsY = new HashSet<>();

	@Inject(method = "render", at = @At(value = "HEAD"))
	private void renderStart(MatrixStack matrices, float tickDelta, CallbackInfo callbackInfo) {
		if (HTOptionsScreen.isOpen()) {
			// super janky way to dim background
			super.fillGradient(matrices, 0, 0, client.getWindow().getScaledWidth(), client.getWindow().getScaledHeight(), 0xC0101010, 0xD0101010);
		}

		client.getProfiler().push("fireHudTweaksEvents");
		updatedElementsX.clear();
		updatedElementsY.clear();
		// try manual update first
		HudContainer.getScreenRoot().tryUpdateX(null, client, false, updatedElementsX);
		HudContainer.getScreenRoot().tryUpdateY(null, client, false, updatedElementsY);
		for (UpdateEvent event : HudContainer.getEventRegistry().getAllEvents()) {
			if (event.shouldUpdate(client)) {
				HudContainer.getScreenRoot().tryUpdateX(event, client, false, updatedElementsX);
				HudContainer.getScreenRoot().tryUpdateY(event, client, false, updatedElementsY);
			}
		}

		for (AbstractContainerNode container : Sets.union(updatedElementsX, updatedElementsY)) {
			if (container instanceof HudElementContainer hudElementContainer) {
				hudElementContainer.setupActiveMatrix();
			}
			container.setUpdated();
		}
		client.getProfiler().pop();
	}

	@Inject(method = "renderHotbar", at = @At(value = "HEAD"))
	private void renderHotbarHead(float tickDelta, MatrixStack matrices, CallbackInfo callbackInfo) {
		RenderStateUtil.tryStartRender(DefaultHotbarElement.IDENTIFIER, RenderSystem.getModelViewStack(), matrices);
	}

	@Inject(method = "renderHotbar", at = @At(value = "RETURN"))
	private void renderHotbarReturn(float tickDelta, MatrixStack matrices, CallbackInfo callbackInfo) {
		RenderStateUtil.tryFinishRender(DefaultHotbarElement.IDENTIFIER, RenderSystem.getModelViewStack(), matrices);
		RenderSystem.applyModelViewMatrix();
	}

	@Inject(method = "renderStatusBars",
			at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
			args = "ldc=armor"))
	private void renderArmor(MatrixStack matrices, CallbackInfo callbackInfo) {
		RenderStateUtil.tryStartRender(DefaultArmorElement.IDENTIFIER, matrices);
	}

	@Inject(method = "renderStatusBars",
			at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
			args = "ldc=health"))
	private void renderHealth(MatrixStack matrices, CallbackInfo callbackInfo) {
		RenderStateUtil.tryFinishRender(DefaultArmorElement.IDENTIFIER, matrices);
		RenderStateUtil.tryStartRender(DefaultHealthElement.IDENTIFIER, matrices);
	}

	@Inject(method = "renderStatusBars",
			at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/gui/hud/InGameHud;getHeartCount(Lnet/minecraft/entity/LivingEntity;)I"))
	private void renderHunger(MatrixStack matrices, CallbackInfo callbackInfo) {
		RenderStateUtil.tryFinishRender(DefaultHealthElement.IDENTIFIER, matrices);
		RenderStateUtil.tryStartRender(DefaultHungerElement.IDENTIFIER, matrices);
	}

	@Inject(method = "renderStatusBars",
			at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
			args = "ldc=air"))
	private void renderAir(MatrixStack matrices, CallbackInfo callbackInfo) {
		RenderStateUtil.tryFinishRender(DefaultHungerElement.IDENTIFIER, matrices);
		RenderStateUtil.tryStartRender(DefaultAirElement.IDENTIFIER, matrices);
	}

	@Inject(method = "renderStatusBars", at = @At(value = "RETURN"))
	private void renderStatusBarsReturn(MatrixStack matrices, CallbackInfo callbackInfo) {
		RenderStateUtil.tryFinishRender(DefaultAirElement.IDENTIFIER, matrices);
	}

	@Inject(method = "renderMountHealth", at = @At(value = "HEAD"))
	private void renderMountHealthHead(MatrixStack matrices, CallbackInfo callbackInfo) {
		RenderStateUtil.tryStartRender(DefaultMountHealthElement.IDENTIFIER, matrices);
	}

	@Inject(method = "renderMountHealth", at = @At(value = "RETURN"))
	private void renderMountHealthReturn(MatrixStack matrices, CallbackInfo callbackInfo) {
		RenderStateUtil.tryFinishRender(DefaultMountHealthElement.IDENTIFIER, matrices);
	}

	@Inject(method = "renderExperienceBar", at = @At(value = "HEAD"))
	public void renderExperienceBarHead(MatrixStack matrices, int x, CallbackInfo callbackInfo) {
		RenderStateUtil.tryStartRender(DefaultExperienceBarElement.IDENTIFIER, matrices);
	}

	@Inject(method = "renderExperienceBar", at = @At(value = "RETURN"))
	public void renderExperienceBarReturn(MatrixStack matrices, int x, CallbackInfo callbackInfo) {
		RenderStateUtil.tryFinishRender(DefaultExperienceBarElement.IDENTIFIER, matrices);
	}

	@Shadow
	public abstract void renderExperienceBar(MatrixStack matrices, int x);

	@Inject(method = "renderMountJumpBar", at = @At(value = "HEAD"))
	public void renderMountJumpBarHead(MatrixStack matrices, int x, CallbackInfo callbackInfo) {
		// Typically, when the jump bar is visible it hides the experience bar. When
		// the exp bar is set to force display, we can just do it here as the only
		// time it's hidden (aside from F1) is when the jump bar is rendering.
		HudElement activeExpBarElement = HudContainer.getElementRegistry().getActiveElement(DefaultExperienceBarElement.IDENTIFIER);
		if (activeExpBarElement instanceof DefaultExperienceBarElement && ((DefaultExperienceBarElement) activeExpBarElement).getForceDisplay()) {
			renderExperienceBar(matrices, x);
		}

		RenderStateUtil.tryStartRender(DefaultJumpBarElement.IDENTIFIER, matrices);
	}

	@Inject(method = "renderMountJumpBar", at = @At(value = "RETURN"))
	public void renderMountJumpBarReturn(MatrixStack matrices, int x, CallbackInfo callbackInfo) {
		RenderStateUtil.tryFinishRender(DefaultJumpBarElement.IDENTIFIER, matrices);
	}

	@Inject(method = "renderStatusEffectOverlay", at = @At(value = "HEAD"))
	private void renderStatusEffectOverlayHead(MatrixStack matrices, CallbackInfo callbackInfo) {
		RenderStateUtil.tryStartRender(DefaultStatusEffectsElement.IDENTIFIER, matrices);
	}
	
	@Inject(method = "renderStatusEffectOverlay", at = @At(value = "RETURN"))
	private void renderStatusEffectOverlayReturn(MatrixStack matrices, CallbackInfo callbackInfo) {
		RenderStateUtil.tryFinishRender(DefaultStatusEffectsElement.IDENTIFIER, matrices);
	}

	@Inject(method = "renderHeldItemTooltip", at = @At(value = "HEAD"))
	private void renderTooltipHead(MatrixStack matrices, CallbackInfo callbackInfo) {
		RenderStateUtil.tryStartRender(DefaultTooltipElement.IDENTIFIER, matrices);
	}

	@Inject(method = "renderHeldItemTooltip", at = @At(value = "RETURN"))
	private void renderTooltipReturn(MatrixStack matrices, CallbackInfo callbackInfo) {
		RenderStateUtil.tryFinishRender(DefaultTooltipElement.IDENTIFIER, matrices);
	}

	@Inject(method = "render",
			at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTextBackground(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;III)V",
			ordinal = 0))
	private void renderActionBarStart(MatrixStack matrices, float tickDelta, CallbackInfo callbackInfo) {
		RenderStateUtil.tryStartRender(DefaultActionBarElement.IDENTIFIER, matrices);
	}

	@Inject(method = "render",
			at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I",
			shift = At.Shift.AFTER))
	private void renderActionBarEnd(MatrixStack matrices, float tickDelta, CallbackInfo callbackInfo) {
		RenderStateUtil.tryFinishRender(DefaultActionBarElement.IDENTIFIER, matrices);
	}

	@Inject(method = "render",
			at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTextBackground(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;III)V",
			ordinal = 1))
	private void renderTitleStart(MatrixStack matrices, float tickDelta, CallbackInfo callbackInfo) {
		RenderStateUtil.tryStartRender(DefaultTitleElement.IDENTIFIER, matrices);
	}

	@Inject(method = "render",
			at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I",
			ordinal = 0,
			shift = At.Shift.AFTER))
	private void renderTitleEnd(MatrixStack matrices, float tickDelta, CallbackInfo callbackInfo) {
		RenderStateUtil.tryFinishRender(DefaultTitleElement.IDENTIFIER, matrices);
	}

	@Inject(method = "render",
			at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTextBackground(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;III)V",
			ordinal = 2))
	private void renderSubtitleStart(MatrixStack matrices, float tickDelta, CallbackInfo callbackInfo) {
		RenderStateUtil.tryStartRender(DefaultSubtitleElement.IDENTIFIER, matrices);
	}

	@Inject(method = "render",
			at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I",
			ordinal = 1,
			shift = At.Shift.AFTER))
	private void renderSubtitleEnd(MatrixStack matrices, float tickDelta, CallbackInfo callbackInfo) {
		RenderStateUtil.tryFinishRender(DefaultSubtitleElement.IDENTIFIER, matrices);
	}
}
