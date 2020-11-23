package com.github.burgerguy.hudtweaks.gui.widget;

import java.util.function.Consumer;

import com.github.burgerguy.hudtweaks.HudTweaksMod;
import com.github.burgerguy.hudtweaks.gui.HudPosHelper.Anchor;
import com.github.burgerguy.hudtweaks.util.Util;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public class AnchorButtonWidget extends AbstractButtonWidget {
	private static final Identifier ANCHORS_LOCATION = new Identifier(HudTweaksMod.MOD_ID, "anchor.png");
	private final boolean isXAnchor;
	private final Consumer<Anchor> onClick;
	
	private DisplayedAnchor currentAnchor;

	public AnchorButtonWidget(int x, int y, boolean isXAnchor, Consumer<Anchor> onClick) {
		super(x, y, 16, 16, LiteralText.EMPTY);
		this.isXAnchor = isXAnchor;
		this.onClick = onClick;
	}
	
	@Override
	public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
		if (currentAnchor != null) {
			MinecraftClient.getInstance().getTextureManager().bindTexture(ANCHORS_LOCATION);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			DrawableHelper.drawTexture(matrixStack, this.x, this.y, currentAnchor.ordinal() * this.width, this.isHovered() ? this.height : 0, this.width, this.height, 96, 32);
		}
	}
	
	@Override
	public void onClick(double mouseX, double mouseY) {
		cycleAnchor();
		this.onClick.accept(convertAnchor());
	}
	
	private void cycleAnchor() {
		switch(currentAnchor) {
			case TOP:
				this.currentAnchor = DisplayedAnchor.CENTER;
				break;
			case BOTTOM:
				this.currentAnchor = DisplayedAnchor.DEFAULT;
				break;
			case LEFT:
				this.currentAnchor = DisplayedAnchor.CENTER;
				break;
			case RIGHT:
				this.currentAnchor = DisplayedAnchor.DEFAULT;
				break;
			case CENTER:
				this.currentAnchor = isXAnchor ? DisplayedAnchor.RIGHT : DisplayedAnchor.BOTTOM;
				break;
			case DEFAULT:
				this.currentAnchor = isXAnchor ? DisplayedAnchor.LEFT : DisplayedAnchor.TOP;
				break;
			default:
				Util.LOGGER.error("Unexpected anchor type recieved: " + currentAnchor.toString());
				break;
		}
	}
	
	public void setAnchor(Anchor anchor) {
		switch(anchor) {
			case MINIMUM:
				this.currentAnchor = isXAnchor ? DisplayedAnchor.LEFT : DisplayedAnchor.TOP;
				break;
			case MAXIMUM:
				this.currentAnchor = isXAnchor ? DisplayedAnchor.RIGHT : DisplayedAnchor.BOTTOM;
				break;
			case CENTER:
				this.currentAnchor = DisplayedAnchor.CENTER;
				break;
			case DEFAULT:
				this.currentAnchor = DisplayedAnchor.DEFAULT;
				break;
			default:
				Util.LOGGER.error("Unexpected anchor type recieved: " + anchor.toString());
				break;
		}
	}
	
	private Anchor convertAnchor() {
		switch(currentAnchor) {
			case TOP:
				return Anchor.MINIMUM;
			case BOTTOM:
				return Anchor.MAXIMUM;
			case LEFT:
				return Anchor.MINIMUM;
			case RIGHT:
				return Anchor.MAXIMUM;
			case CENTER:
				return Anchor.CENTER;
			case DEFAULT:
				return Anchor.DEFAULT;
			default:
				Util.LOGGER.error("Unexpected anchor type recieved: " + currentAnchor.toString());
				return null;
		}
	}
	
	private enum DisplayedAnchor {
		TOP,
		BOTTOM,
		LEFT,
		RIGHT,
		CENTER,
		DEFAULT
	}
	
}
