package com.github.burgerguy.hudtweaks;

import com.github.burgerguy.hudtweaks.api.HudElementOverride;
import com.github.burgerguy.hudtweaks.api.HudTweaksApi;
import com.github.burgerguy.hudtweaks.config.ConfigHelper;
import com.github.burgerguy.hudtweaks.hud.HudContainer;
import com.github.burgerguy.hudtweaks.hud.UpdateEvent;
import com.github.burgerguy.hudtweaks.hud.element.HudElement;

import com.github.burgerguy.hudtweaks.util.Util;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class HudTweaksMod implements ClientModInitializer {
	public static final String MOD_ID = "hudtweaks";
	
	@Override
	public void onInitializeClient() {
		HudContainer.init();
		FabricLoader.getInstance().getEntrypointContainers(MOD_ID, HudTweaksApi.class).forEach(e -> {
			HudTweaksApi apiImpl = e.getEntrypoint();
			apiImpl.onInitialize();

			for (UpdateEvent event : Util.emptyIfNull(apiImpl.getCustomEvents())) {
				HudContainer.getEventRegistry().put(event);
			}

			for (HudElement element : Util.emptyIfNull(apiImpl.getCustomElements())) {
				HudContainer.getElementRegistry().addElement(element);
			}

			for (HudElementOverride override : Util.emptyIfNull(apiImpl.getOverrides())) {
				HudContainer.getElementRegistry().addOverride(override);
			}
		});
		ConfigHelper.tryLoadConfig();
	}

}
