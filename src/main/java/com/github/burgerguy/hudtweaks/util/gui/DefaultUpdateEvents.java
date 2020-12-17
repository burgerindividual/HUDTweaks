package com.github.burgerguy.hudtweaks.util.gui;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.options.AttackIndicator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

public enum DefaultUpdateEvents {
	;
	
	public static final Set<UpdateEvent> EVENTS = ImmutableSet.of(
		new UpdateEvent() {
			@Override
			public String getIdentifier() {
				return "onRender";
			}

			@Override
			public boolean shouldUpdate(MinecraftClient client) {
				return true;
			}
		},
		new UpdateEvent() {
			private int lastWidth;
			private int lastHeight;
			
			@Override
			public String getIdentifier() {
				return "onScreenBoundsChange";
			}

			@Override
			public boolean shouldUpdate(MinecraftClient client) {
				int scaledWidth = client.getWindow().getScaledWidth();
				int scaledHeight = client.getWindow().getScaledHeight();
				
				if (scaledWidth != lastWidth || scaledHeight != lastHeight) {
					lastWidth = scaledWidth;
					lastHeight = scaledHeight;
					return true;
				}
				
				return false;
			}
		},
		new UpdateEvent() {
			private boolean lastOffhandStatus;
			
			@Override
			public String getIdentifier() {
				return "onOffhandStatusChange";
			}

			@Override
			public boolean shouldUpdate(MinecraftClient client) {
				Entity cameraEntity = client.getCameraEntity();
				if (cameraEntity != null && cameraEntity instanceof PlayerEntity) {
					boolean offhandStatus = ((PlayerEntity) cameraEntity).getOffHandStack().isEmpty();
					if (offhandStatus != lastOffhandStatus) {
						lastOffhandStatus = offhandStatus;
						return true;
					}
				}
				return false;
			}
		},
		new UpdateEvent() {
			private int lastHealthRows;
			
			@Override
			public String getIdentifier() {
				return "onHealthRowsChange";
			}

			@Override
			public boolean shouldUpdate(MinecraftClient client) {
				ClientPlayerEntity playerEntity = client.player;
				if (playerEntity != null) {
					double maxHealth = playerEntity.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
					int absorption = MathHelper.ceil(playerEntity.getAbsorptionAmount());
					int healthRows = MathHelper.ceil((maxHealth + absorption) / 2.0F / 10.0F);
					if (healthRows != lastHealthRows) {
						lastHealthRows = healthRows;
						return true;
					}
				}
				return false;
			}
		},
		new UpdateEvent() {
			private int lastRidingHealthRows;
			
			@Override
			public String getIdentifier() {
				return "onRidingHealthRowsChange";
			}

			@Override
			public boolean shouldUpdate(MinecraftClient client) {
				Entity cameraEntity = client.getCameraEntity();
				if (cameraEntity != null && cameraEntity instanceof PlayerEntity) {
					Entity ridingEntity = cameraEntity.getVehicle();
					if (ridingEntity != null && ridingEntity instanceof LivingEntity) {
						LivingEntity livingEntity = (LivingEntity) ridingEntity;
						if (livingEntity.isLiving()) {
							int ridingHeartCount = MathHelper.clamp((int) (livingEntity.getMaxHealth() + 0.5F) / 2, 0, 30);
							int ridingHealthRows = (int)Math.ceil(ridingHeartCount / 10.0D);
							if (ridingHealthRows != lastRidingHealthRows) {
								lastRidingHealthRows = ridingHealthRows;
								return true;
							}
						}
					}
				}
				return false;
			}
		},
		new UpdateEvent() {
			private StatusEffect[] lastStatusEffects;
			
			@Override
			public String getIdentifier() {
				return "onStatusEffectsChange";
			}

			@Override
			public boolean shouldUpdate(MinecraftClient client) {
				ClientPlayerEntity playerEntity = client.player;
				if (playerEntity != null) {
					Collection<StatusEffectInstance> effectInstances = playerEntity.getStatusEffects();
					StatusEffect[] statusEffects = new StatusEffect[effectInstances.size()];
					int i = 0;
					for(StatusEffectInstance effectInstance : effectInstances) {
						statusEffects[i++] = effectInstance.getEffectType();
					}
					if (lastStatusEffects == null || !Arrays.deepEquals(lastStatusEffects, statusEffects)) {
						lastStatusEffects = statusEffects;
						return true;
					}
				}
				return false;
			}
		},
		new UpdateEvent() {
			private AttackIndicator lastIndicator; // FIXME: figure out how to keep this for both x and y queries
			
			@Override
			public String getIdentifier() {
				return "onHotbarAttackIndicatorChange";
			}

			@Override
			public boolean shouldUpdate(MinecraftClient client) {
				AttackIndicator currentIndicator = client.options.attackIndicator;
				if (lastIndicator == null ||
					(!currentIndicator.equals(lastIndicator) &&
					(currentIndicator.equals(AttackIndicator.HOTBAR) || lastIndicator.equals(AttackIndicator.HOTBAR)))) {
					lastIndicator = currentIndicator;
					return true;
				}
				return false;
			}
		}
	);
}