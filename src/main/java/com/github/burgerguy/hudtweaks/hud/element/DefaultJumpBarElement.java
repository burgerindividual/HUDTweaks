package com.github.burgerguy.hudtweaks.hud.element;

import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import com.github.burgerguy.hudtweaks.util.Util;
import net.minecraft.client.MinecraftClient;

public class DefaultJumpBarElement extends HudElement {
	public static final HTIdentifier IDENTIFIER = new HTIdentifier(Util.MINECRAFT_MODID, new HTIdentifier.ElementId("jumpbar", "hudtweaks.element.jumpbar"));

	public DefaultJumpBarElement() {
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
		return client.getWindow().getScaledWidth() / 2.0 - 91;
	}
	
	@Override
	protected double calculateDefaultY(MinecraftClient client) {
		return client.getWindow().getScaledHeight() - 29;
	}
}
