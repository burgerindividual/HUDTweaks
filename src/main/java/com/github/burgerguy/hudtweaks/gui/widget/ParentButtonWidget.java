package com.github.burgerguy.hudtweaks.gui.widget;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import com.github.burgerguy.hudtweaks.hud.HudContainer;
import com.github.burgerguy.hudtweaks.hud.tree.AbstractTypeNode;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ParentButtonWidget extends HTButtonWidget {
	private final Consumer<AbstractTypeNode> onClick;
	
	private final Map<HTIdentifier.ElementType, AbstractTypeNode> innerMap = new LinkedHashMap<>();
	private final HTIdentifier.ElementType[] keyHelper;
	private int currentIndex;
	
	public ParentButtonWidget(int x, int y, int width, int height, AbstractTypeNode currentParentNode, AbstractTypeNode thisNode, Consumer<AbstractTypeNode> onClick, boolean useX) {
		super(x, y, width, height, createMessage(currentParentNode));
		recurseAddNode(HudContainer.getScreenRoot(), thisNode);
		keyHelper = innerMap.keySet().toArray(new HTIdentifier.ElementType[innerMap.size()]);
		for (; currentIndex < keyHelper.length; currentIndex++) {
			if (keyHelper[currentIndex].equals(currentParentNode.getElementIdentifier()))
				break;
		}
		
		this.onClick = onClick;
	}
	
	
	private void recurseAddNode(AbstractTypeNode node, AbstractTypeNode exclude) {
		if (!node.equals(exclude)) {
			innerMap.put(node.getElementIdentifier(), node);
			for (AbstractTypeNode child : node.getXChildren()) {
				recurseAddNode(child, exclude);
			}
		}
	}
	
	@Override
	public void onPress() {
		if (++currentIndex >= keyHelper.length) currentIndex = 0;
		AbstractTypeNode newParentNode = innerMap.get(keyHelper[currentIndex]);
		setMessage(newParentNode);
		
		onClick.accept(newParentNode);
	}
	
	private static Text createMessage(AbstractTypeNode node) {
		return new TranslatableText("hudtweaks.options.parent.display", node.getElementIdentifier().toTranslatedString());
	}
	
	public void setMessage(AbstractTypeNode node) {
		setMessage(createMessage(node));
	}
}
