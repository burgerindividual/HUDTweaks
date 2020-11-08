package com.github.burgerguy.hudtweaks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import com.github.burgerguy.hudtweaks.util.HudTransforms;

@Mixin(InGameHud.class)
public class InGameHudMixin {
	private Matrix4f tempMatrix;
	
	@Inject(method = "renderHotbar", at = @At(value = "HEAD"))
	protected void renderHotbarHead(float f, MatrixStack matrixStack, CallbackInfo callbackInfo) {
		if (HudTransforms.hotbarTransform.isSet()) {
			tempMatrix = matrixStack.peek().getModel();
			tempMatrix.multiply(HudTransforms.hotbarTransform.get());
		}
	}
	
	@Inject(method = "renderHotbar", at = @At(value = "RETURN"))
	protected void renderHotbarReturn(float f, MatrixStack matrixStack, CallbackInfo callbackInfo) {
		if (HudTransforms.hotbarTransform.isSet()) tempMatrix.multiply(HudTransforms.hotbarTransform.getInverse());
	}
	
	@Inject(method = "renderHotbarItem", at = @At(value = "HEAD"))
	private void renderHotbarItemHead(CallbackInfo callbackInfo) {
		if (HudTransforms.hotbarTransform.isSet()) RenderSystem.multMatrix(HudTransforms.hotbarTransform.get());
	}
	
	@Inject(method = "renderHotbarItem", at = @At(value = "RETURN"))
	private void renderHotbarItemReturn(CallbackInfo callbackInfo) {
		if (HudTransforms.hotbarTransform.isSet()) RenderSystem.multMatrix(HudTransforms.hotbarTransform.getInverse());
	}
	
	@Inject(method = "renderStatusBars", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V", args = "ldc=armor"))
	private void renderArmor(MatrixStack matrixStack, CallbackInfo callbackInfo) {
		if (HudTransforms.armorTransform.isSet()) {
			tempMatrix = matrixStack.peek().getModel();
			tempMatrix.multiply(HudTransforms.armorTransform.get());
		}
	}
	
	@Inject(method = "renderStatusBars", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = "ldc=health"))
	private void renderHealth(MatrixStack matrixStack, CallbackInfo callbackInfo) {
		if (HudTransforms.armorTransform.isSet()) tempMatrix.multiply(HudTransforms.armorTransform.getInverse());
		
		if (HudTransforms.healthTransform.isSet()) {
			tempMatrix = matrixStack.peek().getModel();
			tempMatrix.multiply(HudTransforms.healthTransform.get());
		}
	}
	
	@Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;getRiddenEntity()Lnet/minecraft/entity/LivingEntity;"))
	private void renderFood(MatrixStack matrixStack, CallbackInfo callbackInfo) {
		if (HudTransforms.healthTransform.isSet()) tempMatrix.multiply(HudTransforms.healthTransform.getInverse());
		
		if (HudTransforms.foodTransform.isSet()) {
			tempMatrix = matrixStack.peek().getModel();
			tempMatrix.multiply(HudTransforms.foodTransform.get());
		}
	}
	
	@Inject(method = "renderStatusBars", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = "ldc=air"))
	private void renderAir(MatrixStack matrixStack, CallbackInfo callbackInfo) {
		if (HudTransforms.foodTransform.isSet()) tempMatrix.multiply(HudTransforms.foodTransform.getInverse());
		
		if (HudTransforms.airTransform.isSet()) {
			tempMatrix = matrixStack.peek().getModel();
			tempMatrix.multiply(HudTransforms.airTransform.get());
		}
	}
	
	@Inject(method = "renderStatusBars", at = @At(value = "RETURN"))
	private void renderStatusBarsReturn(MatrixStack matrixStack, CallbackInfo callbackInfo) {
		if (HudTransforms.airTransform.isSet()) tempMatrix.multiply(HudTransforms.airTransform.getInverse());
	}
	
	@Inject(method = "renderMountHealth", at = @At(value = "HEAD"))
	private void renderMountHealthHead(MatrixStack matrixStack, CallbackInfo callbackInfo) {
		if (HudTransforms.mountTransform.isSet()) {
			tempMatrix = matrixStack.peek().getModel();
			tempMatrix.multiply(HudTransforms.mountTransform.get());
		} else if (HudTransforms.foodTransform.isSet()) {
			tempMatrix = matrixStack.peek().getModel();
			tempMatrix.multiply(HudTransforms.foodTransform.get());
		}
	}
	
	@Inject(method = "renderMountHealth", at = @At(value = "RETURN"))
	private void renderMountHealthReturn(MatrixStack matrixStack, CallbackInfo callbackInfo) {
		if (HudTransforms.mountTransform.isSet()) {
			tempMatrix.multiply(HudTransforms.mountTransform.getInverse());
		} else if (HudTransforms.foodTransform.isSet()) {
			tempMatrix.multiply(HudTransforms.foodTransform.getInverse());
		}
	}
	
	@Inject(method = "renderExperienceBar", at = @At(value = "HEAD"))
	public void renderExperienceBarHead(MatrixStack matrixStack, int x, CallbackInfo callbackInfo) {
		if (HudTransforms.expBarTransform.isSet()) {
			tempMatrix = matrixStack.peek().getModel();
			tempMatrix.multiply(HudTransforms.expBarTransform.get());
		}
	}
	
	@Inject(method = "renderExperienceBar", at = @At(value = "RETURN"))
	public void renderExperienceBarReturn(MatrixStack matrixStack, int x, CallbackInfo callbackInfo) {
		if (HudTransforms.expBarTransform.isSet()) tempMatrix.multiply(HudTransforms.expBarTransform.getInverse());
	}
	
	@Inject(method = "renderMountJumpBar", at = @At(value = "HEAD"))
	public void renderMountJumpBarHead(MatrixStack matrixStack, int x, CallbackInfo callbackInfo) {
		if (HudTransforms.expBarTransform.isSet()) {
			tempMatrix = matrixStack.peek().getModel();
			tempMatrix.multiply(HudTransforms.expBarTransform.get());
		} else if (HudTransforms.jumpBarTransform.isSet()) {
			tempMatrix = matrixStack.peek().getModel();
			tempMatrix.multiply(HudTransforms.jumpBarTransform.get());
		}
	}
	
	@Inject(method = "renderMountJumpBar", at = @At(value = "RETURN"))
	public void renderMountJumpBarReturn(MatrixStack matrixStack, int x, CallbackInfo callbackInfo) {
		if (HudTransforms.expBarTransform.isSet()) {
			tempMatrix.multiply(HudTransforms.expBarTransform.getInverse());
		} else if (HudTransforms.jumpBarTransform.isSet()) {
			tempMatrix.multiply(HudTransforms.jumpBarTransform.getInverse());
		}
	}
}
