package com.github.burgerguy.hudtweaks.api;

import net.minecraft.client.util.math.MatrixStack;

import java.util.function.Consumer;

public class RenderStateUpdater {
	private static final Consumer<MatrixStack[]> NOOP_CONSUMER = ms -> {};
	
	private Consumer<MatrixStack[]> onStartRender = NOOP_CONSUMER;
	private Consumer<MatrixStack[]> onFinishRender = NOOP_CONSUMER;

	/**
	 * @param matrixStacks The MatrixStack (or stacks) used in rendering
	 */
	public void doStartRender(MatrixStack... matrixStacks) {
		onStartRender.accept(matrixStacks);
	}

	/**
	 * @param matrixStacks The MatrixStacks that were called with doStartRender
	 */
	public void doFinishRender(MatrixStack... matrixStacks) {
		onFinishRender.accept(matrixStacks);
	}

	void fill(Consumer<MatrixStack[]> onStartRender, Consumer<MatrixStack[]> onFinishRender) {
		this.onStartRender = onStartRender;
		this.onFinishRender = onFinishRender;
	}
}
