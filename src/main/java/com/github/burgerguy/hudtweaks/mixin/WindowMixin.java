package com.github.burgerguy.hudtweaks.mixin;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.burgerguy.hudtweaks.util.Util;

import net.minecraft.client.util.Window;

@Mixin(Window.class)
public abstract class WindowMixin {
	@Inject(method = "<init>(Lnet/minecraft/client/WindowEventHandler;Lnet/minecraft/client/util/MonitorTracker;Lnet/minecraft/client/WindowSettings;Ljava/lang/String;Ljava/lang/String;)V",
			at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwDefaultWindowHints()V", shift = Shift.AFTER, remap = false))
	private void addDebugWindowHint(CallbackInfo ci) {
		Util.LOGGER.info("Enabling GLFW Debug Context...");
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, GLFW.GLFW_TRUE);
	}
}
