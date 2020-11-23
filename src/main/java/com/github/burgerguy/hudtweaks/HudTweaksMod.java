package com.github.burgerguy.hudtweaks;

import com.github.burgerguy.hudtweaks.config.ConfigHelper;

import net.fabricmc.api.ClientModInitializer;

public class HudTweaksMod implements ClientModInitializer {
	public static final String MOD_ID = "hudtweaks";

	@Override
	public void onInitializeClient() {
		ConfigHelper.tryLoadConfig();
	}
	
}
