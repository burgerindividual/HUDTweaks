package com.github.burgerguy.hudtweaks.hud;

import com.github.burgerguy.hudtweaks.mixin.BossBarHudAccessor;
import com.github.burgerguy.hudtweaks.mixin.InGameHudAccessor;
import com.github.burgerguy.hudtweaks.util.Util;
import com.google.common.collect.ImmutableSet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

import java.util.*;

public final class DefaultUpdateEvents {
	private DefaultUpdateEvents() {
		// no instantiation, all contents static
	}

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
					if (cameraEntity instanceof PlayerEntity) {
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
					if (cameraEntity instanceof PlayerEntity) {
						Entity ridingEntity = cameraEntity.getVehicle();
						if (ridingEntity instanceof LivingEntity livingEntity) {
							if (livingEntity.isLiving()) {
								int ridingHeartCount = MathHelper.clamp((int) (livingEntity.getMaxHealth() + 0.5F) / 2, 0, 30);
								int ridingHealthRows = (int) Math.ceil(ridingHeartCount / 10.0D);
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
						for (StatusEffectInstance effectInstance : effectInstances) {
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
				private SimpleOption<AttackIndicator> lastIndicator;

				@Override
				public String getIdentifier() {
					return "onHotbarAttackIndicatorChange";
				}

				@Override
				public boolean shouldUpdate(MinecraftClient client) {
					var currentIndicator = client.options.getAttackIndicator();
					if (lastIndicator == null || !currentIndicator.equals(lastIndicator) && (currentIndicator.equals(AttackIndicator.HOTBAR) || lastIndicator.equals(AttackIndicator.HOTBAR))) {
						lastIndicator = currentIndicator;
						return true;
					}
					return false;
				}
			},
			new UpdateEvent() {
				private ItemStack lastHeldStack;

				@Override
				public String getIdentifier() {
					return "onHeldItemTickChange"; // the itemstack referenced only updates per tick, rather than per frame
				}

				@Override
				public boolean shouldUpdate(MinecraftClient client) {
					ItemStack currentHeldStack = ((InGameHudAccessor) client.inGameHud).getCurrentStack();
					if (lastHeldStack == null || !ItemStack.areItemsEqualIgnoreDamage(lastHeldStack, currentHeldStack)) {
						lastHeldStack = currentHeldStack;
						return true;
					}
					return false;
				}
			},
			new UpdateEvent() {
				private Boolean lastHasStatusBars;

				@Override
				public String getIdentifier() {
					return "onHasStatusBarsChange";
				}

				@Override
				public boolean shouldUpdate(MinecraftClient client) {
					if (client.interactionManager == null) return false;
					boolean hasStatusBars = client.interactionManager.hasStatusBars();
					if ((lastHasStatusBars == null || !lastHasStatusBars.equals(hasStatusBars))) {
						lastHasStatusBars = hasStatusBars;
						return true;
					}
					return false;
				}
			},
			new UpdateEvent() {
				private Map<UUID, ClientBossBar> lastBossBars;

				@Override
				public String getIdentifier() {
					return "onBossBarsChange";
				}

				@Override
				public boolean shouldUpdate(MinecraftClient client) {
					Map<UUID, ClientBossBar> currentBossBars = ((BossBarHudAccessor) client.inGameHud.getBossBarHud()).getBossBars();
					if (!currentBossBars.equals(lastBossBars)) {
						lastBossBars = new HashMap<>(currentBossBars);
						return true;
					}
					return false;
				}
			},
			new UpdateEvent() {
				private Text lastActionBarText;
				private Boolean lastTimeThresholdResult;

				@Override
				public String getIdentifier() {
					return "onActionBarChange";
				}

				@Override
				public boolean shouldUpdate(MinecraftClient client) {
					boolean passed = false;

					boolean timeThresholdResult = ((InGameHudAccessor) client.inGameHud).getActionBarRemaining() - Util.getTrueTickDelta(client) > 160.0F / 255.0F;
					if (lastTimeThresholdResult == null || !lastTimeThresholdResult.equals(timeThresholdResult)) {
						lastTimeThresholdResult = timeThresholdResult;
						passed = true;
					}

					Text currentActionBarText = ((InGameHudAccessor) client.inGameHud).getActionBarText();
					if ((lastActionBarText == null && currentActionBarText != null) || (lastActionBarText != null && !lastActionBarText.equals(currentActionBarText))) {
						lastActionBarText = currentActionBarText;
						passed = true;
					}
					return passed;
				}
			},
			new UpdateEvent() {
				private Text lastTitleText;

				@Override
				public String getIdentifier() {
					return "onTitleTextChange";
				}

				@Override
				public boolean shouldUpdate(MinecraftClient client) {
					Text currentTitleText = ((InGameHudAccessor) client.inGameHud).getTitleText();
					if ((lastTitleText == null && currentTitleText != null) || (lastTitleText != null && !lastTitleText.equals(currentTitleText))) {
						lastTitleText = currentTitleText;
						return true;
					}
					return false;
				}
			},
			new UpdateEvent() {
				private Text lastSubtitleText;

				@Override
				public String getIdentifier() {
					return "onSubtitleTextChange";
				}

				@Override
				public boolean shouldUpdate(MinecraftClient client) {
					Text currentSubtitleText = ((InGameHudAccessor) client.inGameHud).getSubtitleText();
					if ((lastSubtitleText == null && currentSubtitleText != null) || (lastSubtitleText != null && !lastSubtitleText.equals(currentSubtitleText))) {
						lastSubtitleText = currentSubtitleText;
						return true;
					}
					return false;
				}
			},
			new UpdateEvent() {
				private Arm lastMainArm;

				@Override
				public String getIdentifier() {
					return "onMainArmChange";
				}

				@Override
				public boolean shouldUpdate(MinecraftClient client) {
					if (client.player == null) return false;
					Arm currentMainArm = client.player.getMainArm();
					if ((lastMainArm == null && currentMainArm != null) || (lastMainArm != null && !lastMainArm.equals(currentMainArm))) {
						lastMainArm = currentMainArm;
						return true;
					}
					return false;
				}
			}
	);
}