package com.github.burgerguy.hudtweaks.util.json;

import java.lang.reflect.Type;

import com.github.burgerguy.hudtweaks.hud.ElementRegistry;
import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import com.github.burgerguy.hudtweaks.hud.element.HudElementEntry;
import com.github.burgerguy.hudtweaks.hud.element.HudElementType;
import com.github.burgerguy.hudtweaks.hud.tree.AbstractTypeNodeEntry;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ElementRegistrySerializer implements JsonSerializer<ElementRegistry> {
	private static final Gson DEFAULT_GSON = new GsonBuilder().setPrettyPrinting().create();
	
	@Override
	public JsonElement serialize(ElementRegistry elementRegistry, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject registryObject = new JsonObject();
		for (HudElementType elementType : elementRegistry.getElementTypes()) {
			JsonObject elementTypeObject = new JsonObject();
			HTIdentifier activeIdentifier = elementType.getActiveEntry().getIdentifier();
			elementTypeObject.addProperty("activeEntry", activeIdentifier.getNamespace().toString() + ":" + activeIdentifier.getEntryName().toString());
			for (AbstractTypeNodeEntry abstractEntry : elementType.getRawEntryList()) {
				HudElementEntry entry = (HudElementEntry) abstractEntry;
				JsonObject xPosObject = new JsonObject();
				xPosObject.add("posType", context.serialize(entry.getXPosType()));
				xPosObject.addProperty("parent", entry.getXParent().getElementIdentifier().toString());
				xPosObject.add("anchorPos", context.serialize(entry.getXAnchorPos()));
				xPosObject.add("relativePos", context.serialize(entry.getXRelativePos()));
				xPosObject.add("offset", context.serialize(entry.getXOffset()));
				JsonObject yPosObject = new JsonObject();
				yPosObject.add("posType", context.serialize(entry.getYPosType()));
				yPosObject.addProperty("parent", entry.getYParent().getElementIdentifier().toString());
				yPosObject.add("anchorPos", context.serialize(entry.getYAnchorPos()));
				yPosObject.add("relativePos", context.serialize(entry.getYRelativePos()));
				yPosObject.add("offset", context.serialize(entry.getYOffset()));
				JsonObject entryObject = DEFAULT_GSON.toJsonTree(entry, TypeToken.of(Object.class).getType()).getAsJsonObject();
				entryObject.add("xPos", xPosObject);
				entryObject.add("yPos", yPosObject);
				entryObject.add("xScale", context.serialize(entry.getXScale()));
				entryObject.add("yScale", context.serialize(entry.getYScale()));
				HTIdentifier identifier = entry.getIdentifier();
				elementTypeObject.add(identifier.getNamespace().toString() + ":" + identifier.getEntryName().toString(), entryObject);
			}
			registryObject.add(elementType.toString(), elementTypeObject);
		}
		return registryObject;
	}


}
