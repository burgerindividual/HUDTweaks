package com.github.burgerguy.hudtweaks.gui.element;

import com.github.burgerguy.hudtweaks.gui.HudElement;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.MathHelper;

public class ArmorElement extends HudElement {
	
	public ArmorElement() {
		super("armor", "onHealthRowsChange");
	}
	
	@Override
	protected double calculateWidth(MinecraftClient client) {
		return 81;
	}
	
	@Override
	protected double calculateHeight(MinecraftClient client) {
		return 9;
	}
	
	@Override
	protected double calculateDefaultX(MinecraftClient client) {
		return client.getWindow().getScaledWidth() / 2 - 91;
	}
	
	@Override
	protected double calculateDefaultY(MinecraftClient client) {
		int offsetHeight;
		if (client == null || client.player == null) {
			offsetHeight = 10;
		} else {
			double maxHealth = client.player.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
			int absorption = MathHelper.ceil(client.player.getAbsorptionAmount());
			int healthRows = MathHelper.ceil((maxHealth + absorption) / 2.0D / 10.0D);
			offsetHeight = (healthRows - 1) * Math.max(10 - (healthRows - 2), 3) + 10;
		}
		return client.getWindow().getScaledHeight() - 39 - offsetHeight;
	}
	
	@Override
	protected boolean isVisible(MinecraftClient client) {
		return true;
	}
}
