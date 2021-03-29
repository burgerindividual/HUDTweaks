package com.github.burgerguy.hudtweaks.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.burgerguy.hudtweaks.hud.ElementRegistry;
import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import com.github.burgerguy.hudtweaks.hud.HTIdentifier.Namespace;
import com.github.burgerguy.hudtweaks.mixin.MinecraftClientAccessor;
import com.github.burgerguy.hudtweaks.util.json.ElementRegistrySerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import net.minecraft.client.MinecraftClient;

/**
 * For small utilities that don't need their own class.
 */
public enum Util {
	; // no instantiation, all contents static
	
	public static final Gson GSON;
	public static final JsonParser JSON_PARSER = new JsonParser();
	public static final Logger LOGGER = LogManager.getLogger("HUDTweaks");
	
	public static final NumberFormat RELATIVE_POS_FORMATTER = new DecimalFormat("%##0.0");
	public static final NumberFormat ANCHOR_POS_FORMATTER = new DecimalFormat("%##0.0");
	public static final NumberFormat NUM_FIELD_FORMATTER = new DecimalFormat("####0.0");
	public static final HTIdentifier.Namespace HUDTWEAKS_NAMESPACE = new Namespace("hudtweaks", "hudtweaks.name");
	public static final HTIdentifier.Namespace MINECRAFT_NAMESPACE = new Namespace("minecraft", "advancements.story.root.title");
	
	public static boolean SHOULD_COMPENSATE_FOR_MODMENU_BUTTON = false;
	
	static {
		GSON = new GsonBuilder().setPrettyPrinting().registerTypeHierarchyAdapter(ElementRegistry.class, new ElementRegistrySerializer()).create();
	}
	
	public static <T> boolean containsNotNull(Collection<T> collection, T item) {
		if (item == null) {
			return false;
		} else {
			return collection.contains(item);
		}
	}
	
	public static double minClamp(double value, double min, double max) {
		if (value < min || max <= min) {
			return min;
		} else {
			return value > max ? max : value;
		}
	}
	
	public static float getTrueTickDelta(MinecraftClient client) {
		return client.isPaused() ? ((MinecraftClientAccessor) client).getPausedTickDelta() : client.getTickDelta();
	}
}
