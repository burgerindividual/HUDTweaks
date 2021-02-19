package com.github.burgerguy.hudtweaks.hud.element;

import com.github.burgerguy.hudtweaks.gui.widget.HTButtonWidget;
import com.github.burgerguy.hudtweaks.gui.widget.SidebarWidget;
import com.google.gson.JsonElement;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.text.TranslatableText;

public class DefaultStatusEffectsEntry extends HudElementEntry {
	private boolean vertical;
	
	public DefaultStatusEffectsEntry() {
		super(new HTIdentifier(new HTIdentifier.ElementType("statuseffects", "hudtweaks.element.statuseffects")), "onStatusEffectsChange");
	}
	
	@Override
	protected double calculateWidth(MinecraftClient client) {
		return vertical ? getRawHeight(client) : getRawWidth(client);
	}
	
	@Override
	protected double calculateHeight(MinecraftClient client) {
		return vertical ? getRawWidth(client) : getRawHeight(client);
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
			
			if (!hasBeneficial && hasOther) return 26;
		}
		return 0;
	}
	
	@Override
	protected double calculateDefaultX(MinecraftClient client) {
		return client.getWindow().getScaledWidth() - calculateWidth(client) - 1 - (vertical ? getNonBeneficialOffset(client) : 0);
	}
	
	@Override
	protected double calculateDefaultY(MinecraftClient client) {
		return (client.isDemo() ? 16 : 1) + (vertical ? 0 : getNonBeneficialOffset(client));
	}
	
	public boolean isVertical() {
		return vertical;
	}
	
	public void setVertical(boolean vertical) {
		this.vertical = vertical;
	}
	
	@Override
	public void updateFromJson(JsonElement json) {
		super.updateFromJson(json);
		setVertical(json.getAsJsonObject().get("vertical").getAsBoolean());
	}
	
	@Override
	public void fillSidebar(SidebarWidget sidebar) {
		super.fillSidebar(sidebar);
		sidebar.addDrawable(new HTButtonWidget(4, 276, sidebar.width - 8, 14, new TranslatableText("hudtweaks.options.statuseffects.style.display", vertical ? I18n.translate("hudtweaks.options.statuseffects.style.vertical.display") : I18n.translate("hudtweaks.options.statuseffects.style.horizontal.display"))) {
			@Override
			public void onPress() {
				vertical = !vertical;
				setMessage(new TranslatableText("hudtweaks.options.statuseffects.style.display", vertical ? I18n.translate("hudtweaks.options.statuseffects.style.vertical.display") : I18n.translate("hudtweaks.options.statuseffects.style.horizontal.display")));
				setRequiresUpdate();
			}
		});
	}
	
	@Override
	public int getSidebarOptionsHeight() {
		return super.getSidebarOptionsHeight() + 25;
	}
}
