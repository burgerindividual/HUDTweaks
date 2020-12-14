package com.github.burgerguy.hudtweaks.util.json;

import java.lang.reflect.Type;

import com.github.burgerguy.hudtweaks.gui.HudElement;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class HudElementSerializer implements JsonSerializer<HudElement> {

	@Override
	public JsonElement serialize(HudElement element, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject xPosObject = new JsonObject();
		xPosObject.add("posType", context.serialize(element.xPosType));
		xPosObject.add("parent", new JsonPrimitive(element.getXParent().getIdentifier()));
		xPosObject.add("anchorPos", new JsonPrimitive(element.xAnchorPos));
		xPosObject.add("relativePos", new JsonPrimitive(element.xRelativePos));
		xPosObject.add("offset", new JsonPrimitive(element.xOffset));
		JsonObject yPosObject = new JsonObject();
		yPosObject.add("posType", context.serialize(element.yPosType));
		yPosObject.add("parent", new JsonPrimitive(element.getYParent().getIdentifier()));
		yPosObject.add("anchorPos", new JsonPrimitive(element.yAnchorPos));
		yPosObject.add("relativePos", new JsonPrimitive(element.yRelativePos));
		yPosObject.add("offset", new JsonPrimitive(element.yOffset));
		JsonObject baseObject = context.serialize((Object) element).getAsJsonObject();
		baseObject.add("xPos", xPosObject);
		baseObject.add("yPos", yPosObject);
		return baseObject;
	}
	
}
