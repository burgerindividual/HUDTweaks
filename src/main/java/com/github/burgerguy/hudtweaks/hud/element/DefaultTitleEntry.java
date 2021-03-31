package com.github.burgerguy.hudtweaks.hud.element;

import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import com.github.burgerguy.hudtweaks.mixin.InGameHudAccessor;
import com.github.burgerguy.hudtweaks.util.Util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class DefaultTitleEntry extends HudElementEntry {
	public static final HTIdentifier IDENTIFIER = new HTIdentifier(new HTIdentifier.ElementType("title", "hudtweaks.element.title"), Util.MINECRAFT_NAMESPACE);
	
	public DefaultTitleEntry() {
		super(IDENTIFIER, "onTitleTextChange");
	}

	@Override
	protected double calculateWidth(MinecraftClient client) {
		Text titleText = ((InGameHudAccessor) client.inGameHud).getTitleText();
		if (titleText != null) {
			return client.textRenderer.getWidth(titleText) * 4.0D;
		}
		return 56;
	}

	@Override
	protected double calculateHeight(MinecraftClient client) {
		return client.textRenderer.fontHeight * 4;
	}

	@Override
	protected double calculateDefaultX(MinecraftClient client) {
		return (client.getWindow().getScaledWidth() - (int) getWidth()) / 2;
	}

	@Override
	protected double calculateDefaultY(MinecraftClient client) {
		return client.getWindow().getScaledHeight() / 2 - 40;
	}
}
