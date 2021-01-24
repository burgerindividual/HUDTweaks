package com.github.burgerguy.hudtweaks.gui.element;

import com.github.burgerguy.hudtweaks.gui.HudElement;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

public class AirElement extends HudElement {

	public AirElement() {
		super("air", "onRidingHealthRowsChange");
	}

	@Override
	protected double calculateWidth(MinecraftClient client) {
		return 81;
	}

	@Override
	protected double calculateHeight(MinecraftClient client) {
		return 9;
	}
	
	private int getRidingHealthOffset(MinecraftClient client) {
		Entity cameraEntity = client.getCameraEntity();
		if (cameraEntity != null && cameraEntity instanceof PlayerEntity) {
			Entity ridingEntity = cameraEntity.getVehicle();
			if (ridingEntity != null && ridingEntity instanceof LivingEntity) {
				LivingEntity livingEntity = (LivingEntity) ridingEntity;
				if (livingEntity.isLiving()) {
					int ridingHeartCount = MathHelper.clamp((int) (livingEntity.getMaxHealth() + 0.5F) / 2, 0, 30);
					return ((int)Math.ceil(ridingHeartCount / 10.0D) - 1) * 10;
				}
			}
		}
		return 0;
	}

	@Override
	protected double calculateDefaultX(MinecraftClient client) {
		return client.getWindow().getScaledWidth() / 2 + 10;
	}

	@Override
	protected double calculateDefaultY(MinecraftClient client) {
		return client.getWindow().getScaledHeight() - 49 - getRidingHealthOffset(client);
	}

	@Override
	protected boolean isVisible(MinecraftClient client) {
		return true;
	}
	
}
