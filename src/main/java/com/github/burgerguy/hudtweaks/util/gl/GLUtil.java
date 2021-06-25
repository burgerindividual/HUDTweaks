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
		MatrixStack.Entry entry = matrices.peek();
		Matrix4f modelMatrix = entry.getModel();
		Matrix3f normalMatrix = entry.getNormal();
		addSolidLine(consumer, modelMatrix, normalMatrix, x1, y1, x2, y2, r, g, b, a);
		addSolidLine(consumer, modelMatrix, normalMatrix, x2, y2, x3, y3, r, g, b, a);
		addSolidLine(consumer, modelMatrix, normalMatrix, x3, y3, x4, y4, r, g, b, a);
		addSolidLine(consumer, modelMatrix, normalMatrix, x4, y4, x1, y1, r, g, b, a);
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
		BufferVertexConsumer consumer = (BufferBuilder) VCP_INSTANCE.getBuffer(dashedLineLayer);
		setupDashes(1000, 3.0F, x1, y1, x2, y2, x3, y3, x4, y4);
		MatrixStack.Entry entry = matrices.peek();
		Matrix4f modelMatrix = entry.getModel();
		Matrix3f normalMatrix = entry.getNormal();
		float currentDist = 0.0F;
		currentDist += addDashedLine(consumer, modelMatrix, normalMatrix, x1, y1, x2, y2, r, g, b, a, currentDist);
		currentDist += addDashedLine(consumer, modelMatrix, normalMatrix, x2, y2, x3, y3, r, g, b, a, currentDist);
		currentDist += addDashedLine(consumer, modelMatrix, normalMatrix, x3, y3, x4, y4, r, g, b, a, currentDist);
		addDashedLine(consumer, modelMatrix, normalMatrix, x4, y4, x1, y1, r, g, b, a, currentDist);
		VCP_INSTANCE.draw(dashedLineLayer);
	}

	/**
	 * This code is used to set up the dashes in a way that the dash length will change to make sure it wraps around the
	 * top left corner smoothly. It will round to the nearest length required to make this happen, so sometimes the dashes
	 * will be smaller or larger by a bit on some elements vs others.
	 */
	private static void setupDashes(int periodMillis, float targetDashLength, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
		float targetPeriodLength = targetDashLength * 2.0F;
		float totalDistance = 0;
		totalDistance += Math.sqrt(MathHelper.square(x2 - x1) + MathHelper.square(y2 - y1));
		totalDistance += Math.sqrt(MathHelper.square(x3 - x2) + MathHelper.square(y3 - y2));
		totalDistance += Math.sqrt(MathHelper.square(x4 - x3) + MathHelper.square(y4 - y3));
		totalDistance += Math.sqrt(MathHelper.square(x1 - x4) + MathHelper.square(y1 - y4));
		float extraLength = totalDistance % targetPeriodLength;
		float offset;
		if (extraLength > targetDashLength) {
			offset = targetPeriodLength - extraLength;
		} else {
			offset = -extraLength;
		}
		float multiplier = totalDistance / (totalDistance + offset);
		HTRenderLayers.setDashOffset(-(System.currentTimeMillis() % periodMillis) / 1000.0F * targetPeriodLength * multiplier);
		HTRenderLayers.setDashLength(targetDashLength * multiplier);
	}

	/**
	 * @return the distance between the two provided points
	 */
	private static float addDashedLine(BufferVertexConsumer consumer, Matrix4f modelMatrix, Matrix3f normalMatrix, float x1, float y1, float x2, float y2, int r, int g, int b, int a, float currentDist) {
		float xDiff = x2 - x1;
		float yDiff = y2 - y1;
		float lineDist = MathHelper.sqrt(xDiff * xDiff + yDiff * yDiff);
		xDiff /= lineDist;
		yDiff /= lineDist;

		consumer.vertex(modelMatrix, x1, y1, 0.0F).color(r, g, b, a).normal(normalMatrix, xDiff, yDiff, 0.0F);
		consumer.putFloat(0, currentDist);
		consumer.nextElement();
		consumer.next();
		consumer.vertex(modelMatrix, x2, y2, 0.0F).color(r, g, b, a).normal(normalMatrix, xDiff, yDiff, 0.0F);
		consumer.putFloat(0, currentDist + lineDist);
		consumer.nextElement();
		consumer.next();
		return lineDist;
	}

	private static void addSolidLine(VertexConsumer consumer, Matrix4f modelMatrix, Matrix3f normalMatrix, float x1, float y1, float x2, float y2, int r, int g, int b, int a) {
		float xDiff = x2 - x1;
		float yDiff = y2 - y1;
		float lineDist = MathHelper.sqrt(xDiff * xDiff + yDiff * yDiff);
		xDiff /= lineDist;
		yDiff /= lineDist;

		consumer.vertex(modelMatrix, x1, y1, 0.0F).color(r, g, b, a).normal(normalMatrix, xDiff, yDiff, 0.0F).next();
		consumer.vertex(modelMatrix, x2, y2, 0.0F).color(r, g, b, a).normal(normalMatrix, xDiff, yDiff, 0.0F).next();
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
