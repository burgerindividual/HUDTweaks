package com.github.burgerguy.hudtweaks.mixin;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.github.burgerguy.hudtweaks.gui.HTOptionsScreen;
import com.github.burgerguy.hudtweaks.hud.HudContainer;
import com.github.burgerguy.hudtweaks.hud.UpdateEvent;
import com.github.burgerguy.hudtweaks.hud.XAxisNode;
import com.github.burgerguy.hudtweaks.hud.YAxisNode;
import com.github.burgerguy.hudtweaks.hud.element.ExperienceBarElement;
import com.github.burgerguy.hudtweaks.hud.element.HealthElement;
import com.github.burgerguy.hudtweaks.hud.element.HudElement;
import com.github.burgerguy.hudtweaks.hud.element.HungerElement;
import com.github.burgerguy.hudtweaks.hud.element.StatusEffectsElement;
import com.google.common.collect.Sets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin extends DrawableHelper {
	@Final
	@Shadow
	private MinecraftClient client;
	
	// the updatedElements sets are used to see what matricies should be updated after
	@Unique
	private final Set<XAxisNode> updatedElementsX = new HashSet<>();
	@Unique
	private final Set<YAxisNode> updatedElementsY = new HashSet<>();
	
	@Inject(method = "render", at = @At(value = "HEAD"))
	private void renderStart(MatrixStack matrices, float tickDelta, CallbackInfo callbackInfo) {
		int scaledWidth = client.getWindow().getScaledWidth();
		int scaledHeight = client.getWindow().getScaledHeight();
		
		if (HTOptionsScreen.isOpen()) {
			// super janky way to dim background
			super.fillGradient(matrices, 0, 0, scaledWidth, scaledHeight, 0xC0101010, 0xD0101010);
		}
		
		for (HudElement element : HudContainer.getElements()) {
			// allows us to keep track of what has rendered this frame
			element.clearDrawTest();
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
		
		for (Object element : Sets.union(updatedElementsX, updatedElementsY)) {
			// something something instanceof bad something something
			if (element instanceof HudElement) {
				HudElement hudElement = (HudElement) element;
				HudContainer.getMatrixCache().putMatrix(hudElement.getIdentifier(), hudElement.createMatrix(client));
			}
		}
		client.getProfiler().pop();
	}
	
	@Inject(method = "renderHotbar", at = @At(value = "HEAD"))
	private void renderHotbarHead(float tickDelta, MatrixStack matrices, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPushMatrix("hotbar", null);
	}
	
	@Inject(method = "renderHotbar", at = @At(value = "RETURN"))
	private void renderHotbarReturn(float tickDelta, MatrixStack matrices, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPopMatrix("hotbar", null);
	}
	
	@Inject(method = "renderStatusBars",
			at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
			args = "ldc=armor"))
	private void renderArmor(MatrixStack matrices, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPushMatrix("armor", matrices);
	}
	
	@Inject(method = "renderStatusBars",
			at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
			args = "ldc=health"))
	private void renderHealth(MatrixStack matrices, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPopMatrix("armor", matrices);
		HudContainer.getMatrixCache().tryPushMatrix("health", matrices);
	}
	
	// injects before if (i <= 4)
	// this reverses the negation it does right before
	@ModifyVariable(method = "renderStatusBars",
			ordinal = 19,
			at = @At(value = "JUMP", opcode = Opcodes.IF_ICMPGT))
	private int flipHealthStackDirection(int healthPos) {
		if (((HealthElement) HudContainer.getActiveElement("health")).isFlipped()) {
			int originalHealthPos = client.getWindow().getScaledHeight() - 39;
			return originalHealthPos + originalHealthPos - healthPos;
		} else {
			return healthPos;
		}
	}
	
	// this makes sure the aa will equal zero so the if (aa == 0) will pass
	@ModifyVariable(method = "renderStatusBars",
			ordinal = 11,
			at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/gui/hud/InGameHud;getHeartCount(Lnet/minecraft/entity/LivingEntity;)I"))
	private int forceRenderHunger(int mountHealth) {
		if (((HungerElement) HudContainer.getActiveElement("hunger")).getForceDisplay()) {
			return 0;
		} else {
			return mountHealth;
		}
	}
	
	@Inject(method = "renderStatusBars",
			at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/gui/hud/InGameHud;getHeartCount(Lnet/minecraft/entity/LivingEntity;)I"))
	private void renderHunger(MatrixStack matrices, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPopMatrix("health", matrices);
		HudContainer.getMatrixCache().tryPushMatrix("hunger", matrices);
	}
	
	@Inject(method = "renderStatusBars",
			at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
			args = "ldc=air"))
	private void renderAir(MatrixStack matrices, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPopMatrix("hunger", matrices);
		HudContainer.getMatrixCache().tryPushMatrix("air", matrices);
	}
	
	@Inject(method = "renderStatusBars", at = @At(value = "RETURN"))
	private void renderStatusBarsReturn(MatrixStack matrices, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPopMatrix("air", matrices);
	}
	
	@Inject(method = "renderMountHealth", at = @At(value = "HEAD"))
	private void renderMountHealthHead(MatrixStack matrices, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPushMatrix("mounthealth", matrices);
	}
	
	@Inject(method = "renderMountHealth", at = @At(value = "RETURN"))
	private void renderMountHealthReturn(MatrixStack matrices, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPopMatrix("mounthealth", matrices);
	}
	
	@Inject(method = "renderExperienceBar", at = @At(value = "HEAD"))
	public void renderExperienceBarHead(MatrixStack matrices, int x, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPushMatrix("expbar", matrices);
	}
	
	@Inject(method = "renderExperienceBar", at = @At(value = "RETURN"))
	public void renderExperienceBarReturn(MatrixStack matrices, int x, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPopMatrix("expbar", matrices);
	}
	
	@Shadow
	public abstract void renderExperienceBar(MatrixStack matrices, int x);
	
	@Inject(method = "renderMountJumpBar", at = @At(value = "HEAD"))
	public void renderMountJumpBarHead(MatrixStack matrices, int x, CallbackInfo callbackInfo) {
		// Typically, when the jump bar is visible it hides the experience bar. When
		// the exp bar is set to force display, we can just do it here as the only
		// time it's hidden (aside from F1) is when the jump bar is rendering.
		if (((ExperienceBarElement) HudContainer.getActiveElement("expbar")).getForceDisplay()) {
			renderExperienceBar(matrices, x);
		}
		
		HudContainer.getMatrixCache().tryPushMatrix("jumpbar", matrices);
	}
	
	@Inject(method = "renderMountJumpBar", at = @At(value = "RETURN"))
	public void renderMountJumpBarReturn(MatrixStack matrices, int x, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPopMatrix("jumpbar", matrices);
	}
	
	@Inject(method = "renderStatusEffectOverlay", at = @At(value = "HEAD"))
	private void renderStatusEffectOverlayHead(MatrixStack matrices, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPushMatrix("statuseffects", matrices);
	}

	@Unique
	private static final int STATUS_EFFECT_OFFSET = 25;
	@Unique
	private int preX;
	@Unique
	private int preY;
	@Unique
	private int postX;
	@Unique
	private int postY;

	@Inject(method = "renderStatusEffectOverlay",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffect;isBeneficial()Z"),
			locals = LocalCapture.CAPTURE_FAILHARD)
	private void setupPreCalcVars(MatrixStack matrices, CallbackInfo callbackInfo,
			Collection<?> u1, int u2, int u3, StatusEffectSpriteManager u4, List<?> u5, Iterator<?> u6, StatusEffectInstance u7, StatusEffect u8, // unused vars
			int x, int y) {
		this.preX = x;
		this.preY = y;
	}

	@Inject(method = "renderStatusEffectOverlay",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffectInstance;isAmbient()Z"),
			locals = LocalCapture.CAPTURE_FAILHARD)
	private void setupPostCalcVars(MatrixStack matrices, CallbackInfo callbackInfo,
			Collection<?> u1, int u2, int u3, StatusEffectSpriteManager u4, List<?> u5, Iterator<?> u6, StatusEffectInstance u7, StatusEffect u8, // unused vars
			int x, int y) {
		this.postX = x;
		this.postY = y;
	}

	@ModifyVariable(method = "renderStatusEffectOverlay",
					ordinal = 2,
					at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffectInstance;isAmbient()Z"))
	private int modifyStatusEffectX(int x, MatrixStack maxtixStack) {
		if (((StatusEffectsElement) HudContainer.getActiveElement("statuseffects")).isVertical()) {
			return client.getWindow().getScaledWidth() - STATUS_EFFECT_OFFSET + preY - postY;
		} else {
			return x;
		}
	}

	@ModifyVariable(method = "renderStatusEffectOverlay",
					ordinal = 3,
					at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffectInstance;isAmbient()Z"))
	private int modifyStatusEffectY(int y, MatrixStack maxtixStack) {
		if (((StatusEffectsElement) HudContainer.getActiveElement("statuseffects")).isVertical()) {
			return preY + client.getWindow().getScaledWidth() - postX - STATUS_EFFECT_OFFSET;
		} else {
			return y;
		}
	}

	@Inject(method = "renderStatusEffectOverlay", at = @At(value = "RETURN"))
	private void renderStatusEffectOverlayReturn(MatrixStack matrices, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPopMatrix("statuseffects", matrices);
	}
}
