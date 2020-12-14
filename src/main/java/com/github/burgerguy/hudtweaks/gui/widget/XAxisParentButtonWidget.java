package com.github.burgerguy.hudtweaks.gui.widget;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.github.burgerguy.hudtweaks.gui.HudContainer;
import com.github.burgerguy.hudtweaks.util.gui.RelativeTreeRootScreen;
import com.github.burgerguy.hudtweaks.util.gui.XAxisNode;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class XAxisParentButtonWidget extends HTButtonWidget {
	private final Consumer<XAxisNode> onClick;
	
	private final Map<String, XAxisNode> innerMap;
	private final String[] keyHelper;
	private int currentIndex;
	
	public XAxisParentButtonWidget(int x, int y, int width, int height, XAxisNode currentParentNode, XAxisNode thisNode, Consumer<XAxisNode> onClick) {
		super(x, y, width, height, createMessage(currentParentNode));
		innerMap = new LinkedHashMap<>();
		innerMap.put(RelativeTreeRootScreen.IDENTIFIER, HudContainer.getScreenRoot());
		recurseAddNode(HudContainer.getScreenRoot(), thisNode);
		keyHelper = innerMap.keySet().toArray(new String[innerMap.size()]);
		for (; currentIndex < keyHelper.length; currentIndex++) {
			if (keyHelper[currentIndex].equals(currentParentNode.getIdentifier()))
				break;
		}
		
		this.onClick = onClick;
	}
	
	
	private void recurseAddNode(XAxisNode node, XAxisNode exclude) {
		for (XAxisNode child : node.getXChildren()) {
			if (!child.equals(exclude)) {
				innerMap.put(child.getIdentifier(), child);
				recurseAddNode(child, exclude);
			}
		}
	}
	
	@Override
	public void onPress() {
		if (++currentIndex >= keyHelper.length)
			currentIndex = 0;
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
