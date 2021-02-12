package com.github.burgerguy.hudtweaks;

import org.lwjgl.opengl.GLUtil;

import com.github.burgerguy.hudtweaks.api.HudTweaksApi;
import com.github.burgerguy.hudtweaks.config.ConfigHelper;
import com.github.burgerguy.hudtweaks.hud.HudContainer;
import com.github.burgerguy.hudtweaks.hud.UpdateEvent;
import com.github.burgerguy.hudtweaks.hud.element.HudElement;
import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class HudTweaksMod implements ClientModInitializer {
	public static final String MOD_ID = "hudtweaks";

	@Override
	public void onInitializeClient() {
		RenderSystem.recordRenderCall(() -> {
			GLUtil.setupDebugMessageCallback(System.out);
		});
		HudContainer.init();
		ConfigHelper.tryLoadConfig();
		FabricLoader.getInstance().getEntrypointContainers("hudtweaks", HudTweaksApi.class).forEach(e -> {
			HudTweaksApi apiImpl = e.getEntrypoint();
			apiImpl.onInitialize();
			
			for (UpdateEvent event : apiImpl.getCustomEvents()) {
				HudContainer.getEventRegistry().put(event);
			}
			
			for (HudElement element : apiImpl.getCustomElements()) {
				HudContainer.addElement(element.getIdentifier(), element);
			}
		});
	}
	
}
