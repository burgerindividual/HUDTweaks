package com.github.burgerguy.hudtweaks.gui.widget;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import com.github.burgerguy.hudtweaks.hud.HudContainer;
import com.github.burgerguy.hudtweaks.hud.tree.AbstractContainerNode;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ParentButtonWidget extends HTButtonWidget {
	private final Consumer<AbstractContainerNode> onClick;

	/**
	 * Actual storage of the parents
	 */
	private final Map<HTIdentifier, AbstractContainerNode> innerMap = new LinkedHashMap<>();
	/**
	 * Used for iteration and indices
	 */
	private final HTIdentifier[] keyHelper;
	private int currentIndex;

	public ParentButtonWidget(int x, int y, int width, int height, AbstractContainerNode currentParentNode, AbstractContainerNode thisNode, Consumer<AbstractContainerNode> onClick) {
		super(x, y, width, height, createMessage(currentParentNode));
		recurseAddNode(HudContainer.getScreenRoot(), thisNode);
		keyHelper = innerMap.keySet().toArray(new HTIdentifier[0]);
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
		if (++currentIndex >= keyHelper.length) currentIndex = 0;
		AbstractContainerNode newParentNode = innerMap.get(keyHelper[currentIndex]);
		setMessage(newParentNode);

		onClick.accept(newParentNode);
	}

	private static Text createMessage(AbstractContainerNode node) {
		// display active name, but store with initial name
		return new TranslatableText("hudtweaks.options.parent.display", node.getActiveElement().getIdentifier().toTranslatedString());
	}

	public void setMessage(AbstractContainerNode node) {
		setMessage(createMessage(node));
	}
}
