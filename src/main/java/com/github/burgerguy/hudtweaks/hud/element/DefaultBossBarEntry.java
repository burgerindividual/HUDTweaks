package com.github.burgerguy.hudtweaks.hud.element;

import com.github.burgerguy.hudtweaks.gui.widget.HTSliderWidget;
import com.github.burgerguy.hudtweaks.gui.widget.SidebarWidget;
import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import com.github.burgerguy.hudtweaks.mixin.BossBarHudAccessor;
import com.github.burgerguy.hudtweaks.util.Util;
import com.google.gson.JsonElement;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;

public class DefaultBossBarEntry extends HudElementEntry {
	public static final HTIdentifier IDENTIFIER = new HTIdentifier(new HTIdentifier.ElementType("bossbar", "hudtweaks.element.bossbar"), Util.MINECRAFT_NAMESPACE);
	private float maxHeight = (1.0f / 3.0f);

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
		float y = 12;
		while (true) {
			y += 19;
			if (y >= (float) client.getWindow().getScaledHeight() * getScaledMaxHeight()) break;
			maxBars++;
		}
		int bars = Math.min(Math.max(((BossBarHudAccessor) client.inGameHud.getBossBarHud()).getBossBars().size() - 1, 0), maxBars);
		return bars * 19 + 14;
	}

	@Override
	protected double calculateDefaultX(MinecraftClient client) {
		return (client.getWindow().getScaledWidth() - (getWidth() / xScale)) / 2;
	}

	@Override
	protected double calculateDefaultY(MinecraftClient client) {
		return 3;
	}
	
	public float getRawMaxHeight() {
		return maxHeight;
	}
	
	public float getScaledMaxHeight() {
		return maxHeight * (yScale == 0.0 ? 0.0f : (float) (1.0 / yScale));
	}
	
	public void setMaxHeight(float screenPercent) {
		this.maxHeight = screenPercent;
	}
	
	@Override
	public void updateFromJson(JsonElement json) {
		super.updateFromJson(json);
		setMaxHeight(json.getAsJsonObject().get("maxHeight").getAsFloat());
	}
	
	@Override
	public void fillSidebar(SidebarWidget sidebar) {
		super.fillSidebar(sidebar);
		sidebar.addDrawable(new HTSliderWidget(4, 276, sidebar.width - 8, 14, maxHeight) {
			@Override
			protected void updateMessage() {
				setMessage(new TranslatableText("hudtweaks.options.bossbar.style.screen_percent", Util.RELATIVE_POS_FORMATTER.format(value)));
			}
			
			@Override
			public void applyValue() {
				maxHeight = (float) value;
				parentNode.setRequiresUpdate();
			}
			
			@Override
			public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
				boolean bl = keyCode == 263;
				if (bl || keyCode == 262) {
					setValue(value + (bl ? -0.001 : 0.001));
					return true;
				}
				return false;
			}

			@Override
			public void updateValue() {
				value = MathHelper.clamp(yRelativePos, 0.0D, 1.0D);
				updateMessage();
			}
		});
	}
	
	@Override
	public int getSidebarOptionsHeight() {
		return super.getSidebarOptionsHeight() + 25;
	}
	
}
