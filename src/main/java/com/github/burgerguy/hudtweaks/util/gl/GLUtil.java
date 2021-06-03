package com.github.burgerguy.hudtweaks.util.gl;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.GL11;

public final class GLUtil {
	private GLUtil() {
		// no instantiation, all contents static
	}
	
	public static void drawBoxOutline(MatrixStack matrices, float x1, float y1, float x2, float y2, int color, float width) {
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
		Matrix4f matrix = matrices.peek().getModel();
		VertexConsumer consumer = HTVertexConsumerProvider.getSolidOutlineConsumer(width);
		consumer.vertex(matrix, x1, y2, 0.0F).color(r, g, b, a).next();
		consumer.vertex(matrix, x2, y2, 0.0F).color(r, g, b, a).next();
		consumer.vertex(matrix, x2, y1, 0.0F).color(r, g, b, a).next();
		consumer.vertex(matrix, x1, y1, 0.0F).color(r, g, b, a).next();
		HTVertexConsumerProvider.draw();
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

		float f = (color >> 24 & 255) / 255.0F;
		float g = (color >> 16 & 255) / 255.0F;
		float h = (color >> 8 & 255) / 255.0F;
		float k = (color & 255) / 255.0F;
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.enableBlend();
		RenderSystem.disableTexture();
		RenderSystem.defaultBlendFunc();
		Matrix4f matrix = matrices.peek().getModel();
		bufferBuilder.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(matrix, x1, y2, 0.0F).color(g, h, k, f).next();
		bufferBuilder.vertex(matrix, x2, y2, 0.0F).color(g, h, k, f).next();
		bufferBuilder.vertex(matrix, x2, y1, 0.0F).color(g, h, k, f).next();
		bufferBuilder.vertex(matrix, x1, y1, 0.0F).color(g, h, k, f).next();
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}
}
