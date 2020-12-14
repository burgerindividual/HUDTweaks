package com.github.burgerguy.hudtweaks.gui.widget;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.github.burgerguy.hudtweaks.gui.HudContainer;
import com.github.burgerguy.hudtweaks.util.gui.RelativeTreeRootScreen;
import com.github.burgerguy.hudtweaks.util.gui.YAxisNode;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class YAxisParentButtonWidget extends HTButtonWidget {
	private final Consumer<YAxisNode> onClick;
	
	private final Map<String, YAxisNode> innerMap;
	private final String[] keyHelper;
	private int currentIndex;
	
	public YAxisParentButtonWidget(int x, int y, int width, int height, YAxisNode currentParentNode, YAxisNode thisNode, Consumer<YAxisNode> onClick) {
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
	
	
	private void recurseAddNode(YAxisNode node, YAxisNode exclude) {
		for (YAxisNode child : node.getYChildren()) {
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
		YAxisNode newParentNode = innerMap.get(keyHelper[currentIndex]);
		setMessage(newParentNode);
		
		onClick.accept(newParentNode);
	}
	
	private static Text createMessage(YAxisNode node) {
		return new TranslatableText("hudtweaks.options.parent.display", node.getIdentifier());
	}
	
	public void setMessage(YAxisNode node) {
		setMessage(createMessage(node));
	}
}

