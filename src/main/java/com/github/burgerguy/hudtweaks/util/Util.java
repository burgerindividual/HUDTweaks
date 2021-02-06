package com.github.burgerguy.hudtweaks.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.burgerguy.hudtweaks.hud.element.HudElement;
import com.github.burgerguy.hudtweaks.util.json.HudElementSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

/**
 * For small utilities that don't need their own class.
 */
public enum Util {
	;
	
	public static final Gson GSON;
	public static final JsonParser JSON_PARSER = new JsonParser();
	public static final Logger LOGGER = LogManager.getLogger("HUDTweaks");
	
	public static final NumberFormat RELATIVE_POS_FORMATTER = new DecimalFormat("%##0.0");
	public static final NumberFormat ANCHOR_POS_FORMATTER = new DecimalFormat("%##0.0");
	public static final NumberFormat NUM_FIELD_FORMATTER = new DecimalFormat("####0.0");
	
	static {
		GSON = new GsonBuilder().setPrettyPrinting().registerTypeHierarchyAdapter(HudElement.class, new HudElementSerializer()).create();
	}
	
	public static <T> boolean containsNotNull(Set<T> set, T item) {
		if (item == null) {
			return false;
		} else {
			return set.contains(item);
		}
	}
}
