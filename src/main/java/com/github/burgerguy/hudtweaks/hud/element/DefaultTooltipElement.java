package com.github.burgerguy.hudtweaks.hud.element;

import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import com.github.burgerguy.hudtweaks.mixin.InGameHudAccessor;
import com.github.burgerguy.hudtweaks.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

public class DefaultTooltipElement extends HudElement {
	public static final HTIdentifier IDENTIFIER = new HTIdentifier(Util.MINECRAFT_MODID, new HTIdentifier.ElementId("tooltip", "hudtweaks.element.tooltip"));
	
	public DefaultTooltipElement() {
		super(IDENTIFIER, "onHeldItemTickChange", "onHasStatusBarsChange");
	}
	
	@Override
	protected float calculateWidth(MinecraftClient client) {
		ItemStack currentHeldStack = ((InGameHudAccessor) client.inGameHud).getCurrentStack();
		MutableText stackText = Text.literal("").append(currentHeldStack.getName()).formatted(currentHeldStack.getRarity().formatting);
		if (currentHeldStack.hasCustomName()) {
			stackText.formatted(Formatting.ITALIC);
		}
		return client.textRenderer.getWidth(stackText);
	}
	
	@Override
	protected float calculateHeight(MinecraftClient client) {
		return client.textRenderer.fontHeight;
	}
	
	@Override
	protected float calculateDefaultX(MinecraftClient client) {
		return (client.getWindow().getScaledWidth() - getWidth() / xScale) / 2.0f;
	}
	
	@Override
	protected float calculateDefaultY(MinecraftClient client) {
		return client.getWindow().getScaledHeight() - (client.interactionManager.hasStatusBars() ? 59 : 45);
	}

}
