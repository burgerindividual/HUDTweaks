package com.github.burgerguy.hudtweaks.api;

import net.minecraft.client.util.math.MatrixStack;

import java.util.function.Consumer;

public class MatrixUpdater {
	private static final Consumer<MatrixStack> NOOP_CONSUMER = ms -> {};
	
	private Consumer<MatrixStack> onStartRender = NOOP_CONSUMER;
	private Consumer<MatrixStack> onEndRender = NOOP_CONSUMER;

	/**
	 * @param matrices The MatrixStack used in rendering
	 */
	public void pushMatrices(MatrixStack matrices) {
		onStartRender.accept(matrices);
	}

	/**
	 * @param matrices The MatrixStack that was called with pushMatrices
	 */
	public void popMatrices(MatrixStack matrices) {
		onEndRender.accept(matrices);
	}

	void fill(Consumer<MatrixStack> onStartRender, Consumer<MatrixStack> onEndRender) {
		this.onStartRender = onStartRender;
		this.onEndRender = onEndRender;
	}
}
