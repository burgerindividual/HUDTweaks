package com.github.burgerguy.hudtweaks.gui.widget;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.github.burgerguy.hudtweaks.hud.HudContainer;
import com.github.burgerguy.hudtweaks.hud.XAxisNode;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class XAxisParentButtonWidget extends HTButtonWidget {
	private final Consumer<XAxisNode> onClick;
	
	private final Map<String, XAxisNode> innerMap = new LinkedHashMap<>();
	private final String[] keyHelper;
	private int currentIndex;
	
	public XAxisParentButtonWidget(int x, int y, int width, int height, XAxisNode currentParentNode, XAxisNode thisNode, Consumer<XAxisNode> onClick) {
		super(x, y, width, height, createMessage(currentParentNode));
		recurseAddNode(HudContainer.getScreenRoot(), thisNode);
		keyHelper = innerMap.keySet().toArray(new String[innerMap.size()]);
		for (; currentIndex < keyHelper.length; currentIndex++) {
			if (keyHelper[currentIndex].equals(currentParentNode.getIdentifier()))
				break;
		}
		
		this.onClick = onClick;
	}
	
	
	private void recurseAddNode(XAxisNode node, XAxisNode exclude) {
		if (!node.equals(exclude)) {
			innerMap.put(node.getIdentifier(), node);
			for (XAxisNode child : node.getXChildren()) {
				recurseAddNode(child, exclude);
			}
		}
	}
	
	@Override
	public void onPress() {
		if (++currentIndex >= keyHelper.length) currentIndex = 0;
		XAxisNode newParentNode = innerMap.get(keyHelper[currentIndex]);
		setMessage(newParentNode);
		
		onClick.accept(newParentNode);
	}
	
	private static Text createMessage(XAxisNode node) {
		return new TranslatableText("hudtweaks.options.parent.display", node.getIdentifier());
	}
	
	public void setMessage(XAxisNode node) {
		setMessage(createMessage(node));
	}
}
