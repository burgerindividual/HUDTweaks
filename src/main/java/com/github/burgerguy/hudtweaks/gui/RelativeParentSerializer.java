package com.github.burgerguy.hudtweaks.gui;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class RelativeParentSerializer implements JsonSerializer<RelativeParent> {
	@Override
	public JsonElement serialize(RelativeParent src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.getIdentifier());
	}
}
