package com.github.burgerguy.hudtweaks.util.gl;

import com.github.burgerguy.hudtweaks.HudTweaksMod;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

import java.util.OptionalDouble;

public abstract class HTVertexConsumerProvider extends RenderLayer {
	private static final String DASHED_LAYER_NAME_PREFIX = HudTweaksMod.MOD_ID + "/dashed-line-layer-";
	private static final String SOLID_LAYER_NAME_PREFIX = HudTweaksMod.MOD_ID + "/solid-line-layer";
	private static final int EMPTY = 0; // this should never be used by the renderlayer unless something went really wrong
	private static final VertexConsumerProvider.Immediate vertexConsumerProvider = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());

	private HTVertexConsumerProvider() {
		// random values just so we can access the class
		super("HTAccessor", VertexFormats.POSITION, 0, 0, false, true, null, null);
	}

	// FIXME: doesn't work in 1.17
	public static RenderLayer createDashedOutlineLayer(Identifier texture, double lineWidth) {
		RenderLayer.MultiPhaseParameters parameters = RenderLayer.MultiPhaseParameters.builder()
				.texture(new RenderPhase.Texture(texture, false, false))
				.transparency(TRANSLUCENT_TRANSPARENCY)
				.fog(NO_FOG)
				.target(TRANSLUCENT_TARGET)
				.lineWidth(new LineWidth(OptionalDouble.of(lineWidth)))
				.build(false);

		return RenderLayer.of(DASHED_LAYER_NAME_PREFIX + texture, VertexFormats.POSITION_COLOR_TEXTURE, GL11.GL_LINE_STRIP, EMPTY, false, true, parameters);
	}

	public static RenderLayer createSolidOutlineLayer(double lineWidth) {
		RenderLayer.MultiPhaseParameters parameters = RenderLayer.MultiPhaseParameters.builder()
				.transparency(TRANSLUCENT_TRANSPARENCY)
				.fog(NO_FOG)
				.target(TRANSLUCENT_TARGET)
				.lineWidth(new LineWidth(OptionalDouble.of(lineWidth)))
				.build(false);

		return RenderLayer.of(SOLID_LAYER_NAME_PREFIX, VertexFormats.POSITION_COLOR, GL11.GL_LINE_LOOP, EMPTY, false, true, parameters);
	}

	public static VertexConsumer getConsumer(RenderLayer renderLayer) {
		return vertexConsumerProvider.getBuffer(renderLayer);
	}

	public static void draw() {
		vertexConsumerProvider.draw();
	}
}
