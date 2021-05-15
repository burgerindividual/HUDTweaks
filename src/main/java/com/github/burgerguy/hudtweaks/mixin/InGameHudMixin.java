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
import com.github.burgerguy.hudtweaks.hud.element.DefaultActionBarEntry;
import com.github.burgerguy.hudtweaks.hud.element.DefaultAirEntry;
import com.github.burgerguy.hudtweaks.hud.element.DefaultArmorEntry;
import com.github.burgerguy.hudtweaks.hud.element.DefaultExperienceBarEntry;
import com.github.burgerguy.hudtweaks.hud.element.DefaultHealthEntry;
import com.github.burgerguy.hudtweaks.hud.element.DefaultHotbarEntry;
import com.github.burgerguy.hudtweaks.hud.element.DefaultHungerEntry;
import com.github.burgerguy.hudtweaks.hud.element.DefaultJumpBarEntry;
import com.github.burgerguy.hudtweaks.hud.element.DefaultMountHealthEntry;
import com.github.burgerguy.hudtweaks.hud.element.DefaultStatusEffectsEntry;
import com.github.burgerguy.hudtweaks.hud.element.DefaultSubtitleEntry;
import com.github.burgerguy.hudtweaks.hud.element.DefaultTitleEntry;
import com.github.burgerguy.hudtweaks.hud.element.DefaultTooltipEntry;
import com.github.burgerguy.hudtweaks.hud.element.HudElementEntry;
import com.github.burgerguy.hudtweaks.hud.element.HudElementType;
import com.github.burgerguy.hudtweaks.hud.tree.AbstractTypeNode;
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
	private final Set<AbstractTypeNode> updatedElementsX = new HashSet<>();
	@Unique
	private final Set<AbstractTypeNode> updatedElementsY = new HashSet<>();

	@Inject(method = "render", at = @At(value = "HEAD"))
	private void renderStart(MatrixStack matrices, float tickDelta, CallbackInfo callbackInfo) {
		if (HTOptionsScreen.isOpen()) {
			// super janky way to dim background
			super.fillGradient(matrices, 0, 0, client.getWindow().getScaledWidth(), client.getWindow().getScaledHeight(), 0xC0101010, 0xD0101010);
		}

		for (HudElementType elementType : HudContainer.getElementRegistry().getElementTypes()) { // TODO: fix this for when entry switches happen
			// allows us to keep track of what has rendered this frame
			elementType.clearDrawTest();
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

		for (Object type : Sets.union(updatedElementsX, updatedElementsY)) {
			if (type instanceof HudElementType) {
				HudElementEntry hudElement = ((HudElementType) type).getActiveEntry();
				HudContainer.getMatrixCache().putMatrix(hudElement.getIdentifier().getElementType(), hudElement.createMatrix());
			}
		}
		client.getProfiler().pop();
	}

	@Inject(method = "renderHotbar", at = @At(value = "HEAD"))
	private void renderHotbarHead(float tickDelta, MatrixStack matrices, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPushMatrix(DefaultHotbarEntry.IDENTIFIER, null);
	}

	@Inject(method = "renderHotbar", at = @At(value = "RETURN"))
	private void renderHotbarReturn(float tickDelta, MatrixStack matrices, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPopMatrix(DefaultHotbarEntry.IDENTIFIER, null);
	}

	@Inject(method = "renderStatusBars",
			at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
			args = "ldc=armor"))
	private void renderArmor(MatrixStack matrices, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPushMatrix(DefaultArmorEntry.IDENTIFIER, matrices);
	}

	@Inject(method = "renderStatusBars",
			at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
			args = "ldc=health"))
	private void renderHealth(MatrixStack matrices, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPopMatrix(DefaultArmorEntry.IDENTIFIER, matrices);
		HudContainer.getMatrixCache().tryPushMatrix(DefaultHealthEntry.IDENTIFIER, matrices);
	}

	// injects before if (i <= 4)
	// this reverses the negation it does right before
	@ModifyVariable(method = "renderStatusBars",
			ordinal = 19,
			at = @At(value = "JUMP", opcode = Opcodes.IF_ICMPGT))
	private int flipHealthStackDirection(int healthPos) {
		HudElementEntry activeHealthEntry = HudContainer.getElementRegistry().getActiveEntry(DefaultHealthEntry.IDENTIFIER.getElementType());
		if (activeHealthEntry instanceof DefaultHealthEntry && ((DefaultHealthEntry) activeHealthEntry).isFlipped()) {
			int originalHealthPos = client.getWindow().getScaledHeight() - 39;
			return originalHealthPos + originalHealthPos - healthPos;
		} else {
			return healthPos;
		}
	}

	@Inject(method = "renderStatusBars",
			at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/gui/hud/InGameHud;getHeartCount(Lnet/minecraft/entity/LivingEntity;)I"))
	private void renderHunger(MatrixStack matrices, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPopMatrix(DefaultHealthEntry.IDENTIFIER, matrices);
		HudContainer.getMatrixCache().tryPushMatrix(DefaultHungerEntry.IDENTIFIER, matrices);
	}

	// this makes sure the aa will equal zero so the if (aa == 0) will pass
	@ModifyVariable(method = "renderStatusBars",
			ordinal = 11,
			at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/gui/hud/InGameHud;getHeartCount(Lnet/minecraft/entity/LivingEntity;)I"))
	private int forceRenderHunger(int mountHealth) {
		HudElementEntry activeHungerEntry = HudContainer.getElementRegistry().getActiveEntry(DefaultHungerEntry.IDENTIFIER.getElementType());
		if (activeHungerEntry instanceof DefaultHungerEntry && ((DefaultHungerEntry) activeHungerEntry).getForceDisplay()) {
			return 0;
		} else {
			return mountHealth;
		}
	}

	@Inject(method = "renderStatusBars",
			at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
			args = "ldc=air"))
	private void renderAir(MatrixStack matrices, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPopMatrix(DefaultHungerEntry.IDENTIFIER, matrices);
		HudContainer.getMatrixCache().tryPushMatrix(DefaultAirEntry.IDENTIFIER, matrices);
	}

	@Inject(method = "renderStatusBars", at = @At(value = "RETURN"))
	private void renderStatusBarsReturn(MatrixStack matrices, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPopMatrix(DefaultAirEntry.IDENTIFIER, matrices);
	}

	@Inject(method = "renderMountHealth", at = @At(value = "HEAD"))
	private void renderMountHealthHead(MatrixStack matrices, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPushMatrix(DefaultMountHealthEntry.IDENTIFIER, matrices);
	}

	@Inject(method = "renderMountHealth", at = @At(value = "RETURN"))
	private void renderMountHealthReturn(MatrixStack matrices, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPopMatrix(DefaultMountHealthEntry.IDENTIFIER, matrices);
	}

	@Inject(method = "renderExperienceBar", at = @At(value = "HEAD"))
	public void renderExperienceBarHead(MatrixStack matrices, int x, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPushMatrix(DefaultExperienceBarEntry.IDENTIFIER, matrices);
	}

	@Inject(method = "renderExperienceBar", at = @At(value = "RETURN"))
	public void renderExperienceBarReturn(MatrixStack matrices, int x, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPopMatrix(DefaultExperienceBarEntry.IDENTIFIER, matrices);
	}

	@Shadow
	public abstract void renderExperienceBar(MatrixStack matrices, int x);

	@Inject(method = "renderMountJumpBar", at = @At(value = "HEAD"))
	public void renderMountJumpBarHead(MatrixStack matrices, int x, CallbackInfo callbackInfo) {
		// Typically, when the jump bar is visible it hides the experience bar. When
		// the exp bar is set to force display, we can just do it here as the only
		// time it's hidden (aside from F1) is when the jump bar is rendering.
		HudElementEntry activeExpBarEntry = HudContainer.getElementRegistry().getActiveEntry(DefaultExperienceBarEntry.IDENTIFIER.getElementType());
		if (activeExpBarEntry instanceof DefaultExperienceBarEntry && ((DefaultExperienceBarEntry) activeExpBarEntry).getForceDisplay()) {
			renderExperienceBar(matrices, x);
		}

		HudContainer.getMatrixCache().tryPushMatrix(DefaultJumpBarEntry.IDENTIFIER, matrices);
	}

	@Inject(method = "renderMountJumpBar", at = @At(value = "RETURN"))
	public void renderMountJumpBarReturn(MatrixStack matrices, int x, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPopMatrix(DefaultJumpBarEntry.IDENTIFIER, matrices);
	}

	@Inject(method = "renderStatusEffectOverlay", at = @At(value = "HEAD"))
	private void renderStatusEffectOverlayHead(MatrixStack matrices, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPushMatrix(DefaultStatusEffectsEntry.IDENTIFIER, matrices);
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
		preX = x;
		preY = y;
	}
	
	@Inject(method = "renderStatusEffectOverlay",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffectInstance;isAmbient()Z"),
			locals = LocalCapture.CAPTURE_FAILHARD)
	private void setupPostCalcVars(MatrixStack matrices, CallbackInfo callbackInfo,
			Collection<?> u1, int u2, int u3, StatusEffectSpriteManager u4, List<?> u5, Iterator<?> u6, StatusEffectInstance u7, StatusEffect u8, // unused vars
			int x, int y) {
		postX = x;
		postY = y;
	}
	
	@ModifyVariable(method = "renderStatusEffectOverlay",
			ordinal = 2,
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffectInstance;isAmbient()Z"))
	private int modifyStatusEffectX(int x, MatrixStack maxtixStack) {
		HudElementEntry activeStatusEffectsEntry = HudContainer.getElementRegistry().getActiveEntry(DefaultStatusEffectsEntry.IDENTIFIER.getElementType());
		if (activeStatusEffectsEntry instanceof DefaultStatusEffectsEntry && ((DefaultStatusEffectsEntry) activeStatusEffectsEntry).isVertical()) {
			return client.getWindow().getScaledWidth() - STATUS_EFFECT_OFFSET + preY - postY;
		} else {
			return x;
		}
	}
	
	@ModifyVariable(method = "renderStatusEffectOverlay",
			ordinal = 3,
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffectInstance;isAmbient()Z"))
	private int modifyStatusEffectY(int y, MatrixStack maxtixStack) {
		HudElementEntry activeStatusEffectsEntry = HudContainer.getElementRegistry().getActiveEntry(DefaultStatusEffectsEntry.IDENTIFIER.getElementType());
		if (activeStatusEffectsEntry instanceof DefaultStatusEffectsEntry && ((DefaultStatusEffectsEntry) activeStatusEffectsEntry).isVertical()) {
			return preY + client.getWindow().getScaledWidth() - postX - STATUS_EFFECT_OFFSET;
		} else {
			return y;
		}
	}
	
	@Inject(method = "renderStatusEffectOverlay", at = @At(value = "RETURN"))
	private void renderStatusEffectOverlayReturn(MatrixStack matrices, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPopMatrix(DefaultStatusEffectsEntry.IDENTIFIER, matrices);
	}

	@Inject(method = "renderHeldItemTooltip", at = @At(value = "HEAD"))
	private void renderTooltipHead(MatrixStack matrices, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPushMatrix(DefaultTooltipEntry.IDENTIFIER, matrices);
	}

	@Inject(method = "renderHeldItemTooltip", at = @At(value = "RETURN"))
	private void renderTooltipReturn(MatrixStack matrices, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPopMatrix(DefaultTooltipEntry.IDENTIFIER, matrices);
	}

	@Inject(method = "render",
			at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I"))
	private void renderActionBarStart(MatrixStack matrices, float tickDelta, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPushMatrix(DefaultActionBarEntry.IDENTIFIER, matrices);
	}

	@Inject(method = "render",
			at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I",
			shift = At.Shift.AFTER))
	private void renderActionBarEnd(MatrixStack matrices, float tickDelta, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPopMatrix(DefaultActionBarEntry.IDENTIFIER, matrices);
	}

	@Inject(method = "render",
			at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I",
			ordinal = 0))
	private void renderTitleStart(MatrixStack matrices, float tickDelta, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPushMatrix(DefaultTitleEntry.IDENTIFIER, matrices);
	}

	@Inject(method = "render",
			at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I",
			ordinal = 0,
			shift = At.Shift.AFTER))
	private void renderTitleEnd(MatrixStack matrices, float tickDelta, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPopMatrix(DefaultTitleEntry.IDENTIFIER, matrices);
	}

	@Inject(method = "render",
			at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I",
			ordinal = 1))
	private void renderSubtitleStart(MatrixStack matrices, float tickDelta, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPushMatrix(DefaultSubtitleEntry.IDENTIFIER, matrices);
	}

	@Inject(method = "render",
			at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I",
			ordinal = 1,
			shift = At.Shift.AFTER))
	private void renderSubtitleEnd(MatrixStack matrices, float tickDelta, CallbackInfo callbackInfo) {
		HudContainer.getMatrixCache().tryPopMatrix(DefaultSubtitleEntry.IDENTIFIER, matrices);
	}
}
