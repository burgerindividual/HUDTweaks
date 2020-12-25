package com.github.burgerguy.hudtweaks.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.burgerguy.hudtweaks.gui.HudElement;
import com.github.burgerguy.hudtweaks.util.json.HudElementSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

/**
 * For small utilities that don't need their own class.
 */
public enum Util {
	;
	
	public static final Gson GSON;
	public static final JsonParser JSON_PARSER = new JsonParser();
	public static final Logger LOGGER = LogManager.getLogger("HUDTweaks");
	
	public static final NumberFormat RELATIVE_POS_FORMATTER = new DecimalFormat("%##0.0");
	public static final NumberFormat ANCHOR_POS_FORMATTER = new DecimalFormat("%##0.0");
	public static final NumberFormat OFFSET_FORMATTER = new DecimalFormat("####0.0");
	
	static {
		GSON = new GsonBuilder().setPrettyPrinting().registerTypeHierarchyAdapter(HudElement.class, new HudElementSerializer()).create();
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
		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
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
