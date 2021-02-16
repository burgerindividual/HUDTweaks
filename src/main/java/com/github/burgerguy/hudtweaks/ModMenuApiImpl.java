package com.github.burgerguy.hudtweaks;

import com.github.burgerguy.hudtweaks.gui.HTOptionsScreen;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.terraformersmc.modmenu.config.ModMenuConfig;

public class ModMenuApiImpl implements ModMenuApi {
	private static boolean SHOULD_COMPENSATE_FOR_BUTTON = false;
	
	public ModMenuApiImpl() {
		// this will only be initialized if modmenu is present
		if (ModMenuConfig.MODIFY_GAME_MENU.getValue() && ModMenuConfig.MODS_BUTTON_STYLE.getValue().forGameMenu().equals(ModMenuConfig.ModsButtonStyle.CLASSIC)) {
			SHOULD_COMPENSATE_FOR_BUTTON = true;
		}
	}
	
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return HTOptionsScreen::new;
	}
	
	public static boolean shouldCompensateForButton() {
		return SHOULD_COMPENSATE_FOR_BUTTON;
	}
}
