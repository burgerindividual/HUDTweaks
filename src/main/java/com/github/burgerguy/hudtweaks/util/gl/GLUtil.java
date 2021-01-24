package com.github.burgerguy.hudtweaks.util.gl;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

public enum GLUtil {
	;

	public static void drawBoxOutline(MatrixStack matrices, double x1, double y1, double x2, double y2, int color, float width) {
		double j;
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
		consumer.vertex(matrix, (float) x1, (float) y2, 0.0F).color(r, g, b, a).next();
		consumer.vertex(matrix, (float) x2, (float) y2, 0.0F).color(r, g, b, a).next();
		consumer.vertex(matrix, (float) x2, (float) y1, 0.0F).color(r, g, b, a).next();
		consumer.vertex(matrix, (float) x1, (float) y1, 0.0F).color(r, g, b, a).next();
		HTVertexConsumerProvider.draw();
	}
	
	public static void drawFillColor(MatrixStack matrices, double x1, double y1, double x2, double y2, int color) {
		double j;
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
		
		float f = (float) (color >> 24 & 255) / 255.0F;
		float g = (float) (color >> 16 & 255) / 255.0F;
		float h = (float) (color >> 8 & 255) / 255.0F;
		float k = (float) (color & 255) / 255.0F;
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.enableBlend();
		RenderSystem.disableTexture();
		RenderSystem.defaultBlendFunc();
		Matrix4f matrix = matrices.peek().getModel();
		bufferBuilder.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(matrix, (float) x1, (float) y2, 0.0F).color(g, h, k, f).next();
		bufferBuilder.vertex(matrix, (float) x2, (float) y2, 0.0F).color(g, h, k, f).next();
		bufferBuilder.vertex(matrix, (float) x2, (float) y1, 0.0F).color(g, h, k, f).next();
		bufferBuilder.vertex(matrix, (float) x1, (float) y1, 0.0F).color(g, h, k, f).next();
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}
}
