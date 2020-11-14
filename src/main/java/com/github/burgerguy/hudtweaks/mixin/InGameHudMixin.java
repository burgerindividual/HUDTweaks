package com.github.burgerguy.hudtweaks.mixin;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.github.burgerguy.hudtweaks.util.HudConfig;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.math.Matrix4f;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin extends DrawableHelper {
	@Final
	@Shadow
	private MinecraftClient client;
	
	@Shadow
	private int scaledWidth;
	
	@Shadow
	private int scaledHeight;
	
	@Unique
	private Matrix4f tempMatrix;
	
	@Inject(method = "renderHotbar", at = @At(value = "HEAD"))
	protected void renderHotbarHead(float f, MatrixStack matrixStack, CallbackInfo callbackInfo) {
		//RenderSystem.scalef(10F, 10F, 10F);
		RenderSystem.rotatef(60F, 0.0F, 0.0F, 1.0F);
		if (HudConfig.hotbarTransform.isSet()) {
			tempMatrix = matrixStack.peek().getModel();
			tempMatrix.multiply(HudConfig.hotbarTransform.get());
		}
	}
	
	@Inject(method = "renderHotbar", at = @At(value = "RETURN"))
	protected void renderHotbarReturn(float f, MatrixStack matrixStack, CallbackInfo callbackInfo) {
		//RenderSystem.scalef(0.1F, 0.1F, 0.1F);
		RenderSystem.rotatef(75F, 0.0F, 0.0F, -1.0F);
		if (HudConfig.hotbarTransform.isSet()) tempMatrix.multiply(HudConfig.hotbarTransform.getInverse());
	}
	
	@Inject(method = "renderHotbarItem", at = @At(value = "HEAD"))
	private void renderHotbarItemHead(CallbackInfo callbackInfo) {
		if (HudConfig.hotbarTransform.isSet()) RenderSystem.multMatrix(HudConfig.hotbarTransform.get());
	}
	
	@Inject(method = "renderHotbarItem", at = @At(value = "RETURN"))
	private void renderHotbarItemReturn(CallbackInfo callbackInfo) {
		if (HudConfig.hotbarTransform.isSet()) RenderSystem.multMatrix(HudConfig.hotbarTransform.getInverse());
	}
	
	@Inject(method = "renderStatusBars",
			at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
			args = "ldc=armor"))
	private void renderArmor(MatrixStack matrixStack, CallbackInfo callbackInfo) {
		if (HudConfig.armorTransform.isSet()) {
			tempMatrix = matrixStack.peek().getModel();
			tempMatrix.multiply(HudConfig.armorTransform.get());
		}
	}
	
	@Inject(method = "renderStatusBars",
			at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
			args = "ldc=health"))
	private void renderHealth(MatrixStack matrixStack, CallbackInfo callbackInfo) {
		if (HudConfig.armorTransform.isSet()) tempMatrix.multiply(HudConfig.armorTransform.getInverse());
		
		if (HudConfig.healthTransform.isSet()) {
			tempMatrix = matrixStack.peek().getModel();
			tempMatrix.multiply(HudConfig.healthTransform.get());
		}
	}
	
	@Inject(method = "renderStatusBars",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;getRiddenEntity()Lnet/minecraft/entity/LivingEntity;"))
	private void renderFood(MatrixStack matrixStack, CallbackInfo callbackInfo) {
		if (HudConfig.healthTransform.isSet()) tempMatrix.multiply(HudConfig.healthTransform.getInverse());
		
		if (HudConfig.foodTransform.isSet()) {
			tempMatrix = matrixStack.peek().getModel();
			tempMatrix.multiply(HudConfig.foodTransform.get());
		}
	}
	
	@Inject(method = "renderStatusBars",
			at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
			args = "ldc=air"))
	private void renderAir(MatrixStack matrixStack, CallbackInfo callbackInfo) {
		if (HudConfig.foodTransform.isSet()) tempMatrix.multiply(HudConfig.foodTransform.getInverse());
		
		if (HudConfig.airTransform.isSet()) {
			tempMatrix = matrixStack.peek().getModel();
			tempMatrix.multiply(HudConfig.airTransform.get());
		}
	}
	
	@Inject(method = "renderStatusBars", at = @At(value = "RETURN"))
	private void renderStatusBarsReturn(MatrixStack matrixStack, CallbackInfo callbackInfo) {
		if (HudConfig.airTransform.isSet()) tempMatrix.multiply(HudConfig.airTransform.getInverse());
	}
	
	@Inject(method = "renderMountHealth", at = @At(value = "HEAD"))
	private void renderMountHealthHead(MatrixStack matrixStack, CallbackInfo callbackInfo) {
		if (HudConfig.mountTransform.isSet()) {
			tempMatrix = matrixStack.peek().getModel();
			tempMatrix.multiply(HudConfig.mountTransform.get());
		} else if (HudConfig.foodTransform.isSet()) {
			tempMatrix = matrixStack.peek().getModel();
			tempMatrix.multiply(HudConfig.foodTransform.get());
		}
	}
	
	@Inject(method = "renderMountHealth", at = @At(value = "RETURN"))
	private void renderMountHealthReturn(MatrixStack matrixStack, CallbackInfo callbackInfo) {
		if (HudConfig.mountTransform.isSet()) {
			tempMatrix.multiply(HudConfig.mountTransform.getInverse());
		} else if (HudConfig.foodTransform.isSet()) {
			tempMatrix.multiply(HudConfig.foodTransform.getInverse());
		}
	}
	
	@Inject(method = "renderExperienceBar", at = @At(value = "HEAD"))
	public void renderExperienceBarHead(MatrixStack matrixStack, int x, CallbackInfo callbackInfo) {
		if (HudConfig.expBarTransform.isSet()) {
			tempMatrix = matrixStack.peek().getModel();
			tempMatrix.multiply(HudConfig.expBarTransform.get());
		}
	}
	
	@Inject(method = "renderExperienceBar", at = @At(value = "RETURN"))
	public void renderExperienceBarReturn(MatrixStack matrixStack, int x, CallbackInfo callbackInfo) {
		if (HudConfig.expBarTransform.isSet()) tempMatrix.multiply(HudConfig.expBarTransform.getInverse());
	}
	
	@Inject(method = "renderMountJumpBar", at = @At(value = "HEAD"))
	public void renderMountJumpBarHead(MatrixStack matrixStack, int x, CallbackInfo callbackInfo) {
		if (HudConfig.expBarTransform.isSet()) {
			tempMatrix = matrixStack.peek().getModel();
			tempMatrix.multiply(HudConfig.expBarTransform.get());
		} else if (HudConfig.jumpBarTransform.isSet()) {
			tempMatrix = matrixStack.peek().getModel();
			tempMatrix.multiply(HudConfig.jumpBarTransform.get());
		}
	}
	
	@Inject(method = "renderMountJumpBar", at = @At(value = "RETURN"))
	public void renderMountJumpBarReturn(MatrixStack matrixStack, int x, CallbackInfo callbackInfo) {
		if (HudConfig.expBarTransform.isSet()) {
			tempMatrix.multiply(HudConfig.expBarTransform.getInverse());
		} else if (HudConfig.jumpBarTransform.isSet()) {
			tempMatrix.multiply(HudConfig.jumpBarTransform.getInverse());
		}
	}
	
	@Inject(method = "renderStatusEffectOverlay", at = @At(value = "HEAD"))
	protected void renderStatusEffectOverlayHead(MatrixStack matrixStack, CallbackInfo callbackInfo) {
		if (HudConfig.statusEffectTransform.isSet()) {
			tempMatrix = matrixStack.peek().getModel();
			tempMatrix.multiply(HudConfig.statusEffectTransform.get());
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
	protected void setupPreCalcVars(MatrixStack matrixStack, CallbackInfo callbackInfo,
			Collection<?> u1, int u2, int u3, StatusEffectSpriteManager u4, List<?> u5, Iterator<?> u6, StatusEffectInstance u7, StatusEffect u8, // unused vars
			int x, int y) {
		this.preX = x;
		this.preY = y;
	}
	
	@Inject(method = "renderStatusEffectOverlay",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffectInstance;isAmbient()Z"),
			locals = LocalCapture.CAPTURE_FAILHARD)
	protected void setupPostCalcVars(MatrixStack matrixStack, CallbackInfo callbackInfo,
			Collection<?> u1, int u2, int u3, StatusEffectSpriteManager u4, List<?> u5, Iterator<?> u6, StatusEffectInstance u7, StatusEffect u8, // unused vars
			int x, int y) {
		this.postX = x;
		this.postY = y;
	}
	
	@ModifyVariable(method = "renderStatusEffectOverlay",
					ordinal = 2,
					at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffectInstance;isAmbient()Z"))
	protected int modifyStatusEffectX(int x, MatrixStack maxtixStack) {
		if (HudConfig.statusEffectVertical) {
			return scaledWidth - STATUS_EFFECT_OFFSET + preY - postY;
		} else {
			return x;
		}
	}
	
	@ModifyVariable(method = "renderStatusEffectOverlay",
					ordinal = 3,
					at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffectInstance;isAmbient()Z"))
	protected int modifyStatusEffectY(int y, MatrixStack maxtixStack) {
		if (HudConfig.statusEffectVertical) {
			return preY + scaledWidth - postX - STATUS_EFFECT_OFFSET;
		} else {
			return y;
		}
	}
	
	@Inject(method = "renderStatusEffectOverlay", at = @At(value = "RETURN"))
	protected void renderStatusEffectOverlayReturn(MatrixStack matrixStack, CallbackInfo callbackInfo) {
		if (HudConfig.statusEffectTransform.isSet()) tempMatrix.multiply(HudConfig.statusEffectTransform.getInverse());
	}
	
//	@Overwrite
//	public void renderStatusEffectOverlay(MatrixStack matrixStack) {
//		if (HudConfig.statusEffectTransform.isSet()) {
//			tempMatrix = matrixStack.peek().getModel();
//			tempMatrix.multiply(HudConfig.statusEffectTransform.get());
//		}
//		
//		Collection<StatusEffectInstance> collection = this.client.player.getStatusEffects();
//		if (!collection.isEmpty()) {
//			RenderSystem.enableBlend();
//			int i = 0;
//			int j = 0;
//			StatusEffectSpriteManager statusEffectSpriteManager = this.client.getStatusEffectSpriteManager();
//			List<Runnable> drawList = Lists.newArrayListWithExpectedSize(collection.size());
//			this.client.getTextureManager().bindTexture(HandledScreen.BACKGROUND_TEXTURE);
//			Iterator<StatusEffectInstance> iterator = Ordering.natural().reverse().sortedCopy(collection).iterator();
//			
//			while (iterator.hasNext()) {
//				StatusEffectInstance statusEffectInstance = (StatusEffectInstance) iterator.next();
//				StatusEffect statusEffect = statusEffectInstance.getEffectType();
//				if (statusEffectInstance.shouldShowIcon()) {
//					int k = this.scaledWidth;
//					int l = 1;
//					if (this.client.isDemo()) {
//						l += 15;
//					}
//					
//					if (HudConfig.statusEffectVertical) {
//						k -= 25;
//						l -= 25;
//						
//						if (statusEffect.isBeneficial()) {
//							++i;
//							l += 25 * i;
//						} else {
//							++j;
//							l += 25 * j;
//							k -= 26;
//						}
//					} else {
//						if (statusEffect.isBeneficial()) {
//							++i;
//							k -= 25 * i;
//						} else {
//							++j;
//							k -= 25 * j;
//							l += 26;
//						}
//					}
//					
//					RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
//					float f = 1.0F;
//					if (statusEffectInstance.isAmbient()) {
//						this.drawTexture(matrixStack, k, l, 165, 166, 24, 24);
//					} else {
//						this.drawTexture(matrixStack, k, l, 141, 166, 24, 24);
//						if (statusEffectInstance.getDuration() <= 200) {
//							int m = 10 - statusEffectInstance.getDuration() / 20;
//							f = MathHelper.clamp((float) statusEffectInstance.getDuration() / 10.0F / 5.0F * 0.5F, 0.0F,
//									0.5F)
//									+ MathHelper.cos((float) statusEffectInstance.getDuration() * 3.1415927F / 5.0F)
//											* MathHelper.clamp((float) m / 10.0F * 0.25F, 0.0F, 0.25F);
//						}
//					}
//					
//					Sprite sprite = statusEffectSpriteManager.getSprite(statusEffect);
//					drawList.add(statusEffectRunnableHelper(matrixStack, k, l, f, sprite));
//				}
//			}
//			
//			for (Runnable r : drawList) r.run();
//		}
//		
//		if (HudConfig.statusEffectTransform.isSet()) tempMatrix.multiply(HudConfig.statusEffectTransform.getInverse());
//	}
//	
//	private Runnable statusEffectRunnableHelper(MatrixStack matrixStack, int k, int l, float alpha, Sprite sprite) {
//		return () -> {
//			this.client.getTextureManager().bindTexture(sprite.getAtlas().getId());
//			RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
//			DrawableHelper.drawSprite(matrixStack, k + 3, l + 3, this.getZOffset(), 18, 18, sprite);
//		};
//	}
	
}
