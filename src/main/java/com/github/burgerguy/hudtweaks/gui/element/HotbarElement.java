package com.github.burgerguy.hudtweaks.gui.element;

import com.github.burgerguy.hudtweaks.gui.HudElement;
import com.github.burgerguy.hudtweaks.util.gui.MatrixCache.UpdateEvent;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.AttackIndicator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class HotbarElement extends HudElement {

	public HotbarElement() {
		super("hotbar", UpdateEvent.ON_SCREEN_BOUNDS_CHANGE, UpdateEvent.ON_OFFHAND_STATUS_CHANGE, UpdateEvent.ON_HOTBAR_ATTACK_INDICATOR_CHANGE);
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
	public int getWidth(MinecraftClient client) {
		return 182 + getAttackIndicatorOffset(client) + getOffhandOffset(client);
	}

	@Override
	public int getHeight(MinecraftClient client) {
		return 24;
	}

	@Override
	public int getDefaultX(MinecraftClient client) {
		return client.getWindow().getScaledWidth() / 2 - 91 - getOffhandOffset(client);
	}

	@Override
	public int getDefaultY(MinecraftClient client) {
		return client.getWindow().getScaledHeight() - 24;
	}
	
}
