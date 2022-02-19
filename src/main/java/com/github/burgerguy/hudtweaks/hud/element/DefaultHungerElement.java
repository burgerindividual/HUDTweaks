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
import net.minecraft.text.TranslatableText;

public class DefaultHungerElement extends HudElement {
	public static final HTIdentifier IDENTIFIER = new HTIdentifier(Util.MINECRAFT_MODID, new HTIdentifier.ElementId("hunger", "hudtweaks.element.hunger"));
	private static final boolean DEFAULT_FORCE_DISPLAY_VALUE = false;
	private boolean forceDisplay = DEFAULT_FORCE_DISPLAY_VALUE;
	
	public DefaultHungerElement() {
		super(IDENTIFIER);
	}
	
	@Override
	protected float calculateWidth(MinecraftClient client) {
		return 81;
	}
	
	@Override
	protected float calculateHeight(MinecraftClient client) {
		return 9 + 2; // the +2 is for the possible jump distance of the food
	}
	
	@Override
	protected float calculateDefaultX(MinecraftClient client) {
		return client.getWindow().getScaledWidth() / 2.0f + 10;
	}
	
	@Override
	protected float calculateDefaultY(MinecraftClient client) {
		return client.getWindow().getScaledHeight() - 39 - 1;  // the -1 is for the possible jump distance of the food
	}
	
	public boolean getForceDisplay() {
		return HTMixinPlugin.canForceDisplayHunger() ? forceDisplay : DEFAULT_FORCE_DISPLAY_VALUE;
	}
	
	public void setForceDisplay(boolean forceDisplay) {
		this.forceDisplay = forceDisplay;
	}

	@Override
	public void resetToDefaults() {
		super.resetToDefaults();
		forceDisplay = DEFAULT_FORCE_DISPLAY_VALUE;
	}
	
	@Override
	public void updateFromJson(JsonElement json) {
		super.updateFromJson(json);
		setForceDisplay(json.getAsJsonObject().get("forceDisplay").getAsBoolean());
	}

	@Override
	public void fillSidebar(SidebarWidget sidebar) {
		super.fillSidebar(sidebar);
		sidebar.addPadding(6);
		sidebar.addEntry(new SidebarWidget.DrawableEntry<>(y -> {
			HTButtonWidget widget = new HTOverflowButtonWidget(4, y, sidebar.width - 8, 14, new TranslatableText("hudtweaks.options.forceDisplay.display", forceDisplay ? I18n.translate("hudtweaks.options.forceDisplay.on.display") : I18n.translate("hudtweaks.options.forceDisplay.off.display"))) {
				@Override
				public void onPress() {
					forceDisplay = !forceDisplay;
					setMessage(new TranslatableText("hudtweaks.options.forceDisplay.display", forceDisplay ? I18n.translate("hudtweaks.options.forceDisplay.on.display") : I18n.translate("hudtweaks.options.forceDisplay.off.display")));
					containerNode.setRequiresUpdate();
				}
			};

			widget.active = HTMixinPlugin.canForceDisplayHunger();
			return widget;
		}, 14));
	}
}
