package com.github.burgerguy.hudtweaks.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

/**
 * For small utilities that don't need their own class.
 */
public enum Util {
	;
	
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	public static final JsonParser JSON_PARSER = new JsonParser();
	public static final Logger LOGGER = LogManager.getLogger("HUDTweaks");
}
