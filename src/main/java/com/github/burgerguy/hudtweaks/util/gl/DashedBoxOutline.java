package com.github.burgerguy.hudtweaks.util.gl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

public class DashedBoxOutline implements AutoCloseable {
	private static final String TEXTURE_NAME_PREFIX = "dashed-line-tex";
	
	private NativeImageBackedTexture texture;
	private Identifier texId;
	// items that can cause a texture update
	private int pattern;
	private byte patternLength;
	
	public void draw(MatrixStack matrices, int color, int pattern, byte patternLength, double x1, double y1, double x2, double y2, float width) {
		boolean newTex = false;
		if (this.patternLength != patternLength) {
			this.patternLength = patternLength;
			if (texture != null) texture.close();
			texture = new NativeImageBackedTexture(patternLength, 1, false);
			texId = MinecraftClient.getInstance().getTextureManager().registerDynamicTexture(TEXTURE_NAME_PREFIX, texture);
			newTex = true;
		}
		
		if (this.pattern != pattern || newTex) {
			this.pattern = pattern;
			
			NativeImage image = texture.getImage();
			for (int i = 0; i < patternLength; i++) {
				if (((pattern >> i) & 1) == 1) {
					image.setPixelColor(i, 0, 0xFFFFFFFF);
				} else {
					image.setPixelColor(i, 0, 0x00000000);
				}
			}
			
			texture.upload();
		}
		
		int a = color >> 24 & 255;
		int r = color >> 16 & 255;
		int g = color >> 8 & 255;
		int b = color & 255;		
		Matrix4f matrix = matrices.peek().getModel();
		VertexConsumer consumer = HTVertexConsumerProvider.getDashedOutlineConsumer(texId, width);
		consumer.vertex(matrix, (float) x1, (float) y2, 0.0F).color(r, g, b, a).texture(0.0F, 1).next();
		double wrapTexPos = calcTexLength(x1, y2, x2, y2);
		consumer.vertex(matrix, (float) x2, (float) y2, 0.0F).color(r, g, b, a).texture((float) wrapTexPos, 1).next();
		wrapTexPos += calcTexLength(x2, y2, x2, y1);
		consumer.vertex(matrix, (float) x2, (float) y1, 0.0F).color(r, g, b, a).texture((float) wrapTexPos, 1).next();
		wrapTexPos += calcTexLength(x2, y1, x1, y1);
		consumer.vertex(matrix, (float) x1, (float) y1, 0.0F).color(r, g, b, a).texture((float) wrapTexPos, 1).next();
		wrapTexPos += calcTexLength(x1, y1, x1, y2);
		consumer.vertex(matrix, (float) x1, (float) y2, 0.0F).color(r, g, b, a).texture((float) wrapTexPos, 1).next();
		HTVertexConsumerProvider.draw();
	}
	
	private double calcTexLength(double lastX, double lastY, double thisX, double thisY) {
		return Math.sqrt(Math.pow(thisX - lastX, 2) + Math.pow(thisY - lastY, 2)) / patternLength;
	}

	@Override
	public void close() {
		if (texture != null) texture.close();
	}
}
