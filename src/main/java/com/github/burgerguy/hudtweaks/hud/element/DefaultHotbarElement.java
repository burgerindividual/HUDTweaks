package com.github.burgerguy.hudtweaks.hud.element;

import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import com.github.burgerguy.hudtweaks.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.AttackIndicator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class DefaultHotbarElement extends HudElement {
	public static final HTIdentifier IDENTIFIER = new HTIdentifier(Util.MINECRAFT_MODID, new HTIdentifier.ElementId("hotbar", "hudtweaks.element.hotbar"));
	
	public DefaultHotbarElement() {
		super(IDENTIFIER, "onOffhandStatusChange", "onHotbarAttackIndicatorChange");
	}

	private int getAttackIndicatorOffset(MinecraftClient client) {
		return client != null && client.options.attackIndicator.equals(AttackIndicator.HOTBAR) ? 24 : 0;
	}

	private int getOffhandOffset(MinecraftClient client) {
		Entity cameraEntity = client.getCameraEntity();
		if (cameraEntity instanceof PlayerEntity) {
			if (!((PlayerEntity) cameraEntity).getOffHandStack().isEmpty()) {
				return 29;
			}
		}
		return 0;
	}
	
	@Override
	protected float calculateWidth(MinecraftClient client) {
		return 182 + getAttackIndicatorOffset(client) + getOffhandOffset(client);
	}
	
	@Override
	protected float calculateHeight(MinecraftClient client) {
		return 24;
	}
	
	@Override
	protected float calculateDefaultX(MinecraftClient client) {
		return client.getWindow().getScaledWidth() / 2.0f - 91 - getOffhandOffset(client);
	}
	
	@Override
	protected float calculateDefaultY(MinecraftClient client) {
		return client.getWindow().getScaledHeight() - 24;
	}
}
