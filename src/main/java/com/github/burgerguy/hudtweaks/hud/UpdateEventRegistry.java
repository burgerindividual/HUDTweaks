package com.github.burgerguy.hudtweaks.hud;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class UpdateEventRegistry {
	private final Map<String, UpdateEvent> registeredEventMap = new HashMap<>();
	
	public UpdateEventRegistry() {
		for (UpdateEvent event : DefaultUpdateEvents.EVENTS) {
			put(event);
		}
	}
	
	public void put(UpdateEvent event) {
		registeredEventMap.putIfAbsent(event.getIdentifier(), event);
	}
	
	public UpdateEvent get(String identifier) {
		return registeredEventMap.get(identifier);
	}
	
	public Collection<UpdateEvent> getAllEvents() {
		return registeredEventMap.values();
	}
}
