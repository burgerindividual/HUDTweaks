package com.github.burgerguy.hudtweaks.util.gl;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;

public final class GLUtil {
	private GLUtil() {
		// no instantiation, all contents static
	}

	public static final VertexConsumerProvider.Immediate VCP_INSTANCE = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());

	private static RenderLayer solidLineLayer;
	private static RenderLayer dashedLineLayer;
	// used to check if render layer needs to be recreated
	private static double solidLineWidth;
	private static double dashedLineWidth;
	
	public static void drawBoxOutline(MatrixStack matrices, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, int color, double lineWidth) {
		if (GLUtil.solidLineWidth != lineWidth) {
			GLUtil.solidLineWidth = lineWidth;
			solidLineLayer = HTRenderLayers.createSolidOutlineLayer(lineWidth);
		}

		int a = color >> 24 & 255;
		int r = color >> 16 & 255;
		int g = color >> 8 & 255;
		int b = color & 255;
		VertexConsumer consumer = VCP_INSTANCE.getBuffer(solidLineLayer);
		Matrix4f matrix = matrices.peek().getModel();
		consumer.vertex(matrix, x1, y1, 0.0F).color(r, g, b, a).next();
		consumer.vertex(matrix, x2, y2, 0.0F).color(r, g, b, a).next();
		consumer.vertex(matrix, x3, y3, 0.0F).color(r, g, b, a).next();
		consumer.vertex(matrix, x4, y4, 0.0F).color(r, g, b, a).next();
		consumer.vertex(matrix, x1, y1, 0.0F).color(r, g, b, a).next();
		VCP_INSTANCE.draw(solidLineLayer);
	}

	public static void drawDashedBoxOutline(MatrixStack matrices, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, int color, double lineWidth) {
		if (GLUtil.dashedLineWidth != lineWidth) {
			GLUtil.dashedLineWidth = lineWidth;
			dashedLineLayer = HTRenderLayers.createDashedOutlineLayer(lineWidth);
		}

		int a = color >> 24 & 255;
		int r = color >> 16 & 255;
		int g = color >> 8 & 255;
		int b = color & 255;
		VertexConsumer consumer = VCP_INSTANCE.getBuffer(RenderLayer.getLineStrip());
		Matrix4f matrix = matrices.peek().getModel();
		Matrix3f normal = matrices.peek().getNormal();
		consumer.vertex(matrix, x1, y1, 0.0F).color(r, g, b, a).normal(normal, 1, 1, 0).next();
		consumer.vertex(matrix, x2, y2, 0.0F).color(r, g, b, a).normal(normal, 1, 1, 0).next();
		consumer.vertex(matrix, x3, y3, 0.0F).color(r, g, b, a).normal(normal, 1, 1, 0).next();
		consumer.vertex(matrix, x4, y4, 0.0F).color(r, g, b, a).normal(normal, 1, 1, 0).next();
		consumer.vertex(matrix, x1, y1, 0.0F).color(r, g, b, a).normal(normal, 1, 1, 0).next();
		VCP_INSTANCE.draw(dashedLineLayer);
	}

	public static void drawFillColor(MatrixStack matrices, float x1, float y1, float x2, float y2, int color) {
		float j;
		if (x1 < x2) {
			j = x1;
			x1 = x2;
			x2 = j;
		}

		if (y1 < y2) {
			j = y1;
			y1 = y2;
			y2 = j;
		}

		int a = color >> 24 & 255;
		int r = color >> 16 & 255;
		int g = color >> 8 & 255;
		int b = color & 255;
		BufferBuilder builder = Tessellator.getInstance().getBuffer();
		RenderSystem.enableBlend();
		RenderSystem.disableTexture();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		Matrix4f matrix = matrices.peek().getModel();
		builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		builder.vertex(matrix, x1, y2, 0.0F).color(r, g, b, a).next();
		builder.vertex(matrix, x2, y2, 0.0F).color(r, g, b, a).next();
		builder.vertex(matrix, x2, y1, 0.0F).color(r, g, b, a).next();
		builder.vertex(matrix, x1, y1, 0.0F).color(r, g, b, a).next();
		builder.end();
		BufferRenderer.draw(builder);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}
}
