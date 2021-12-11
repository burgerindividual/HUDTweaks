package com.github.burgerguy.hudtweaks.mixin;

import com.github.burgerguy.hudtweaks.util.gl.HTRenderLayers;
import com.github.burgerguy.hudtweaks.util.gl.HTVertexFormats;
import net.minecraft.client.render.*;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Shadow protected abstract Shader loadShader(ResourceFactory factory, String name, VertexFormat vertexFormat);

    @Inject(method = "loadShaders", at = @At("TAIL"))
    private void loadHudtweaksShaders(ResourceManager manager, CallbackInfo ci) {
        Shader dashedLinesShader = loadShader(manager, "dashed_lines", HTVertexFormats.LINES_MODIFIED);
        Shader solidLinesShader = loadShader(manager, "solid_lines", VertexFormats.LINES);

        HTRenderLayers.dashedLinesShader = new RenderPhase.Shader(() -> dashedLinesShader);
        HTRenderLayers.dashOffset = dashedLinesShader.getUniform("DashOffset");
        HTRenderLayers.dashLength = dashedLinesShader.getUniform("DashLength");
        HTRenderLayers.solidLinesShader = new RenderPhase.Shader(() -> solidLinesShader);
    }
}
