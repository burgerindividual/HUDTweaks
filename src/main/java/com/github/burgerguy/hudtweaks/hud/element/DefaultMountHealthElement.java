package com.github.burgerguy.hudtweaks.hud.element;

import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import com.github.burgerguy.hudtweaks.util.Util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

public class DefaultMountHealthElement extends HudElement {
	public static final HTIdentifier IDENTIFIER = new HTIdentifier(Util.MINECRAFT_MODID, new HTIdentifier.ElementId("mounthealth", "hudtweaks.element.mounthealth"));
	
	public DefaultMountHealthElement() {
		super(IDENTIFIER, "onRidingHealthRowsChange");
	}
	
	@Override
	protected double calculateWidth(MinecraftClient client) {
		return 81;
	}
	
	@Override
	protected double calculateHeight(MinecraftClient client) {
		int ridingHeartCount = 10;
		Entity cameraEntity = client.getCameraEntity();
		if (cameraEntity instanceof PlayerEntity) {
			Entity ridingEntity = cameraEntity.getVehicle();
			if (ridingEntity instanceof LivingEntity) {
				LivingEntity livingEntity = (LivingEntity) ridingEntity;
				if (livingEntity.isLiving()) {
					ridingHeartCount = MathHelper.clamp((int) (livingEntity.getMaxHealth() + 0.5F) / 2, 0, 30);
				}
			}
		}
		return Math.ceil(ridingHeartCount / 10.0D) * 9;
	}
	
	@Override
	protected double calculateDefaultX(MinecraftClient client) {
		return client.getWindow().getScaledWidth() / 2.0 + 10;
	}
	
	@Override
	protected double calculateDefaultY(MinecraftClient client) {
		// TODO: i think we can do getHeight here instead of getRawHeight, check this
		return client.getWindow().getScaledHeight() - 30 - getHeight();
	}
}