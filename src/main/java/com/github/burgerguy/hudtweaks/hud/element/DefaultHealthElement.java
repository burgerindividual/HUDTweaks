package com.github.burgerguy.hudtweaks.hud.element;

import com.github.burgerguy.hudtweaks.gui.widget.HTButtonWidget;
import com.github.burgerguy.hudtweaks.gui.widget.SidebarWidget;
import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import com.github.burgerguy.hudtweaks.util.Util;
import com.google.gson.JsonElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

public class DefaultHealthElement extends HudElement {
	public static final HTIdentifier IDENTIFIER = new HTIdentifier(Util.MINECRAFT_MODID, new HTIdentifier.ElementId("health", "hudtweaks.element.health"));
	private boolean flipped;
	
	public DefaultHealthElement() {
		super(IDENTIFIER, "onHealthRowsChange");
	}
	
	@Override
	protected float calculateWidth(MinecraftClient client) {
		return 81;
	}

	private float getRawHeight(MinecraftClient client) {
		double maxHealth = client.player.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
		int absorption = MathHelper.ceil(client.player.getAbsorptionAmount());
		int healthRows = MathHelper.ceil((maxHealth + absorption) / 2.0D / 10.0D);
		return (healthRows - 1) * Math.max(10 - (healthRows - 2), 3);
	}

	private int getHeartJumpDistance(MinecraftClient client) {
		if (flipped || client == null || client.player == null) {
			return 2;
		} else {
			// absorption hearts don't jump, so if we know the top row
			// is only absorption hearts, the distance will be 0.
			int heartsInTopRow = MathHelper.ceil(client.player.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH) / 2.0D) % 10;
			int absorption = MathHelper.ceil(client.player.getAbsorptionAmount());
			if (heartsInTopRow == 0) {
				return absorption > 0 ? 0 : 2;
			} else {
				return heartsInTopRow + absorption > 10 ? 0 : 2;
			}
		}
	}
	
	@Override
	protected float calculateHeight(MinecraftClient client) {
		if (client == null || client.player == null) {
			return 9 + getHeartJumpDistance(client);
		}
		else {
			return getRawHeight(client) + 9 + getHeartJumpDistance(client); // +9 because of the base heart height
		}
	}

	@Override
	protected float calculateDefaultX(MinecraftClient client) {
		return client.getWindow().getScaledWidth() / 2.0f - 91;
	}
	
	@Override
	protected float calculateDefaultY(MinecraftClient client) {
		return client.getWindow().getScaledHeight() - 39 - (flipped || client.player == null ? 0 : getRawHeight(client)) - getHeartJumpDistance(client);
	}

	public boolean isFlipped() {
		return flipped;
	}

	public void setFlipped(boolean flipped) {
		this.flipped = flipped;
	}

	@Override
	public void updateFromJson(JsonElement json) {
		super.updateFromJson(json);
		setFlipped(json.getAsJsonObject().get("flipped").getAsBoolean());
	}

	@Override
	public void fillSidebar(SidebarWidget sidebar) {
		super.fillSidebar(sidebar);
		sidebar.addDrawable(new HTButtonWidget(4, 276, sidebar.width - 8, 14, new TranslatableText("hudtweaks.options.health.style.display", flipped ? I18n.translate("hudtweaks.options.health.style.flipped.display") : I18n.translate("hudtweaks.options.health.style.normal.display"))) {
			@Override
			public void onPress() {
				flipped = !flipped;
				setMessage(new TranslatableText("hudtweaks.options.health.style.display", flipped ? I18n.translate("hudtweaks.options.health.style.flipped.display") : I18n.translate("hudtweaks.options.health.style.normal.display")));
				parentNode.setRequiresUpdate();
			}
		});
	}

	@Override
	public int getSidebarOptionsHeight() {
		return super.getSidebarOptionsHeight() + 25;
	}
}
