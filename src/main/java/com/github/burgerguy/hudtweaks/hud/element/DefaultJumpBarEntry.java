package com.github.burgerguy.hudtweaks.hud.element;

import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import com.github.burgerguy.hudtweaks.util.Util;

import net.minecraft.client.MinecraftClient;

public class DefaultJumpBarEntry extends HudElementEntry {
	public static final HTIdentifier IDENTIFIER = new HTIdentifier(new HTIdentifier.ElementType("jumpbar", "hudtweaks.element.jumpbar"), Util.MINECRAFT_NAMESPACE);

	public DefaultJumpBarEntry() {
		super(IDENTIFIER);
	}
	
	@Override
	protected double calculateWidth(MinecraftClient client) {
		return 182;
	}
	
	@Override
	protected double calculateHeight(MinecraftClient client) {
		return 5;
	}
	
	@Override
	protected double calculateDefaultX(MinecraftClient client) {
		return client.getWindow().getScaledWidth() / 2 - 91;
	}
	
	@Override
	protected double calculateDefaultY(MinecraftClient client) {
		return client.getWindow().getScaledHeight() - 29;
	}
}
