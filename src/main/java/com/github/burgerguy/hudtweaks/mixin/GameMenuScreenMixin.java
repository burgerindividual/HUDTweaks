package com.github.burgerguy.hudtweaks.mixin;

import com.github.burgerguy.hudtweaks.gui.HTOptionsScreen;
import com.github.burgerguy.hudtweaks.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
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
	private void initWidgets(CallbackInfo callbackInfo) {
		Text text = new TranslatableText("hudtweaks.options");
		int buttonWidth = MinecraftClient.getInstance().textRenderer.getWidth(text) + 14;
		@SuppressWarnings("MixinInnerClass") // cry about it
		AbstractButtonWidget button = new AbstractPressableButtonWidget(width - buttonWidth, Util.SHOULD_COMPENSATE_FOR_MODMENU_BUTTON ? 12 : 0, buttonWidth, 20, text) {
			@Override
			public void onPress() {
				client.openScreen(new HTOptionsScreen(null));
			}
		};
		addButton(button);
	}
}
