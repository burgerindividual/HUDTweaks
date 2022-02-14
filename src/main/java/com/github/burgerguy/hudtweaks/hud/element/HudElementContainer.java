package com.github.burgerguy.hudtweaks.hud.element;

import com.github.burgerguy.hudtweaks.api.HudElementOverride;
import com.github.burgerguy.hudtweaks.hud.tree.AbstractContainerNode;
import com.github.burgerguy.hudtweaks.util.Util;
import com.github.burgerguy.hudtweaks.util.gl.DrawTest;
import com.google.gson.JsonElement;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

public class HudElementContainer extends AbstractContainerNode {
	private final HudElement initialElement;
	private final List<HudElementOverride> overrides = new ArrayList<>(2);

	protected HudElementWidget widget;
	private boolean matrixPushed;
	private DrawTest drawTest;
	private boolean drawTestFailed;

	public HudElementContainer(HudElement initialElement) {
		this.initialElement = initialElement;
		// we have to create the draw test here because
		// it has to be on the render thread and it has
		// to be after lwjgl has initialized
		RenderSystem.recordRenderCall(() -> drawTest = new DrawTest());
		initialElement.setContainerNode(this);
		initialElement.init();
	}

	public void addOverride(HudElementOverride override) {
		overrides.add(override);
		HudElement element = override.getElement();
		element.setContainerNode(this);
		element.init();
	}

	@Override
	public HudElement getInitialElement() {
		return initialElement;
	}

	/**
	 * Gets the first enabled override in the list, otherwise the initial element.
	 */
	@Override
	public HudElement getActiveElement() {
		if (!overrides.isEmpty()) {
			for (HudElementOverride override : overrides) {
				if (override.isEnabled()) {
					return override.getElement();
				}
			}
		}
		return initialElement;
	}

	/**
	 * Should only be used for profile saving and loading.
	 */
	public List<HudElementOverride> getOverrides() {
		return Collections.unmodifiableList(overrides);
	}

	public void updateFromJson(JsonElement json) {
		initialElement.updateFromJson(json);
		JsonElement overridesElement = json.getAsJsonObject().get("overrides");
		if (overridesElement != null) {
			for (Entry<String, JsonElement> jsonOverride : overridesElement.getAsJsonObject().entrySet()) {
				boolean foundOverride = false;
				for (HudElementOverride override : overrides) {
					HudElement element = override.getElement();
					if (element.getIdentifier().toString().equals(jsonOverride.getKey())) {
						element.updateFromJson(jsonOverride.getValue());
						foundOverride = true;
						break;
					}
				}

				if (!foundOverride)
					Util.LOGGER.error("Override specified in config doesn't exist in override map, skipping...");
			}
		}
	}

	public void setupActiveMatrix() {
		getActiveElement().createMatrix();
	}

	public boolean tryPushMatrix(MatrixStack... matrixStacks) {
		// we can't push more than once at a time
		if (!matrixPushed) {
			Matrix4f activeElementMatrix = getActiveElement().getMatrix();
			for (MatrixStack matrixStack : matrixStacks) {
				matrixStack.push();
				matrixStack.peek().getPositionMatrix().multiply(activeElementMatrix);
			}
			matrixPushed = true;
			return true;
		}
		return false;
	}

	public boolean tryPopMatrix(MatrixStack... matrixStacks) {
		// we can't pop if it hasn't been pushed
		if (matrixPushed) {
			for (MatrixStack matrixStack : matrixStacks) {
				matrixStack.pop();
			}
			matrixPushed = false;
			return true;
		}
		return false;
	}

	public void markDrawTestStart() {
		if (!drawTestFailed) {
			try {
				drawTest.markStart();
			} catch (IllegalStateException e) {
				drawTestFail(e);
			}
		}
	}

	public void markDrawTestEnd() {
		if (!drawTestFailed) {
			try {
				drawTest.markEnd();
			} catch (IllegalStateException e) {
				drawTestFail(e);
			}
		}
	}

	public boolean isRendered() {
		if (!drawTestFailed) {
			try {
				return drawTest.getResult();
			} catch (IllegalStateException e) {
				drawTestFail(e);
			}
		}
		return false;
	}

	private void drawTestFail(Throwable e) {
		drawTestFailed = true;
		// this is done so that we don't accidentally mess up the next draw test and cause a gl error
		if (drawTest.isActive()) drawTest.markEnd();
		Util.LOGGER.error("Draw test failed for element " + getActiveElement().getIdentifier().toString() + " (container: " + getInitialElement().getIdentifier() + "), likely due to other mod.\nTo mod devs: please implement the HUDTweaks API and avoid unnecessary cancellations.", e);
	}

	@Nullable
	public HudElementWidget getWidget() {
		return widget;
	}

	public HudElementWidget createWidget(@Nullable Runnable valueUpdater) {
		return widget = new HudElementWidget(this, valueUpdater);
	}
}
