package com.github.burgerguy.hudtweaks;

import com.github.burgerguy.hudtweaks.api.CustomHudElementEntry;
import com.github.burgerguy.hudtweaks.api.HudTweaksApi;
import com.github.burgerguy.hudtweaks.api.PlaceholderName;
import com.github.burgerguy.hudtweaks.config.ConfigHelper;
import com.github.burgerguy.hudtweaks.hud.HudContainer;
import com.github.burgerguy.hudtweaks.hud.UpdateEvent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class HudTweaksMod implements ClientModInitializer {
	public static final String MOD_ID = "hudtweaks";

	@Override
	public void onInitializeClient() {
		HudContainer.init();
		FabricLoader.getInstance().getEntrypointContainers("hudtweaks", HudTweaksApi.class).forEach(e -> {
			HudTweaksApi apiImpl = e.getEntrypoint();
			apiImpl.onInitialize();
			
			for (UpdateEvent event : apiImpl.getCustomEvents()) {
				HudContainer.getEventRegistry().put(event);
			}
			
			for (PlaceholderName element : apiImpl.getCustomElementEntries()) {
				HudContainer.getElementRegistry().addEntry(new CustomHudElementEntry(element));
			}
		});
		ConfigHelper.tryLoadConfig();
	}
	
}
