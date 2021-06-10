package com.github.burgerguy.hudtweaks.util.gl;

import com.github.burgerguy.hudtweaks.HudTweaksMod;
import com.github.burgerguy.hudtweaks.mixin.RenderLayerAccessor;
import ladysnake.satin.api.managed.ManagedCoreShader;
import ladysnake.satin.api.managed.ShaderEffectManager;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;

import java.util.OptionalDouble;

public abstract class HTRenderLayers extends RenderLayer {
	private static final String DASHED_LAYER_NAME = HudTweaksMod.MOD_ID + "/dashed-line-layer";
	private static final String SOLID_LAYER_NAME = HudTweaksMod.MOD_ID + "/solid-line-layer";
	private static ManagedCoreShader SATIN_DASHED_LINES_SHADER;

	public static void initializeShaders() {
		SATIN_DASHED_LINES_SHADER = ShaderEffectManager.getInstance().manageCoreShader(new Identifier(HudTweaksMod.MOD_ID, "dashed_lines"));
	}

	private HTRenderLayers() {
		// random values just so we can access the class
		super("", null, null, 0, false, true, null, null);
	}

	public static RenderLayer createDashedOutlineLayer(double lineWidth) {
		RenderLayer.MultiPhaseParameters parameters = RenderLayer.MultiPhaseParameters.builder()
				.shader(new RenderPhase.Shader(() -> SATIN_DASHED_LINES_SHADER.getProgram()))
				.transparency(TRANSLUCENT_TRANSPARENCY)
				//.target(TRANSLUCENT_TARGET)
				//.writeMaskState(ALL_MASK)
				//.cull(DISABLE_CULLING)
				//.layering(VIEW_OFFSET_Z_LAYERING)
				.lineWidth(new LineWidth(OptionalDouble.of(lineWidth)))
				.build(false);

		return RenderLayerAccessor.of(DASHED_LAYER_NAME, VertexFormats.LINES, VertexFormat.DrawMode.LINE_STRIP, 256, false, false, parameters);
	}

	public static RenderLayer createSolidOutlineLayer(double lineWidth) {
		RenderLayer.MultiPhaseParameters parameters = RenderLayer.MultiPhaseParameters.builder()
				.shader(COLOR_SHADER)
				.transparency(TRANSLUCENT_TRANSPARENCY)
				//.target(TRANSLUCENT_TARGET)
				//.writeMaskState(ALL_MASK)
				//.cull(DISABLE_CULLING)
				//.layering(VIEW_OFFSET_Z_LAYERING)
				.lineWidth(new LineWidth(OptionalDouble.of(lineWidth)))
				.build(false);

		return RenderLayerAccessor.of(SOLID_LAYER_NAME, VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.LINE_STRIP, 256, false, false, parameters);
	}
}
