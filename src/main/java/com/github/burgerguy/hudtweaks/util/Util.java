package com.github.burgerguy.hudtweaks.util;

import com.github.burgerguy.hudtweaks.config.ElementRegistrySerializer;
import com.github.burgerguy.hudtweaks.hud.ElementRegistry;
import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import com.github.burgerguy.hudtweaks.mixin.MinecraftClientAccessor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec2f;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Collections;

/**
 * For small utilities that don't need their own class.
 */
public final class Util {
	private Util() {
		// no instantiation, all contents static
	}

	public static final Gson GSON;
	public static final JsonParser JSON_PARSER = new JsonParser();
	public static final Logger LOGGER = LogManager.getLogger("HUDTweaks");

	public static final NumberFormat RELATIVE_POS_FORMATTER = new DecimalFormat("%##0.0");
	public static final NumberFormat ANCHOR_POS_FORMATTER = new DecimalFormat("%##0.0");
	public static final NumberFormat NUM_FIELD_FORMATTER = new DecimalFormat("####0.0");
	public static final HTIdentifier.ModId MINECRAFT_MODID = new HTIdentifier.ModId("minecraft");

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

	public static float minClamp(float value, float min, float max) {
		if (value < min || max <= min) {
			return min;
		} else {
			return Math.min(value, max);
		}
	}

	public static float getTrueTickDelta(MinecraftClient client) {
		return client.isPaused() ? ((MinecraftClientAccessor) client).getPausedTickDelta() : client.getTickDelta();
	}

	public static <T> Iterable<T> emptyIfNull(Iterable<T> iterable) {
		return iterable == null ? Collections.emptyList() : iterable;
	}

	public static Vec2f rotatePoint(float x, float y, float xOrigin, float yOrigin, double radians) {
		float sinResult = (float) Math.sin(radians);
		float cosResult = (float) Math.cos(radians);
		return new Vec2f(((x - xOrigin) * cosResult) - ((y - yOrigin) * sinResult) + xOrigin,
				((y - yOrigin) * cosResult) + ((x - xOrigin) * sinResult) + yOrigin);
	}
}
