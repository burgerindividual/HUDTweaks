package com.github.burgerguy.hudtweaks.hud.element;

import com.github.burgerguy.hudtweaks.asm.HTMixinPlugin;
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

public class DefaultBossBarElement extends HudElement {
	public static final HTIdentifier IDENTIFIER = new HTIdentifier(Util.MINECRAFT_MODID, new HTIdentifier.ElementId("bossbar", "hudtweaks.element.bossbar"));
	private static final float DEFAULT_MAX_HEIGHT = 1.0f / 3.0f;
	private float maxHeight = DEFAULT_MAX_HEIGHT;
	
	public DefaultBossBarElement() {
		super(IDENTIFIER, "onBossBarsChange");
	}
	
	@Override
	protected float calculateWidth(MinecraftClient client) {
		int widestText = 0;
		for (ClientBossBar bossBar : ((BossBarHudAccessor) client.inGameHud.getBossBarHud()).getBossBars().values()) {
			int textWidth = client.textRenderer.getWidth(bossBar.getName());
			if (textWidth > widestText) widestText = textWidth;
		}
		return Math.max(182, widestText);
	}
	
	@Override
	protected float calculateHeight(MinecraftClient client) {
		int maxBars = 0;
		float y = 12;
		while (true) {
			y += 19;
			if (y >= client.getWindow().getScaledHeight() * getScaledMaxHeight()) break;
			maxBars++;
		}
		int bars = Math.min(Math.max(((BossBarHudAccessor) client.inGameHud.getBossBarHud()).getBossBars().size() - 1, 0), maxBars);
		return bars * 19 + 14;
	}
	
	@Override
	protected float calculateDefaultX(MinecraftClient client) {
		return (client.getWindow().getScaledWidth() - getWidth() / xScale) / 2;
	}
	
	@Override
	protected float calculateDefaultY(MinecraftClient client) {
		return 3;
	}

	public float getRawMaxHeight() {
		return HTMixinPlugin.canUnrestrictBossBar() ? maxHeight : DEFAULT_MAX_HEIGHT;
	}

	public float getScaledMaxHeight() {
		return getRawMaxHeight() * (yScale == 0.0 ? 0.0f : (float) (1.0 / yScale));
	}

	public void setMaxHeight(float screenPercent) {
		maxHeight = screenPercent;
	}

	@Override
	public void updateFromJson(JsonElement json) {
		super.updateFromJson(json);
		setMaxHeight(json.getAsJsonObject().get("maxHeight").getAsFloat());
	}

	@Override
	public void fillSidebar(SidebarWidget sidebar) {
		super.fillSidebar(sidebar);
		sidebar.addPadding(6);
		sidebar.addEntry(new SidebarWidget.DrawableEntry<>(y -> {
			HTSliderWidget widget = new HTSliderWidget(4, y, sidebar.width - 8, 14, maxHeight) {
				@Override
				protected void updateMessage() {
					setMessage(new TranslatableText("hudtweaks.options.bossbar.style.screen_percent", Util.RELATIVE_POS_FORMATTER.format(value)));
				}

				@Override
				public void applyValue() {
					maxHeight = (float) value;
					containerNode.setRequiresUpdate();
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
			};

			widget.active = HTMixinPlugin.canUnrestrictBossBar();
			return widget;
		}, 14));
	}
}
