package com.github.burgerguy.hudtweaks.config;

import com.github.burgerguy.hudtweaks.hud.HudContainer;
import com.github.burgerguy.hudtweaks.util.Util;
import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

// TODO: make a more structured config with serializers and categories
// TODO: make a gui or use cloth config for section other than elements (or maybe merge the two, modmenu is root and game menu button is quick elements screen)
public class Config {

	private static final String SHOW_GAME_MENU_BUTTON_KEY = "showGameMenuButton";
	private static final String GENERAL_KEY = "general";
	private static final String ELEMENTS_KEY = "elements";

	private final Path configFilePath;
	public boolean showGameMenuButton;

	// TODO: create config in constructor?
	// defaults
	private Config(Path configFilePath) {
		this.configFilePath = configFilePath;
		this.showGameMenuButton = true;
	}

	private Config(Path configFilePath, JsonObject rootConfigObject) {
		this.configFilePath = configFilePath;
		JsonObject generalObject = rootConfigObject.get(GENERAL_KEY).getAsJsonObject();
		this.showGameMenuButton = generalObject.get(SHOW_GAME_MENU_BUTTON_KEY).getAsBoolean();
	}

	/**
	 * Tries to parse the configuration file.
	 */
	public static Config tryLoadConfig(Path configFilePath) {
		if (Files.exists(configFilePath)) {
			Util.LOGGER.info("Loading config file...");
			try (BufferedReader reader = new BufferedReader(new FileReader(configFilePath.toFile()))) {
				JsonObject rootConfigObject = JsonParser.parseReader(reader).getAsJsonObject();
				if (rootConfigObject.has(ELEMENTS_KEY)) {
					HudContainer.getElementRegistry().updateFromJson(rootConfigObject.get(ELEMENTS_KEY));
					return new Config(configFilePath, rootConfigObject);
				} else {
					// legacy support, update elements but not other settings
					HudContainer.getElementRegistry().updateFromJson(rootConfigObject);
				}
			} catch (JsonIOException e) {
				Util.LOGGER.error("Unable to read config file", e);
			} catch (JsonParseException e) {
				Util.LOGGER.error("Config file invalid", e);
			} catch (IOException e) {
				Util.LOGGER.error("Error loading config file", e);
			}
		} else {
			Util.LOGGER.info("Config file not found");
		}
		return new Config(configFilePath);
	}

	/**
	 * Saves the current HUD state as a json file for later reloading.
	 * This doesn't save all the information that would be in the class
	 * tree, just the things that would make sense in a config file.
	 */
	public void trySaveConfig() {
		Util.LOGGER.info("Saving config file...");
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFilePath.toFile()))) {
			JsonObject rootConfigObject = new JsonObject();
			JsonObject generalObject = new JsonObject();
			generalObject.add(SHOW_GAME_MENU_BUTTON_KEY, new JsonPrimitive(showGameMenuButton));
			rootConfigObject.add(GENERAL_KEY, generalObject);
			rootConfigObject.add(ELEMENTS_KEY, Util.GSON.toJsonTree(HudContainer.getElementRegistry()));
			Util.GSON.toJson(rootConfigObject, writer);
		} catch (JsonIOException e) {
			Util.LOGGER.error("Unable to write to config file", e);
		} catch (IOException e) {
			Util.LOGGER.error("Error saving config file", e);
		}
	}

}
