package com.github.burgerguy.hudtweaks.util.json;

import java.lang.reflect.Type;

import com.github.burgerguy.hudtweaks.gui.HudElement;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class HudElementSerializer implements JsonSerializer<HudElement> {
	private static final Gson DEFAULT_GSON = new GsonBuilder().setPrettyPrinting().create();

	@Override
	public JsonElement serialize(HudElement element, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject xPosObject = new JsonObject();
		xPosObject.add("posType", context.serialize(element.xPosType));
		xPosObject.add("parent", context.serialize(element.getXParent().getIdentifier()));
		xPosObject.add("anchorPos", context.serialize(element.xAnchorPos));
		xPosObject.add("relativePos", context.serialize(element.xRelativePos));
		xPosObject.add("offset", context.serialize(element.xOffset));
		JsonObject yPosObject = new JsonObject();
		yPosObject.add("posType", context.serialize(element.yPosType));
		yPosObject.add("parent", context.serialize(element.getYParent().getIdentifier()));
		yPosObject.add("anchorPos", context.serialize(element.yAnchorPos));
		yPosObject.add("relativePos", context.serialize(element.yRelativePos));
		yPosObject.add("offset", context.serialize(element.yOffset));
		JsonObject baseObject = DEFAULT_GSON.toJsonTree(element, TypeToken.of(Object.class).getType()).getAsJsonObject();
		baseObject.add("xPos", xPosObject);
		baseObject.add("yPos", yPosObject);
		baseObject.add("xScale", context.serialize(element.xScale));
		baseObject.add("yScale", context.serialize(element.yScale));
		return baseObject;
	}
	
}
