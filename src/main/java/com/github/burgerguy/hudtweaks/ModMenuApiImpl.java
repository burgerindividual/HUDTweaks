package com.github.burgerguy.hudtweaks;

import com.github.burgerguy.hudtweaks.gui.HTOptionsScreen;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuApiImpl implements ModMenuApi {
	
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return HTOptionsScreen::new;
	}
}
