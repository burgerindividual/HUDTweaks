package com.github.burgerguy.hudtweaks.hud.element;

import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import com.github.burgerguy.hudtweaks.util.Util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.AttackIndicator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class DefaultHotbarEntry extends HudElementEntry {
	public transient static final HTIdentifier IDENTIFIER = new HTIdentifier(new HTIdentifier.ElementType("hotbar", "hudtweaks.element.hotbar"), Util.HUDTWEAKS_NAMESPACE);

	public DefaultHotbarEntry() {
		super(IDENTIFIER, "onOffhandStatusChange", "onHotbarAttackIndicatorChange");
	}
	
	private int getAttackIndicatorOffset(MinecraftClient client) {
		return client != null && client.options.attackIndicator.equals(AttackIndicator.HOTBAR) ? 24 : 0;
	}
	
	private int getOffhandOffset(MinecraftClient client) {
		Entity cameraEntity = client.getCameraEntity();
		if (cameraEntity != null && cameraEntity instanceof PlayerEntity) {
			if (!((PlayerEntity) cameraEntity).getOffHandStack().isEmpty()) {
				return 29;
			}
		}
		return 0;
	}

	@Override
	protected double calculateWidth(MinecraftClient client) {
		return 182 + getAttackIndicatorOffset(client) + getOffhandOffset(client);
	}

	@Override
	protected double calculateHeight(MinecraftClient client) {
		return 24;
	}

	@Override
	protected double calculateDefaultX(MinecraftClient client) {
		return client.getWindow().getScaledWidth() / 2 - 91 - getOffhandOffset(client);
	}

	@Override
	protected double calculateDefaultY(MinecraftClient client) {
		return client.getWindow().getScaledHeight() - 24;
	}
}
