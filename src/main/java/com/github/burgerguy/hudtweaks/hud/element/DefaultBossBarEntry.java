package com.github.burgerguy.hudtweaks.hud.element;

import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import com.github.burgerguy.hudtweaks.mixin.BossBarHudAccessor;
import com.github.burgerguy.hudtweaks.util.Util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ClientBossBar;

public class DefaultBossBarEntry extends HudElementEntry {
	public transient static final HTIdentifier IDENTIFIER = new HTIdentifier(new HTIdentifier.ElementType("bossbar", "hudtweaks.element.bossbar"), Util.MINECRAFT_NAMESPACE);

	public DefaultBossBarEntry() {
		super(IDENTIFIER, "onBossBarsChange");
	}

	@Override
	protected double calculateWidth(MinecraftClient client) {
		int widestText = 0;
		for (ClientBossBar bossBar : ((BossBarHudAccessor) client.inGameHud.getBossBarHud()).getBossBars().values()) {
			int textWidth = client.textRenderer.getWidth(bossBar.getName());
			if (textWidth > widestText) widestText = textWidth;
		}
		return Math.max(182, widestText);
	}

	@Override
	protected double calculateHeight(MinecraftClient client) {
		int maxBars = 0;
		int y = 12;
		while (true) {
			y += 19;
			if (y >= client.getWindow().getScaledHeight() / 3) break;
			maxBars++;
		}
		int bars = Math.min(Math.max(((BossBarHudAccessor) client.inGameHud.getBossBarHud()).getBossBars().size() - 1, 0), maxBars);
		return bars * 19 + 14;
	}

	@Override
	protected double calculateDefaultX(MinecraftClient client) {
		return client.getWindow().getScaledWidth() / 2 - (getWidth() / 2);
	}

	@Override
	protected double calculateDefaultY(MinecraftClient client) {
		return 3;
	}
}
