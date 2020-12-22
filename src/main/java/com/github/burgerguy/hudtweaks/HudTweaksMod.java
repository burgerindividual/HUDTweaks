package com.github.burgerguy.hudtweaks;

import com.github.burgerguy.hudtweaks.api.HudInitializer;
import com.github.burgerguy.hudtweaks.config.ConfigHelper;
import com.github.burgerguy.hudtweaks.gui.HudContainer;
import com.github.burgerguy.hudtweaks.gui.HudElement;
import com.github.burgerguy.hudtweaks.util.gui.UpdateEvent;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class HudTweaksMod implements ClientModInitializer {
	public static final String MOD_ID = "hudtweaks";

	@Override
	public void onInitializeClient() {
		ConfigHelper.tryLoadConfig();
		FabricLoader.getInstance().getEntrypointContainers("hudtweaks", HudInitializer.class).forEach(e -> {
			HudInitializer hudInitializer = e.getEntrypoint();
			hudInitializer.onInitialize();
			
			for (UpdateEvent event : hudInitializer.getCustomEvents()) {
				HudContainer.getEventRegistry().put(event);
			}
			
			for (HudElement element : hudInitializer.getCustomElements()) {
				HudContainer.addElement(element.getIdentifier(), element);
			}
		});
	}
	
}
