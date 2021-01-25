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
import com.github.burgerguy.hudtweaks.hud.element.HealthElement;
import com.github.burgerguy.hudtweaks.hud.element.HudElement;
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
	private void renderStart(MatrixStack matrixStack, float tickDelta, CallbackInfo callbackInfo) {
		int scaledWidth = client.getWindow().getScaledWidth();
		int scaledHeight = client.getWindow().getScaledHeight();
		
		if (HTOptionsScreen.isOpen()) {
			// super janky way to dim background
			super.fillGradient(matrixStack, 0, 0, scaledWidth, scaledHeight, 0xC0101010, 0xD0101010);
		}
		
		for (HudElement element : HudContainer.getElements()) {
			// allows us to keep track of what has rendered this frame
			element.setRendered(false);
		}
		
		client.getProfiler().push("fireHudTweaksEvents");
		updatedElementsX.clear();
		updatedElementsY.clear();
		HudContainer.getScreenRoot().tryManualUpdate(client, false, updatedElementsX, updatedElementsY);
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
	private void renderHotbarHead(float tickDelta, MatrixStack matrixStack, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPushMatrix("hotbar", null);
	}
	
	@Inject(method = "renderHotbar", at = @At(value = "RETURN"))
	private void renderHotbarReturn(float tickDelta, MatrixStack matrixStack, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPopMatrix("hotbar", null);
	}
	
	@Inject(method = "renderStatusBars",
			at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
			args = "ldc=armor"))
	private void renderArmor(MatrixStack matrixStack, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPushMatrix("armor", matrixStack);
	}
	
	@Inject(method = "renderStatusBars",
			at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
			args = "ldc=health"))
	private void renderHealth(MatrixStack matrixStack, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPopMatrix("armor", matrixStack);
		HudContainer.getMatrixCache().tryPushMatrix("health", matrixStack);
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
	
	@Inject(method = "renderStatusBars",
			at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/gui/hud/InGameHud;getHeartCount(Lnet/minecraft/entity/LivingEntity;)I"))
	private void renderHunger(MatrixStack matrixStack, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPopMatrix("health", matrixStack);
		HudContainer.getMatrixCache().tryPushMatrix("hunger", matrixStack);
	}
	
	@Inject(method = "renderStatusBars",
			at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
			args = "ldc=air"))
	private void renderAir(MatrixStack matrixStack, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPopMatrix("hunger", matrixStack);
		HudContainer.getMatrixCache().tryPushMatrix("air", matrixStack);
	}
	
	@Inject(method = "renderStatusBars", at = @At(value = "RETURN"))
	private void renderStatusBarsReturn(MatrixStack matrixStack, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPopMatrix("air", matrixStack);
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
		HudContainer.getMatrixCache().tryPushMatrix("expbar", matrixStack);
	}
	
	@Inject(method = "renderExperienceBar", at = @At(value = "RETURN"))
	public void renderExperienceBarReturn(MatrixStack matrixStack, int x, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPopMatrix("expbar", matrixStack);
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
		HudContainer.getMatrixCache().tryPushMatrix("statuseffects", matrixStack);
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
			return client.getWindow().getScaledWidth() - STATUS_EFFECT_OFFSET + preY - postY;
		} else {
			return x;
		}
	}

	@ModifyVariable(method = "renderStatusEffectOverlay",
					ordinal = 3,
					at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffectInstance;isAmbient()Z"))
	private int modifyStatusEffectY(int y, MatrixStack maxtixStack) {
		if (((StatusEffectsElement) HudContainer.getElement("statuseffects")).isVertical()) {
			return preY + client.getWindow().getScaledWidth() - postX - STATUS_EFFECT_OFFSET;
		} else {
			return y;
		}
	}

	@Inject(method = "renderStatusEffectOverlay", at = @At(value = "RETURN"))
	private void renderStatusEffectOverlayReturn(MatrixStack matrixStack, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPopMatrix("statuseffects", matrixStack);
	}
}
