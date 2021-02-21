package com.github.burgerguy.hudtweaks.api;

import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public abstract class PlaceholderName {
    private static final Consumer<MatrixStack> NOOP_CONSUMER = ms -> {};

    private Consumer<MatrixStack> onStartRender = NOOP_CONSUMER;
    private Consumer<MatrixStack> onEndRender = NOOP_CONSUMER;

    public final HTIdentifier identifier;
    public final String[] updateEvents;

    protected PlaceholderName(HTIdentifier identifier, String... updateEvents) {
        this.identifier = identifier;
        this.updateEvents = updateEvents;
    }

    /**
     * @param matrices If null, uses the RenderSystem matrix stack.
     */
    public void onStartRender(@Nullable MatrixStack matrices) {
        onStartRender.accept(matrices);
    }

    /**
     * @param matrices If null, uses the RenderSystem matrix stack.
     *                    <b>The MatrixStack (or lack of) must be the same
     *                    you called onStartRender with.</b>
     */
    public void onEndRender(@Nullable MatrixStack matrices) {
        onEndRender.accept(matrices);
    }

    /*
     * only for internal use
     */
    void fillRunnables(Consumer<MatrixStack> onStartRender, Consumer<MatrixStack> onEndRender) {
        this.onStartRender = onStartRender;
        this.onEndRender = onEndRender;
    }

    protected abstract double calculateWidth(MinecraftClient client);

    protected abstract double calculateHeight(MinecraftClient client);

    protected abstract double calculateDefaultX(MinecraftClient client);

    protected abstract double calculateDefaultY(MinecraftClient client);
}
