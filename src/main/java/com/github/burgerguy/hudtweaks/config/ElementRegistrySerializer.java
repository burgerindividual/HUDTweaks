package com.github.burgerguy.hudtweaks.config;

import com.github.burgerguy.hudtweaks.api.HudElementOverride;
import com.github.burgerguy.hudtweaks.hud.ElementRegistry;
import com.github.burgerguy.hudtweaks.hud.element.HudElement;
import com.github.burgerguy.hudtweaks.hud.element.HudElementContainer;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.List;

public class ElementRegistrySerializer implements JsonSerializer<ElementRegistry> {
	private static final Gson DEFAULT_GSON = new GsonBuilder().setPrettyPrinting().create();
	
	@Override
	public JsonElement serialize(ElementRegistry elementRegistry, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject registryObject = new JsonObject();
		for (HudElementContainer elementContainer : elementRegistry.getElementContainers()) {
			HudElement initialElement = elementContainer.getInitialElement();
			JsonObject elementContainerObject = serializeHudElement(initialElement, context); // initial element data should be in root of element container
			List<HudElementOverride> overrides = elementContainer.getOverrides();
			if (!overrides.isEmpty()) {
				JsonObject overridesObject = new JsonObject();
				for (HudElementOverride override : elementContainer.getOverrides()) {
					overridesObject.add(override.getElement().getIdentifier().toString(), serializeHudElement(override.getElement(), context));
				}
				elementContainerObject.add("overrides", overridesObject);
			}
			registryObject.add(initialElement.getIdentifier().toString(), elementContainerObject);
		}
		return registryObject;
	}

	private static JsonObject serializeHudElement(HudElement element, JsonSerializationContext context) {
		JsonObject xPosObject = new JsonObject();
		xPosObject.add("posType", context.serialize(element.getXPosType()));
		xPosObject.addProperty("parent", element.getXParent().getInitialElement().getIdentifier().toString());
		xPosObject.add("anchorPos", context.serialize(element.getXAnchorPos()));
		xPosObject.add("relativePos", context.serialize(element.getXRelativePos()));
		xPosObject.add("offset", context.serialize(element.getXOffset()));
		JsonObject yPosObject = new JsonObject();
		yPosObject.add("posType", context.serialize(element.getYPosType()));
		yPosObject.addProperty("parent", element.getYParent().getInitialElement().getIdentifier().toString());
		yPosObject.add("anchorPos", context.serialize(element.getYAnchorPos()));
		yPosObject.add("relativePos", context.serialize(element.getYRelativePos()));
		yPosObject.add("offset", context.serialize(element.getYOffset()));
		JsonObject entryObject = DEFAULT_GSON.toJsonTree(element, TypeToken.of(Object.class).getType()).getAsJsonObject();
		entryObject.add("xPos", xPosObject);
		entryObject.add("yPos", yPosObject);
		entryObject.add("xScale", context.serialize(element.getXScale()));
		entryObject.add("yScale", context.serialize(element.getYScale()));
		return entryObject;
	}


}
