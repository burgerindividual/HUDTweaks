package com.github.burgerguy.hudtweaks.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.logging.log4j.Level;

import com.github.burgerguy.hudtweaks.hud.HudContainer;
import com.github.burgerguy.hudtweaks.util.Util;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;

import net.fabricmc.loader.api.FabricLoader;

public enum ConfigHelper {
	;

	public static final Path configFile = FabricLoader.getInstance().getConfigDir().resolve("hudtweaks.json");

	/**
	 * Tries to parse the configuration file.
	 */
	public static void tryLoadConfig() {
		if (Files.exists(configFile)) {
			Util.LOGGER.log(Level.INFO, "Loading config file...");
			try (BufferedReader reader = new BufferedReader(new FileReader(configFile.toFile()))) {
				HudContainer.getElementRegistry().updateFromJson(Util.JSON_PARSER.parse(reader));
			} catch (JsonIOException e) {
				Util.LOGGER.error("Unable to read config file", e);
			} catch (JsonParseException e) {
				Util.LOGGER.error("Config file invalid", e);
			} catch (IOException e) {
				Util.LOGGER.error("Error loading config file", e);
			}
		} else {
			Util.LOGGER.log(Level.INFO, "Config file not found");
		}
	}

	/**
	 * Saves the current HUD state as a json file for later reloading.
	 * This doesn't save all the information that would be in the class
	 * tree, just the things that would make sense in a config file. The
	 * fields that aren't included are set as transient in their
	 * appropriate classes.
	 */
	public static void trySaveConfig() {
		Util.LOGGER.log(Level.INFO, "Saving config file...");
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile.toFile()))) {
			Util.GSON.toJson(HudContainer.getElementRegistry(), writer);
		} catch (JsonIOException e) {
			Util.LOGGER.error("Unable to write to config file", e);
		} catch (IOException e) {
			Util.LOGGER.error("Error saving config file", e);
		}
	}

}
