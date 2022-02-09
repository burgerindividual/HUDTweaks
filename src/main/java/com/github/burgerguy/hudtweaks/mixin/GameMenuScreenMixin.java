package com.github.burgerguy.hudtweaks.mixin;

import com.github.burgerguy.hudtweaks.HudTweaksMod;
import com.github.burgerguy.hudtweaks.gui.HTOptionsScreen;
import com.github.burgerguy.hudtweaks.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends Screen {
	private GameMenuScreenMixin() {
		super(null);
	}

	@Inject(method = "initWidgets", at = @At(value = "HEAD"))
	private void initWidgets(CallbackInfo ci) {
		if (HudTweaksMod.getConfig().showGameMenuButton) {
			Text text = new TranslatableText("hudtweaks.options");
			int buttonWidth = MinecraftClient.getInstance().textRenderer.getWidth(text) + 14;
			ButtonWidget button = new ButtonWidget(width - buttonWidth, Util.SHOULD_COMPENSATE_FOR_MODMENU_BUTTON ? 12 : 0, buttonWidth, 20, text, b -> client.setScreen(new HTOptionsScreen(null)));
			addDrawableChild(button);
		}
	}
}
