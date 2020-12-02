package com.github.burgerguy.hudtweaks.gui.element;

import java.awt.Point;

import com.github.burgerguy.hudtweaks.gui.HudElement;
import com.github.burgerguy.hudtweaks.util.gui.MatrixCache.UpdateEvent;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

public class AirElement extends HudElement {

	public AirElement() {
		super("air", UpdateEvent.ON_SCREEN_BOUNDS_CHANGE, UpdateEvent.ON_RIDING_HEALTH_ROWS_CHANGE);
	}

	@Override
	public int getWidth(MinecraftClient client) {
		return 81;
	}

	@Override
	public int getHeight(MinecraftClient client) {
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
					return ((int)Math.ceil((double)ridingHeartCount / 10.0D) - 1) * 10;
				}
			}
		}
		return 0;
	}

	@Override
	public Point calculateDefaultCoords(MinecraftClient client) {
		return new Point((client.getWindow().getScaledWidth() / 2) + 10, client.getWindow().getScaledHeight() - 49 - getRidingHealthOffset(client));
	}
	
}
