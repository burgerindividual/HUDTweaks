package com.github.burgerguy.hudtweaks.gui.element;

import com.github.burgerguy.hudtweaks.gui.HudElement;
import com.github.burgerguy.hudtweaks.gui.widget.HTButtonWidget;
import com.github.burgerguy.hudtweaks.gui.widget.SidebarWidget;
import com.google.gson.JsonElement;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;

public class StatusEffectsElement extends HudElement {
	private boolean vertical;

	public StatusEffectsElement() {
		super("statuseffects", "onStatusEffectsChange");
	}

	// FIXME
	@Override
	public int calculateWidth(MinecraftClient client) {
		return 5;
	}

	@Override
	public int calculateHeight(MinecraftClient client) {
		return 5;
	}


	@Override
	public int calculateDefaultX(MinecraftClient client) {
		return 0;
	}

	@Override
	public int calculateDefaultY(MinecraftClient client) {
		return 0;
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
		sidebar.addDrawable(new HTButtonWidget(4, 225, sidebar.width - 8, 14, new TranslatableText("hudtweaks.options.statuseffects.style.display", vertical ? I18n.translate("hudtweaks.options.statuseffects.style.vertical.display") : I18n.translate("hudtweaks.options.statuseffects.style.horizontal.display"))) {
			@Override
			public void onPress() {
				vertical = !vertical;
				setMessage(new TranslatableText("hudtweaks.options.statuseffects.style.display", vertical ? I18n.translate("hudtweaks.options.statuseffects.style.vertical.display") : I18n.translate("hudtweaks.options.statuseffects.style.horizontal.display")));
				requiresUpdate = true;
			}
		});
	}
}
