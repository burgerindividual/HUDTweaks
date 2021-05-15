package com.github.burgerguy.hudtweaks.util.gl;

import java.util.OptionalDouble;

import org.lwjgl.opengl.GL11;

import com.github.burgerguy.hudtweaks.HudTweaksMod;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public abstract class HTVertexConsumerProvider extends RenderLayer {
	private static final String DASHED_LAYER_NAME_PREFIX = HudTweaksMod.MOD_ID + "/dashed-line-layer-";
	private static final String SOLID_LAYER_NAME_PREFIX = HudTweaksMod.MOD_ID + "/solid-line-layer";
	private static final int EMPTY = 0; // this should never be used by the renderlayer unless something went really wrong
	private static final VertexConsumerProvider.Immediate vertexConsumerProvider = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());

	private HTVertexConsumerProvider() {
		// random values just so we can access the class
		super("HTAccessor", VertexFormats.POSITION, 0, 0, false, true, null, null);
	}

	public static VertexConsumer getDashedOutlineConsumer(Identifier texture, double lineWidth) {
		RenderLayer.MultiPhaseParameters parameters = RenderLayer.MultiPhaseParameters.builder()
				.texture(new RenderPhase.Texture(texture, false, false))
				.transparency(TRANSLUCENT_TRANSPARENCY)
				.fog(NO_FOG)
				.target(TRANSLUCENT_TARGET)
				.lineWidth(new LineWidth(OptionalDouble.of(lineWidth)))
				.build(false);

		return vertexConsumerProvider.getBuffer(RenderLayer.of(DASHED_LAYER_NAME_PREFIX + texture, VertexFormats.POSITION_COLOR_TEXTURE, GL11.GL_LINE_STRIP, EMPTY, false, true, parameters));
	}

	public static VertexConsumer getSolidOutlineConsumer(double lineWidth) {
		RenderLayer.MultiPhaseParameters parameters = RenderLayer.MultiPhaseParameters.builder()
				.transparency(TRANSLUCENT_TRANSPARENCY)
				.fog(NO_FOG)
				.target(TRANSLUCENT_TARGET)
				.lineWidth(new LineWidth(OptionalDouble.of(lineWidth)))
				.build(false);

		return vertexConsumerProvider.getBuffer(RenderLayer.of(SOLID_LAYER_NAME_PREFIX, VertexFormats.POSITION_COLOR, GL11.GL_LINE_LOOP, EMPTY, false, true, parameters));
	}

	public static void draw() {
		vertexConsumerProvider.draw();
	}
}
