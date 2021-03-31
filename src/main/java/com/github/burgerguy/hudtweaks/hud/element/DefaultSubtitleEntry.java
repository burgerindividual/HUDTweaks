package com.github.burgerguy.hudtweaks.hud.element;

import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import com.github.burgerguy.hudtweaks.mixin.InGameHudAccessor;
import com.github.burgerguy.hudtweaks.util.Util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class DefaultSubtitleEntry extends HudElementEntry {
	public static final HTIdentifier IDENTIFIER = new HTIdentifier(new HTIdentifier.ElementType("subtitle", "hudtweaks.element.subtitle"), Util.MINECRAFT_NAMESPACE);
	
	public DefaultSubtitleEntry() {
		super(IDENTIFIER, "onSubtitleTextChange");
	}

	@Override
	protected double calculateWidth(MinecraftClient client) {
		Text titleText = ((InGameHudAccessor) client.inGameHud).getTitleText();
		if (titleText != null) {
			return client.textRenderer.getWidth(titleText) * 2.0D;
		}
		return 56;
	}

	@Override
	protected double calculateHeight(MinecraftClient client) {
		return client.textRenderer.fontHeight * 2;
	}

	@Override
	protected double calculateDefaultX(MinecraftClient client) {
		return (client.getWindow().getScaledWidth() - (int) getWidth()) / 2;
	}

	@Override
	protected double calculateDefaultY(MinecraftClient client) {
		return client.getWindow().getScaledHeight() / 2 + 10;
	}
}
