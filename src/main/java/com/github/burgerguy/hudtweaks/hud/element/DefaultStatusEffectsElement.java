package com.github.burgerguy.hudtweaks.hud.element;

import com.github.burgerguy.hudtweaks.asm.HTMixinPlugin;
import com.github.burgerguy.hudtweaks.gui.widget.HTButtonWidget;
import com.github.burgerguy.hudtweaks.gui.widget.HTOverflowButtonWidget;
import com.github.burgerguy.hudtweaks.gui.widget.SidebarWidget;
import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import com.github.burgerguy.hudtweaks.util.Util;
import com.google.gson.JsonElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.text.Text;

public class DefaultStatusEffectsElement extends HudElement {
	public static final HTIdentifier IDENTIFIER = new HTIdentifier(Util.MINECRAFT_MODID, new HTIdentifier.ElementId("statuseffects", "hudtweaks.element.statuseffects"));
	private static final boolean DEFAULT_VERTICAL_VALUE = false;
	private boolean vertical = DEFAULT_VERTICAL_VALUE;

	public DefaultStatusEffectsElement() {
		super(IDENTIFIER, "onStatusEffectsChange");
	}

	@Override
	protected float calculateWidth(MinecraftClient client) {
		return isVertical() ? getRawHeight(client) : getRawWidth(client);
	}

	@Override
	protected float calculateHeight(MinecraftClient client) {
		return isVertical() ? getRawWidth(client) : getRawHeight(client);
	}

	private int getRawWidth(MinecraftClient client) {
		int beneficial = 0;
		int other = 0;
		for (StatusEffectInstance effect : client.player.getStatusEffects()) {
			if (effect.getEffectType().isBeneficial()) {
				beneficial++;
			} else {
				other++;
			}
		}
		return Math.max(Math.max(beneficial, other), 1) * 25 - 1; // atleast show area for 1
	}

	private int getRawHeight(MinecraftClient client) {
		boolean hasBeneficial = false;
		boolean hasOther = false;
		for (StatusEffectInstance effect : client.player.getStatusEffects()) {
			if (effect.getEffectType().isBeneficial()) {
				hasBeneficial = true;
			} else {
				hasOther = true;
			}

			if (hasBeneficial && hasOther) return 50;
		}
		return 24;
	}

	private int getNonBeneficialOffset(MinecraftClient client) {
		boolean hasBeneficial = false;
		boolean hasOther = false;
		for (StatusEffectInstance effect : client.player.getStatusEffects()) {
			if (effect.getEffectType().isBeneficial()) {
				hasBeneficial = true;
			} else {
				hasOther = true;
			}
		}
		return !hasBeneficial && hasOther ? 26 : 0;
	}

	@Override
	protected float calculateDefaultX(MinecraftClient client) {
		return client.getWindow().getScaledWidth() - calculateWidth(client) - 1 - (isVertical() ? getNonBeneficialOffset(client) : 0);
	}

	@Override
	protected float calculateDefaultY(MinecraftClient client) {
		return (client.isDemo() ? 16 : 1) + (isVertical() ? 0 : getNonBeneficialOffset(client));
	}

	public boolean isVertical() {
		return HTMixinPlugin.canForceEffectsVertical() ? vertical : DEFAULT_VERTICAL_VALUE;
	}

	public void setVertical(boolean vertical) {
		this.vertical = vertical;
	}

	@Override
	public void resetToDefaults() {
		super.resetToDefaults();
		vertical = DEFAULT_VERTICAL_VALUE;
	}

	@Override
	public void updateFromJson(JsonElement json) {
		super.updateFromJson(json);
		setVertical(json.getAsJsonObject().get("vertical").getAsBoolean());
	}

	@Override
	public void fillSidebar(SidebarWidget sidebar) {
		super.fillSidebar(sidebar);
		sidebar.addPadding(6);
		sidebar.addEntry(new SidebarWidget.DrawableEntry<>(y -> {
			HTButtonWidget widget = new HTOverflowButtonWidget(4, y, sidebar.width - 8, 14, Text.translatable("hudtweaks.options.statuseffects.style.display", vertical ? I18n.translate("hudtweaks.options.statuseffects.style.vertical.display") : I18n.translate("hudtweaks.options.statuseffects.style.horizontal.display"))) {
				@Override
				public void onPress() {
					vertical = !vertical;
					setMessage(Text.translatable("hudtweaks.options.statuseffects.style.display", vertical ? I18n.translate("hudtweaks.options.statuseffects.style.vertical.display") : I18n.translate("hudtweaks.options.statuseffects.style.horizontal.display")));
					containerNode.setRequiresUpdate();
				}
			};

			widget.active = HTMixinPlugin.canForceEffectsVertical();
			return widget;
		}, 14));
	}
}
