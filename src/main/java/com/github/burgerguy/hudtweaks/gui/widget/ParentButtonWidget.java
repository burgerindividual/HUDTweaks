package com.github.burgerguy.hudtweaks.gui.widget;

import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import com.github.burgerguy.hudtweaks.hud.HudContainer;
import com.github.burgerguy.hudtweaks.hud.tree.AbstractContainerNode;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ParentButtonWidget extends HTOverflowButtonWidget {
	private final Consumer<AbstractContainerNode> onClick;

	/**
	 * Actual storage of the parents
	 */
	private final Map<HTIdentifier, AbstractContainerNode> innerMap = new HashMap<>();
	/**
	 * Used for iteration and indices
	 */
	private final HTIdentifier[] keyHelper;
	private int currentIndex;

	public ParentButtonWidget(int x, int y, int width, int height, AbstractContainerNode currentParentNode, AbstractContainerNode thisNode, Consumer<AbstractContainerNode> onClick) {
		super(x, y, width, height, createMessage(currentParentNode));
		recurseAddNode(HudContainer.getScreenRoot(), thisNode);
		keyHelper = innerMap.keySet().toArray(new HTIdentifier[0]);
		Arrays.sort(keyHelper, (i1, i2) -> String.CASE_INSENSITIVE_ORDER.compare(i1.toDisplayableString(), i2.toDisplayableString()));
		for (; currentIndex < keyHelper.length; currentIndex++) { // iterates through the array to find the index of the last saved element
			if (keyHelper[currentIndex].equals(currentParentNode.getInitialElement().getIdentifier()))
				break;
		}

		this.onClick = onClick;
	}


	private void recurseAddNode(AbstractContainerNode node, AbstractContainerNode exclude) {
		if (!node.equals(exclude)) {
			innerMap.put(node.getInitialElement().getIdentifier(), node);
			for (AbstractContainerNode child : node.getXChildren()) {
				recurseAddNode(child, exclude);
			}
		}
	}

	@Override
	public void onPress() {
		currentIndex += Screen.hasShiftDown() ? -1 : 1;

		if (currentIndex >= keyHelper.length) {
			currentIndex = 0;
		} else if (currentIndex <= -1) {
			currentIndex = keyHelper.length - 1;
		}
		AbstractContainerNode newParentNode = innerMap.get(keyHelper[currentIndex]);
		setMessage(newParentNode);

		onClick.accept(newParentNode);
	}

	private static Text createMessage(AbstractContainerNode node) {
		// display active name, but store with initial name
		return new TranslatableText("hudtweaks.options.parent.display", node.getActiveElement().getIdentifier().toDisplayableString());
	}

	public void setMessage(AbstractContainerNode node) {
		setMessage(createMessage(node));
		overflowTextRenderer.restart();
	}
}
