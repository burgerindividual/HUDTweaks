package com.github.burgerguy.hudtweaks.util.gl;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;
import net.minecraft.client.render.VertexFormats;

public final class HTVertexFormats {
    private HTVertexFormats() {
        // no instantiation, all contents static
    }

    public static final VertexFormatElement DISTANCE_ELEMENT = new VertexFormatElement(0, VertexFormatElement.ComponentType.FLOAT, VertexFormatElement.Type.GENERIC, 1);
    public static final VertexFormat LINES_MODIFIED = new VertexFormat(ImmutableMap.<String, VertexFormatElement>builder().put("Position", VertexFormats.POSITION_ELEMENT).put("Color", VertexFormats.COLOR_ELEMENT).put("Normal", VertexFormats.NORMAL_ELEMENT).put("Padding", VertexFormats.PADDING_ELEMENT).put("Distance", DISTANCE_ELEMENT).build());
}
