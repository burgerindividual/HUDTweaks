package com.github.burgerguy.hudtweaks.mixin;

import java.util.Arrays;
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
import com.github.burgerguy.hudtweaks.gui.HudContainer;
import com.github.burgerguy.hudtweaks.gui.HudElement;
import com.github.burgerguy.hudtweaks.gui.element.HealthElement;
import com.github.burgerguy.hudtweaks.gui.element.StatusEffectsElement;
import com.github.burgerguy.hudtweaks.util.gui.MatrixCache.UpdateEvent;
import com.google.common.collect.Sets;
import com.github.burgerguy.hudtweaks.util.gui.XAxisNode;
import com.github.burgerguy.hudtweaks.util.gui.YAxisNode;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.options.AttackIndicator;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Matrix4f;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin extends DrawableHelper {
	@Final
	@Shadow
	private MinecraftClient client;
	
	// the excludedElements sets are used to stop redundant updating
	@Unique
	private final Set<XAxisNode> excludedElementsX = new HashSet<>();
	@Unique
	private final Set<YAxisNode> excludedElementsY = new HashSet<>();
	// the updatedElements sets are used to see what matricies should be updated after an event
	@Unique
	private final Set<XAxisNode> updatedElementsX = new HashSet<>();
	@Unique
	private final Set<YAxisNode> updatedElementsY = new HashSet<>();
	@Unique
	private int lastWidth;
	@Unique
	private int lastHeight;
	@Unique
	private int lastHeartRows;
	@Unique
	private int lastRidingHeartRows;
	@Unique
	private boolean lastOffhandStatus;
	@Unique
	private StatusEffect[] lastStatusEffects;
	@Unique
	private AttackIndicator lastIndicator;
	
	@Unique
	private boolean multipliedMatrix;
	
	@Inject(method = "render", at = @At(value = "HEAD"))
	private void renderStart(MatrixStack matrixStack, float tickDelta, CallbackInfo callbackInfo) {
		int scaledWidth = client.getWindow().getScaledWidth();
		int scaledHeight = client.getWindow().getScaledHeight();
		
		if (HTOptionsScreen.isOpen()) {
			// super janky way to dim background
			super.fillGradient(matrixStack, 0, 0, scaledWidth, scaledHeight, -1072689136, -804253680);
		}
		
		excludedElementsX.clear();
		excludedElementsY.clear();
		
		fireUpdateEvent(UpdateEvent.ON_RENDER);
		
		if (scaledWidth != lastWidth || scaledHeight != lastHeight) {
			lastWidth = scaledWidth;
			lastHeight = scaledHeight;
			fireUpdateEvent(UpdateEvent.ON_SCREEN_BOUNDS_CHANGE);
		}
		
		Entity cameraEntity = client.getCameraEntity();
		if (cameraEntity != null && cameraEntity instanceof PlayerEntity) {
			boolean offhandStatus = ((PlayerEntity) cameraEntity).getOffHandStack().isEmpty();
			if (offhandStatus != lastOffhandStatus) {
				lastOffhandStatus = offhandStatus;
				fireUpdateEvent(UpdateEvent.ON_OFFHAND_STATUS_CHANGE);
			}
		}
		
		Collection<StatusEffectInstance> effectInstances = client.player.getStatusEffects();
		StatusEffect[] statusEffects = new StatusEffect[effectInstances.size()];
		int i = 0;
		for(StatusEffectInstance effectInstance : effectInstances) {
			statusEffects[i++] = effectInstance.getEffectType();
		}
		if (lastStatusEffects == null || !Arrays.deepEquals(lastStatusEffects, statusEffects)) {
			lastStatusEffects = statusEffects;
			fireUpdateEvent(UpdateEvent.ON_STATUS_EFFECTS_CHANGE);
		}
		
		AttackIndicator currentIndicator = client.options.attackIndicator;
		if (lastIndicator == null ||
			(!currentIndicator.equals(lastIndicator) &&
			(currentIndicator.equals(AttackIndicator.HOTBAR) || lastIndicator.equals(AttackIndicator.HOTBAR)))) {
			lastIndicator = currentIndicator;
			fireUpdateEvent(UpdateEvent.ON_HOTBAR_ATTACK_INDICATOR_CHANGE);
		}
	}
	
	@Unique
	private void fireUpdateEvent(UpdateEvent event) {
		client.getProfiler().push("fireHudTweaksEvents");
		client.getProfiler().push(event.toString());
		updatedElementsX.clear();
		updatedElementsY.clear();
		HudContainer.getScreenRoot().updateX(event, client, false, excludedElementsX, updatedElementsX);
		HudContainer.getScreenRoot().updateY(event, client, false, excludedElementsY, updatedElementsY);
		for (Object element : Sets.union(updatedElementsX, updatedElementsY)) {
			if (element instanceof HudElement) {
				HudElement hudElement = (HudElement) element;
				HudContainer.getMatrixCache().putMatrix(hudElement.getIdentifier(), hudElement.createMatrix(client));
			}
		}
		client.getProfiler().pop();
		client.getProfiler().pop();
	}
	
	@Inject(method = "renderHotbar", at = @At(value = "HEAD"))
	private void renderHotbarHead(float tickDelta, MatrixStack matrixStack, CallbackInfo callbackInfo) {
		Matrix4f hotbarMatrix = HudContainer.getMatrixCache().getMatrix("hotbar");
		if (hotbarMatrix != null) {
			multipliedMatrix = true;
			RenderSystem.pushMatrix();
			RenderSystem.multMatrix(hotbarMatrix);
		}
	}
	
	@Inject(method = "renderHotbar", at = @At(value = "RETURN"))
	private void renderHotbarReturn(float tickDelta, MatrixStack matrixStack, CallbackInfo callbackInfo) {
		if (multipliedMatrix) {
			RenderSystem.popMatrix();
			multipliedMatrix = false;
		}
	}
	
	//	@Inject(method = "renderHotbarItem", at = @At(value = "HEAD"))
	//	private void renderHotbarItemHead(CallbackInfo callbackInfo) {
	//	}
	//
	//	@Inject(method = "renderHotbarItem", at = @At(value = "RETURN"))
	//	private void renderHotbarItemReturn(CallbackInfo callbackInfo) {
	//	}
	
	@Inject(method = "renderStatusBars",
			at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(II)I"),
			locals = LocalCapture.CAPTURE_FAILSOFT)//Math.max
	private void checkHealthRows(MatrixStack matrixStack, CallbackInfo callbackInfo,
			PlayerEntity u1, int u2, boolean u3, long u4, int u5, HungerManager u6, int u7, int u8, int u9, int u10, float u11, int u12,
			int heartRows) {
		if (heartRows != lastHeartRows) {
			lastHeartRows = heartRows;
			fireUpdateEvent(UpdateEvent.ON_HEALTH_ROWS_CHANGE);
		}
	}
	
	@Inject(method = "renderStatusBars",
			at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
			args = "ldc=armor"))
	private void renderArmor(MatrixStack matrixStack, CallbackInfo callbackInfo) {
		Matrix4f armorMatrix = HudContainer.getMatrixCache().getMatrix("armor");
		if (armorMatrix != null) {
			multipliedMatrix = true;
			matrixStack.push();
			matrixStack.peek().getModel().multiply(armorMatrix);
		}
	}
	
	@Inject(method = "renderStatusBars",
			at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
			args = "ldc=health"))
	private void renderHealth(MatrixStack matrixStack, CallbackInfo callbackInfo) {
		if (multipliedMatrix) {
			matrixStack.pop();
			multipliedMatrix = false;
		}
		
		Matrix4f healthMatrix = HudContainer.getMatrixCache().getMatrix("health");
		if (healthMatrix != null) {
			multipliedMatrix = true;
			matrixStack.push();
			matrixStack.peek().getModel().multiply(healthMatrix);
		}
	}
	
	// injects before if (i <= 4)
	// this reverses the negation it does right before
	@ModifyVariable(method = "renderStatusBars",
			ordinal = 19,
			at = @At(value = "JUMP", opcode = Opcodes.IF_ICMPGT))
	private int flipHealthStackDirection(int healthPos) {
		if (((HealthElement) HudContainer.getElement("health")).isFlipped()) {
			int originalHealthPos = client.getWindow().getScaledHeight() - 39;
			return originalHealthPos + originalHealthPos - healthPos;
		} else {
			return healthPos;
		}
	}
	
	@Shadow
	protected abstract int getHeartRows(int heartCount);
	
	@Inject(method = "renderStatusBars",
			at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/gui/hud/InGameHud;getHeartCount(Lnet/minecraft/entity/LivingEntity;)I"),
			locals = LocalCapture.CAPTURE_FAILSOFT)
	private void renderHungerAndCheckRidingHealth(MatrixStack matrixStack, CallbackInfo callbackInfo,
			PlayerEntity u1, int u2, boolean u3, long u4, int u5, HungerManager u6, int u7, int u8, int u9, int u10, float u11, int u12, int u13, int u14, int u15, int u16, LivingEntity u17,
			int ridingHealth) {
		if (multipliedMatrix) {
			matrixStack.pop();
			multipliedMatrix = false;
		}
		
		int ridingHeartRows = getHeartRows(ridingHealth);
		if (lastRidingHeartRows != ridingHeartRows) {
			lastRidingHeartRows = ridingHeartRows;
			fireUpdateEvent(UpdateEvent.ON_RIDING_HEALTH_ROWS_CHANGE);
		}
		
		Matrix4f hungerMatrix = HudContainer.getMatrixCache().getMatrix("hunger");
		if (hungerMatrix != null) {
			multipliedMatrix = true;
			matrixStack.push();
			matrixStack.peek().getModel().multiply(hungerMatrix);
		}
	}
	
	@Inject(method = "renderStatusBars",
			at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
			args = "ldc=air"))
	private void renderAir(MatrixStack matrixStack, CallbackInfo callbackInfo) {
		if (multipliedMatrix) {
			matrixStack.pop();
			multipliedMatrix = false;
		}
		
		Matrix4f airMatrix = HudContainer.getMatrixCache().getMatrix("air");
		if (airMatrix != null) {
			multipliedMatrix = true;
			matrixStack.push();
			matrixStack.peek().getModel().multiply(airMatrix);
		}
	}
	
	@Inject(method = "renderStatusBars", at = @At(value = "RETURN"))
	private void renderStatusBarsReturn(MatrixStack matrixStack, CallbackInfo callbackInfo) {
		if (multipliedMatrix) {
			matrixStack.pop();
			multipliedMatrix = false;
		}
	}
	
	//	@Inject(method = "renderMountHealth", at = @At(value = "HEAD"))
	//	private void renderMountHealthHead(MatrixStack matrixStack, CallbackInfo callbackInfo) {
	//		if (HudContainer.getMatrixCache().mountTransform != null) {
	//			tempMatrix = matrixStack.peek().getModel();
	//			tempMatrix.multiply(HudContainer.getMatrixCache().mountTransform);
	//		} else if (HudContainer.getMatrixCache().foodTransform != null) {
	//			tempMatrix = matrixStack.peek().getModel();
	//			tempMatrix.multiply(HudContainer.getMatrixCache().foodTransform);
	//		}
	//	}
	//
	//	@Inject(method = "renderMountHealth", at = @At(value = "RETURN"))
	//	private void renderMountHealthReturn(MatrixStack matrixStack, CallbackInfo callbackInfo) {
	//		if (HudContainer.getMatrixCache().mountTransform != null) {
	//			tempMatrix.multiply(HudTweaksOptions.mountTransform.getInverse());
	//		} else if (HudContainer.getMatrixCache().foodTransform != null) {
	//			tempMatrix.multiply(HudTweaksOptions.foodTransform.getInverse());
	//		}
	//	}
	//
	@Inject(method = "renderExperienceBar", at = @At(value = "HEAD"))
	public void renderExperienceBarHead(MatrixStack matrixStack, int x, CallbackInfo callbackInfo) {
		Matrix4f expBarMatrix = HudContainer.getMatrixCache().getMatrix("expbar");
		if (expBarMatrix != null) {
			multipliedMatrix = true;
			matrixStack.push();
			matrixStack.peek().getModel().multiply(expBarMatrix);
		}
	}
	
	@Inject(method = "renderExperienceBar", at = @At(value = "RETURN"))
	public void renderExperienceBarReturn(MatrixStack matrixStack, int x, CallbackInfo callbackInfo) {
		if (multipliedMatrix) {
			matrixStack.pop();
			multipliedMatrix = false;
		}
	}
	//
	//	@Inject(method = "renderMountJumpBar", at = @At(value = "HEAD"))
	//	public void renderMountJumpBarHead(MatrixStack matrixStack, int x, CallbackInfo callbackInfo) {
	//		if (HudContainer.getMatrixCache().expBarTransform != null) {
	//			tempMatrix = matrixStack.peek().getModel();
	//			tempMatrix.multiply(HudContainer.getMatrixCache().expBarTransform);
	//		} else if (HudContainer.getMatrixCache().jumpBarTransform != null) {
	//			tempMatrix = matrixStack.peek().getModel();
	//			tempMatrix.multiply(HudContainer.getMatrixCache().jumpBarTransform);
	//		}
	//	}
	//
	//	@Inject(method = "renderMountJumpBar", at = @At(value = "RETURN"))
	//	public void renderMountJumpBarReturn(MatrixStack matrixStack, int x, CallbackInfo callbackInfo) {
	//		if (HudContainer.getMatrixCache().expBarTransform != null) {
	//			tempMatrix.multiply(HudTweaksOptions.expBarTransform.getInverse());
	//		} else if (HudContainer.getMatrixCache().jumpBarTransform != null) {
	//			tempMatrix.multiply(HudTweaksOptions.jumpBarTransform.getInverse());
	//		}
	//	}
	//
	@Inject(method = "renderStatusEffectOverlay", at = @At(value = "HEAD"))
	private void renderStatusEffectOverlayHead(MatrixStack matrixStack, CallbackInfo callbackInfo) {
		Matrix4f statusEffectsMatrix = HudContainer.getMatrixCache().getMatrix("statuseffects");
		if (statusEffectsMatrix != null) {
			multipliedMatrix = true;
			matrixStack.push();
			matrixStack.peek().getModel().multiply(statusEffectsMatrix);
		}
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
	private void setupPreCalcVars(MatrixStack matrixStack, CallbackInfo callbackInfo,
			Collection<?> u1, int u2, int u3, StatusEffectSpriteManager u4, List<?> u5, Iterator<?> u6, StatusEffectInstance u7, StatusEffect u8, // unused vars
			int x, int y) {
		this.preX = x;
		this.preY = y;
	}

	@Inject(method = "renderStatusEffectOverlay",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffectInstance;isAmbient()Z"),
			locals = LocalCapture.CAPTURE_FAILHARD)
	private void setupPostCalcVars(MatrixStack matrixStack, CallbackInfo callbackInfo,
			Collection<?> u1, int u2, int u3, StatusEffectSpriteManager u4, List<?> u5, Iterator<?> u6, StatusEffectInstance u7, StatusEffect u8, // unused vars
			int x, int y) {
		this.postX = x;
		this.postY = y;
	}

	@ModifyVariable(method = "renderStatusEffectOverlay",
					ordinal = 2,
					at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffectInstance;isAmbient()Z"))
	private int modifyStatusEffectX(int x, MatrixStack maxtixStack) {
		if (((StatusEffectsElement) HudContainer.getElement("statuseffects")).isVertical()) {
			return lastWidth - STATUS_EFFECT_OFFSET + preY - postY;
		} else {
			return x;
		}
	}

	@ModifyVariable(method = "renderStatusEffectOverlay",
					ordinal = 3,
					at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffectInstance;isAmbient()Z"))
	private int modifyStatusEffectY(int y, MatrixStack maxtixStack) {
		if (((StatusEffectsElement) HudContainer.getElement("statuseffects")).isVertical()) {
			return preY + lastWidth - postX - STATUS_EFFECT_OFFSET;
		} else {
			return y;
		}
	}

	@Inject(method = "renderStatusEffectOverlay", at = @At(value = "RETURN"))
	private void renderStatusEffectOverlayReturn(MatrixStack matrixStack, CallbackInfo callbackInfo) {
		if (multipliedMatrix) {
			matrixStack.pop();
			multipliedMatrix = false;
		}
	}
}
