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
	protected float calculateWidth(MinecraftClient client) {
		return 182;
	}
	
	@Override
	protected float calculateHeight(MinecraftClient client) {
		return 5;
	}
	
	@Override
	protected float calculateDefaultX(MinecraftClient client) {
		return client.getWindow().getScaledWidth() / 2.0f - 91;
	}
	
	@Override
	protected float calculateDefaultY(MinecraftClient client) {
		return client.getWindow().getScaledHeight() - 29;
	}
}
