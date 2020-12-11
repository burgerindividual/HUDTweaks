package com.github.burgerguy.hudtweaks.gui.element;

import com.github.burgerguy.hudtweaks.gui.HudElement;
import com.github.burgerguy.hudtweaks.util.gui.MatrixCache.UpdateEvent;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.MathHelper;

public class ArmorElement extends HudElement {

	public ArmorElement() {
		super("armor", UpdateEvent.ON_SCREEN_BOUNDS_CHANGE, UpdateEvent.ON_HEALTH_ROWS_CHANGE);
	}

	@Override
	public int getWidth(MinecraftClient client) {
		return 81;
	}

	@Override
	public int getHeight(MinecraftClient client) {
		return 9;
	}

	@Override
	public int getDefaultX(MinecraftClient client) {
		return client.getWindow().getScaledWidth() / 2 - 91;
	}

	@Override
	public int getDefaultY(MinecraftClient client) {
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
}
