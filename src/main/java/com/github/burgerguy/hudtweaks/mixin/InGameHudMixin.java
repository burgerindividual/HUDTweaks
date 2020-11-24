package com.github.burgerguy.hudtweaks.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.burgerguy.hudtweaks.gui.HudContainer;
import com.github.burgerguy.hudtweaks.gui.HudElement;
import com.github.burgerguy.hudtweaks.gui.HudTweaksOptionsScreen;
import com.github.burgerguy.hudtweaks.util.gui.MatrixCache;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
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
	private int lastWidth;
	@Unique
	private int lastHeight;
	
	@Unique
	private boolean multipliedMatrix;
	
	@Inject(method = "render", at = @At(value = "HEAD"))
	private void tryUpdateMatricies(MatrixStack matrixStack, float tickDelta, CallbackInfo callbackInfo) {
		if (HudTweaksOptionsScreen.isOpen()) super.fillGradient(matrixStack, 0, 0, scaledWidth, scaledHeight, -1072689136, -804253680);
		
		if (scaledWidth != lastWidth || scaledHeight != lastHeight) {
			lastWidth = scaledWidth;
			lastHeight = scaledHeight;
			MatrixCache.calculateAllMatricies(scaledWidth, scaledHeight);
		}
		
		for (HudElement element : HudContainer.getElements()) {
			if (element.requiresUpdate()) {
				MatrixCache.calculateMatrix(element, scaledWidth, scaledHeight);
			}
		}
	}
	
	@Inject(method = "renderHotbar", at = @At(value = "HEAD"))
	private void renderHotbarHead(float tickDelta, MatrixStack matrixStack, CallbackInfo callbackInfo) {
		Matrix4f hotbarMatrix = MatrixCache.getMatrix("hotbar");
		if (hotbarMatrix != null) {
			multipliedMatrix = true;
			RenderSystem.pushMatrix();
			RenderSystem.multMatrix(hotbarMatrix);
		}
		
//		if (HudTweaksOptions.hotbarVertical) {
//			RenderSystem.pushMatrix();
//			RenderSystem.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
//			RenderSystem.translatef(-200.0F, -scaledHeight, 0.0F);
//		}
	}
	
	@Inject(method = "renderHotbar", at = @At(value = "RETURN"))
	private void renderHotbarReturn(float tickDelta, MatrixStack matrixStack, CallbackInfo callbackInfo) {
//		if (HudTweaksOptions.hotbarVertical) RenderSystem.popMatrix();
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
			at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
			args = "ldc=armor"))
	private void renderArmor(MatrixStack matrixStack, CallbackInfo callbackInfo) {
		Matrix4f armorMatrix = MatrixCache.getMatrix("armor");
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
		
		Matrix4f healthMatrix = MatrixCache.getMatrix("health");
		if (healthMatrix != null) {
			multipliedMatrix = true;
			matrixStack.push();
			matrixStack.peek().getModel().multiply(healthMatrix);
		}
	}
	
	@Inject(method = "renderStatusBars",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;getRiddenEntity()Lnet/minecraft/entity/LivingEntity;"))
	private void renderFood(MatrixStack matrixStack, CallbackInfo callbackInfo) {
		if (multipliedMatrix) {
			matrixStack.pop();
			multipliedMatrix = false;
		}
		
		Matrix4f foodMatrix = MatrixCache.getMatrix("food");
		if (foodMatrix != null) {
			multipliedMatrix = true;
			matrixStack.push();
			matrixStack.peek().getModel().multiply(foodMatrix);
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
		
		Matrix4f airMatrix = MatrixCache.getMatrix("air");
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
//		if (MatrixCache.mountTransform != null) {
//			tempMatrix = matrixStack.peek().getModel();
//			tempMatrix.multiply(MatrixCache.mountTransform);
//		} else if (MatrixCache.foodTransform != null) {
//			tempMatrix = matrixStack.peek().getModel();
//			tempMatrix.multiply(MatrixCache.foodTransform);
//		}
//	}
//	
//	@Inject(method = "renderMountHealth", at = @At(value = "RETURN"))
//	private void renderMountHealthReturn(MatrixStack matrixStack, CallbackInfo callbackInfo) {
//		if (MatrixCache.mountTransform != null) {
//			tempMatrix.multiply(HudTweaksOptions.mountTransform.getInverse());
//		} else if (MatrixCache.foodTransform != null) {
//			tempMatrix.multiply(HudTweaksOptions.foodTransform.getInverse());
//		}
//	}
//	
	@Inject(method = "renderExperienceBar", at = @At(value = "HEAD"))
	public void renderExperienceBarHead(MatrixStack matrixStack, int x, CallbackInfo callbackInfo) {
		Matrix4f expBarMatrix = MatrixCache.getMatrix("expbar");
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
//		if (MatrixCache.expBarTransform != null) {
//			tempMatrix = matrixStack.peek().getModel();
//			tempMatrix.multiply(MatrixCache.expBarTransform);
//		} else if (MatrixCache.jumpBarTransform != null) {
//			tempMatrix = matrixStack.peek().getModel();
//			tempMatrix.multiply(MatrixCache.jumpBarTransform);
//		}
//	}
//	
//	@Inject(method = "renderMountJumpBar", at = @At(value = "RETURN"))
//	public void renderMountJumpBarReturn(MatrixStack matrixStack, int x, CallbackInfo callbackInfo) {
//		if (MatrixCache.expBarTransform != null) {
//			tempMatrix.multiply(HudTweaksOptions.expBarTransform.getInverse());
//		} else if (MatrixCache.jumpBarTransform != null) {
//			tempMatrix.multiply(HudTweaksOptions.jumpBarTransform.getInverse());
//		}
//	}
//	
//	@Inject(method = "renderStatusEffectOverlay", at = @At(value = "HEAD"))
//	private void renderStatusEffectOverlayHead(MatrixStack matrixStack, CallbackInfo callbackInfo) {
//		if (MatrixCache.statusEffectTransform != null) {
//			tempMatrix = matrixStack.peek().getModel();
//			tempMatrix.multiply(MatrixCache.statusEffectTransform);
//		}
//	}
//	
//	@Unique
//	private static final int STATUS_EFFECT_OFFSET = 25;
//	@Unique
//	private int preX;
//	@Unique
//	private int preY;
//	@Unique
//	private int postX;
//	@Unique
//	private int postY;
//	
//	@Inject(method = "renderStatusEffectOverlay",
//			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffect;isBeneficial()Z"),
//			locals = LocalCapture.CAPTURE_FAILHARD)
//	private void setupPreCalcVars(MatrixStack matrixStack, CallbackInfo callbackInfo,
//			Collection<?> u1, int u2, int u3, StatusEffectSpriteManager u4, List<?> u5, Iterator<?> u6, StatusEffectInstance u7, StatusEffect u8, // unused vars
//			int x, int y) {
//		this.preX = x;
//		this.preY = y;
//	}
//	
//	@Inject(method = "renderStatusEffectOverlay",
//			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffectInstance;isAmbient()Z"),
//			locals = LocalCapture.CAPTURE_FAILHARD)
//	private void setupPostCalcVars(MatrixStack matrixStack, CallbackInfo callbackInfo,
//			Collection<?> u1, int u2, int u3, StatusEffectSpriteManager u4, List<?> u5, Iterator<?> u6, StatusEffectInstance u7, StatusEffect u8, // unused vars
//			int x, int y) {
//		this.postX = x;
//		this.postY = y;
//	}
//	
//	@ModifyVariable(method = "renderStatusEffectOverlay",
//					ordinal = 2,
//					at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffectInstance;isAmbient()Z"))
//	private int modifyStatusEffectX(int x, MatrixStack maxtixStack) {
//		if (HudTweaksOptions.statusEffectVertical) {
//			return scaledWidth - STATUS_EFFECT_OFFSET + preY - postY;
//		} else {
//			return x;
//		}
//	}
//	
//	@ModifyVariable(method = "renderStatusEffectOverlay",
//					ordinal = 3,
//					at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffectInstance;isAmbient()Z"))
//	private int modifyStatusEffectY(int y, MatrixStack maxtixStack) {
//		if (HudTweaksOptions.statusEffectVertical) {
//			return preY + scaledWidth - postX - STATUS_EFFECT_OFFSET;
//		} else {
//			return y;
//		}
//	}
//	
//	@Inject(method = "renderStatusEffectOverlay", at = @At(value = "RETURN"))
//	private void renderStatusEffectOverlayReturn(MatrixStack matrixStack, CallbackInfo callbackInfo) {
//		if (MatrixCache.statusEffectTransform != null) tempMatrix.multiply(HudTweaksOptions.statusEffectTransform.getInverse());
//	}
	
//	@Overwrite
//	public void renderStatusEffectOverlay(MatrixStack matrixStack) {
//		if (MatrixCache.statusEffectTransform != null) {
//			tempMatrix = matrixStack.peek().getModel();
//			tempMatrix.multiply(MatrixCache.statusEffectTransform);
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
//					if (HudTweaksOptions.statusEffectVertical) {
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
//		if (MatrixCache.statusEffectTransform != null) tempMatrix.multiply(HudTweaksOptions.statusEffectTransform.getInverse());
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
