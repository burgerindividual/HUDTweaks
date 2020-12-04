package com.github.burgerguy.hudtweaks;

import com.github.burgerguy.hudtweaks.gui.HTOptionsScreen;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;

public class ModMenuApiImpl implements ModMenuApi {
	
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> new HTOptionsScreen(parent);
	}
}
