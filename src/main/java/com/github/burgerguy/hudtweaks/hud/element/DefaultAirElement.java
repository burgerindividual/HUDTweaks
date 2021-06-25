package com.github.burgerguy.hudtweaks.hud.element;

import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import com.github.burgerguy.hudtweaks.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

public class DefaultAirElement extends HudElement {
	public static final HTIdentifier IDENTIFIER = new HTIdentifier(Util.MINECRAFT_MODID, new HTIdentifier.ElementId("air", "hudtweaks.element.air"));
	
	public DefaultAirElement() {
		super(IDENTIFIER, "onRidingHealthRowsChange");
	}
	
	@Override
	protected float calculateWidth(MinecraftClient client) {
		return 81;
	}
	
	@Override
	protected float calculateHeight(MinecraftClient client) {
		return 9;
	}

	private int getRidingHealthOffset(MinecraftClient client) {
		Entity cameraEntity = client.getCameraEntity();
		if (cameraEntity instanceof PlayerEntity) {
			Entity ridingEntity = cameraEntity.getVehicle();
			if (ridingEntity instanceof LivingEntity livingEntity) {
				if (livingEntity.isLiving()) {
					int ridingHeartCount = MathHelper.clamp((int) (livingEntity.getMaxHealth() + 0.5F) / 2, 0, 30);
					return ((int)Math.ceil(ridingHeartCount / 10.0D) - 1) * 10;
				}
			}
		}
		return 0;
	}
	
	@Override
	protected float calculateDefaultX(MinecraftClient client) {
		return client.getWindow().getScaledWidth() / 2.0f + 10;
	}
	
	@Override
	protected float calculateDefaultY(MinecraftClient client) {
		return client.getWindow().getScaledHeight() - 49 - getRidingHealthOffset(client);
	}
}
