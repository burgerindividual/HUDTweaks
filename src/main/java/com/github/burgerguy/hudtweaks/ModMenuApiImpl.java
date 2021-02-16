package com.github.burgerguy.hudtweaks;

import com.github.burgerguy.hudtweaks.gui.HTOptionsScreen;
import com.github.burgerguy.hudtweaks.util.Util;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.terraformersmc.modmenu.config.ModMenuConfig;

public class ModMenuApiImpl implements ModMenuApi {
	
	public ModMenuApiImpl() {
		// this will only be initialized if modmenu is present
		if (ModMenuConfig.MODIFY_GAME_MENU.getValue() && ModMenuConfig.MODS_BUTTON_STYLE.getValue().forGameMenu().equals(ModMenuConfig.ModsButtonStyle.CLASSIC)) {
			Util.SHOULD_COMPENSATE_FOR_MODMENU_BUTTON = true;
		}
	}
	
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return HTOptionsScreen::new;
	}
}
