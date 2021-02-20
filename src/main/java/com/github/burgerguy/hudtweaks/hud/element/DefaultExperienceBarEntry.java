package com.github.burgerguy.hudtweaks.hud.element;

import com.github.burgerguy.hudtweaks.gui.widget.HTButtonWidget;
import com.github.burgerguy.hudtweaks.gui.widget.SidebarWidget;
import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import com.github.burgerguy.hudtweaks.util.Util;
import com.google.gson.JsonElement;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;

public class DefaultExperienceBarEntry extends HudElementEntry {
	public transient static final HTIdentifier IDENTIFIER = new HTIdentifier(new HTIdentifier.ElementType("expbar", "hudtweaks.element.expbar"), Util.HUDTWEAKS_NAMESPACE);
	private boolean forceDisplay;

	public DefaultExperienceBarEntry() {
		super(IDENTIFIER);
	}

	@Override
	protected double calculateWidth(MinecraftClient client) {
		return 182;
	}

	@Override
	protected double calculateHeight(MinecraftClient client) {
		// TODO: is this right?
		return 12;
	}

	@Override
	protected double calculateDefaultX(MinecraftClient client) {
		return client.getWindow().getScaledWidth() / 2 - 91;
	}

	@Override
	protected double calculateDefaultY(MinecraftClient client) {
		return client.getWindow().getScaledHeight() - 36;
	}
	
	public boolean getForceDisplay() {
		return forceDisplay;
	}

	public void setForceDisplay(boolean forceDisplay) {
		this.forceDisplay = forceDisplay;
	}
	
	@Override
	public void updateFromJson(JsonElement json) {
		super.updateFromJson(json);
		setForceDisplay(json.getAsJsonObject().get("forceDisplay").getAsBoolean());
	}
	
	@Override
	public void fillSidebar(SidebarWidget sidebar) {
		super.fillSidebar(sidebar);
		sidebar.addDrawable(new HTButtonWidget(4, 276, sidebar.width - 8, 14, new TranslatableText("hudtweaks.options.forceDisplay.display", forceDisplay ? I18n.translate("hudtweaks.options.forceDisplay.on.display") : I18n.translate("hudtweaks.options.forceDisplay.off.display"))) {
			@Override
			public void onPress() {
				forceDisplay = !forceDisplay;
				setMessage(new TranslatableText("hudtweaks.options.forceDisplay.display", forceDisplay ? I18n.translate("hudtweaks.options.forceDisplay.on.display") : I18n.translate("hudtweaks.options.forceDisplay.off.display")));
				parentNode.setRequiresUpdate();
			}
		});
	}
	
	@Override
	public int getSidebarOptionsHeight() {
		return super.getSidebarOptionsHeight() + 25;
	}
}
