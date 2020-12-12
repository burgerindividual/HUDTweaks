package com.github.burgerguy.hudtweaks.gui;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class RelativeParentCache {
	private final Table<HudElement, Boolean, RelativeParent> relativeParentTable = HashBasedTable.create();
	
	public RelativeParent getOrCreate(HudElement element, boolean isX) {
		RelativeParent currentValue = relativeParentTable.get(element, isX);
		if (currentValue != null) {
			return currentValue;
		} else {
			RelativeParent newValue = new RelativeHudElementParent(element, isX);
			relativeParentTable.put(element, isX, newValue);
			return newValue;
		}
	}
}
