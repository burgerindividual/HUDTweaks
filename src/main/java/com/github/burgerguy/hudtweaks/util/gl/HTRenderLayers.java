package com.github.burgerguy.hudtweaks.util.gl;

import com.github.burgerguy.hudtweaks.HudTweaksMod;
import com.github.burgerguy.hudtweaks.mixin.RenderLayerAccessor;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

import java.util.OptionalDouble;

public final class HTRenderLayers extends RenderLayer {
	private static final String DASHED_LAYER_NAME = HudTweaksMod.MOD_ID + "/dashed-line-layer";
	private static final String SOLID_LAYER_NAME = HudTweaksMod.MOD_ID + "/solid-line-layer";
	public static RenderPhase.Shader dashedLinesShader;
	public static RenderPhase.Shader solidLinesShader; // minecraft has a built in one but it's slightly broken
	public static GlUniform dashOffset;
	public static GlUniform dashLength;

	private HTRenderLayers() {
		// random values just so we can access the class
		super("", null, null, 0, false, true, null, null);
	}

	public static RenderLayer createDashedOutlineLayer(double lineWidth) {
		RenderLayer.MultiPhaseParameters parameters = RenderLayer.MultiPhaseParameters.builder()
				.shader(dashedLinesShader)
				.transparency(TRANSLUCENT_TRANSPARENCY)
				.writeMaskState(COLOR_MASK)
				.depthTest(ALWAYS_DEPTH_TEST)
				.cull(DISABLE_CULLING)
				.layering(VIEW_OFFSET_Z_LAYERING)
				.lineWidth(new LineWidth(OptionalDouble.of(lineWidth)))
				.build(false);

		return RenderLayerAccessor.of(DASHED_LAYER_NAME, HTVertexFormats.LINES_MODIFIED, VertexFormat.DrawMode.LINES, 256, false, false, parameters);
	}

	public static RenderLayer createSolidOutlineLayer(double lineWidth) {
		RenderLayer.MultiPhaseParameters parameters = RenderLayer.MultiPhaseParameters.builder()
				.shader(solidLinesShader)
				.transparency(TRANSLUCENT_TRANSPARENCY)
				.writeMaskState(COLOR_MASK)
				.depthTest(ALWAYS_DEPTH_TEST)
				.cull(DISABLE_CULLING)
				.layering(VIEW_OFFSET_Z_LAYERING)
				.lineWidth(new LineWidth(OptionalDouble.of(lineWidth)))
				.build(false);

		return RenderLayerAccessor.of(SOLID_LAYER_NAME, VertexFormats.LINES, VertexFormat.DrawMode.LINES, 256, false, false, parameters);
	}

	public static void setDashOffset(float offset) {
		dashOffset.set(offset);
	}

	public static void setDashLength(float length) {
		dashLength.set(length);
	}
}
