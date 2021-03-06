package com.github.burgerguy.hudtweaks.api;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.util.math.MatrixStack;

public class MatrixUpdater {
	private static final Consumer<MatrixStack> NOOP_CONSUMER = ms -> {};
	
	private Consumer<MatrixStack> onStartRender = NOOP_CONSUMER;
	private Consumer<MatrixStack> onEndRender = NOOP_CONSUMER;
	private Supplier<Boolean> isEnabled = () -> true;
	
	/**
	 * This will return true if HUDTweaks isn't present.
	 * If this is false, the element entry should not render.
	 */
	public boolean isEntryEnabled() {
		return isEnabled.get();
	}
	
	/**
	 * @param matrices If null, uses the RenderSystem matrix stack.
	 */
	public void pushMatrices(@Nullable MatrixStack matrices) {
		onStartRender.accept(matrices);
	}
	
	/**
	 * @param matrices If null, uses the RenderSystem matrix stack.
	 *                    <b>The MatrixStack (or lack of) must be the same
	 *                    you called onStartRender with.</b>
	 */
	public void popMatrices(@Nullable MatrixStack matrices) {
		onEndRender.accept(matrices);
	}
	
	void fill(Consumer<MatrixStack> onStartRender, Consumer<MatrixStack> onEndRender, Supplier<Boolean> isEnabled) {
		this.onStartRender = onStartRender;
		this.onEndRender = onEndRender;
		this.isEnabled = isEnabled;
	}
}
