package com.github.burgerguy.hudtweaks.api;

import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.util.math.MatrixStack;

public class RenderStatusUpdater {
	private static final Consumer<MatrixStack> NOOP_CONSUMER = ms -> {};
	
	private Consumer<MatrixStack> onStartRender = NOOP_CONSUMER;
	private Consumer<MatrixStack> onEndRender = NOOP_CONSUMER;
	
	public void startRender(@Nullable MatrixStack matrixStack) {
		onStartRender.accept(matrixStack);
	}
	
	public void endRender(@Nullable MatrixStack matrixStack) {
		onEndRender.accept(matrixStack);
	}
	
	void fillRunnables(Consumer<MatrixStack> onStartRender, Consumer<MatrixStack> onEndRender) {
		this.onStartRender = onStartRender;
		this.onEndRender = onEndRender;
	}
}
