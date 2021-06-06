package com.github.burgerguy.hudtweaks.util.gl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;

public class DashedBoxOutline implements AutoCloseable {
	private static final String TEXTURE_NAME_PREFIX = "dashed-line-tex";

	private NativeImageBackedTexture texture;
	private Identifier texId;
	private RenderLayer renderLayer;
	private byte patternOffset;
	// items that can cause a render layer update
	private double lineWidth;
	// items that can cause a texture update
	private int pattern;
	private byte patternLength;

	public void draw(MatrixStack matrices, int color, int pattern, byte patternLength, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, double lineWidth) {
		boolean texUpdated = false;
		if (this.patternLength != patternLength) {
			this.patternLength = patternLength;
			if (texture != null) texture.close();
			texture = new NativeImageBackedTexture(patternLength, 1, false);
			texId = MinecraftClient.getInstance().getTextureManager().registerDynamicTexture(TEXTURE_NAME_PREFIX, texture);
			texUpdated = true;
		}

		if (this.pattern != pattern || texUpdated) {
			this.pattern = pattern;

			NativeImage image = texture.getImage();
			for (int i = 0; i < patternLength; i++) {
				image.setPixelColor(i, 0, (pattern >> i & 1) == 1 ? 0xFFFFFFFF : 0x00000000); // uses the bit at the corresponding index
			}

			texture.upload();
			texUpdated = true;
		}

		if (this.lineWidth != lineWidth || texUpdated) {
			this.lineWidth = lineWidth;
			renderLayer = HTVertexConsumerProvider.createDashedOutlineLayer(texId, lineWidth);
		}

		int a = color >> 24 & 255;
		int r = color >> 16 & 255;
		int g = color >> 8 & 255;
		int b = color & 255;
		Matrix4f matrix = matrices.peek().getModel();
		VertexConsumer consumer = HTVertexConsumerProvider.getConsumer(renderLayer);
		consumer.vertex(matrix, x1, y1, 0.0F).color(r, g, b, a).texture(-(float) patternOffset / patternLength, 1).next();
		float wrapTexPos = calcTexLength(x1, y1, x2, y2);
		consumer.vertex(matrix, x2, y2, 0.0F).color(r, g, b, a).texture(wrapTexPos - ((float) patternOffset / patternLength), 1).next();
		wrapTexPos += calcTexLength(x2, y2, x3, y3);
		consumer.vertex(matrix, x3, y3, 0.0F).color(r, g, b, a).texture(wrapTexPos - ((float) patternOffset / patternLength), 1).next();
		wrapTexPos += calcTexLength(x3, y3, x4, y4);
		consumer.vertex(matrix, x4, y4, 0.0F).color(r, g, b, a).texture(wrapTexPos - ((float) patternOffset / patternLength), 1).next();
		wrapTexPos += calcTexLength(x4, y4, x1, y1);
		consumer.vertex(matrix, x1, y1, 0.0F).color(r, g, b, a).texture(wrapTexPos - ((float) patternOffset / patternLength), 1).next();
		HTVertexConsumerProvider.draw();
	}

	public void cyclePattern() {
		if (patternOffset < patternLength - 1) {
			patternOffset++;
		} else {
			patternOffset = 0;
		}
	}

	private float calcTexLength(float lastX, float lastY, float thisX, float thisY) {
		return MathHelper.sqrt(MathHelper.square(thisX - lastX) + MathHelper.square(thisY - lastY)) / patternLength;
	}
	
	@Override
	public void close() {
		if (texture != null) texture.close();
	}
}
