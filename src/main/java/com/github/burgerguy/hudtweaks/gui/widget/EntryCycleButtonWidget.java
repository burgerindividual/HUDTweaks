package com.github.burgerguy.hudtweaks.gui.widget;

import com.github.burgerguy.hudtweaks.hud.element.HudElementType;
import com.github.burgerguy.hudtweaks.hud.tree.AbstractTypeNodeEntry;

import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class EntryCycleButtonWidget extends HTButtonWidget {
	private HudElementType elementType;
	
	public EntryCycleButtonWidget(int x, int y, int width, int height) {
		super(x, y, width, height, LiteralText.EMPTY);
	}

	@Override
	public void onPress() {
		elementType.cycleEntry();
		updateMessage();
	}
	
	private Text createMessage(AbstractTypeNodeEntry entry) {
		return elementType != null ? new LiteralText(entry.getIdentifier().getNamespace().toTranslatedString() + " - " + entry.getIdentifier().getEntryName().toTranslatedString()) : LiteralText.EMPTY;
	}
	
	public void setHudElementType(HudElementType elementType) {
		this.elementType = elementType;
		updateMessage();
		overflowTextRenderer.restart();
	}
	
	private void updateMessage() {
		setMessage(createMessage(elementType != null ? elementType.getActiveEntry() : null));
	}
}
