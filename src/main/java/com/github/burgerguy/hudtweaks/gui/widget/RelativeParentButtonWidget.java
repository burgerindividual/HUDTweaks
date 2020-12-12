package com.github.burgerguy.hudtweaks.gui.widget;

import java.util.Map;
import java.util.function.Consumer;

import com.github.burgerguy.hudtweaks.gui.HudContainer;
import com.github.burgerguy.hudtweaks.gui.RelativeParent;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class RelativeParentButtonWidget extends HTButtonWidget {
	private final Consumer<RelativeParent> onClick;
	
	private final Map<String, RelativeParent> innerMap;
	private final String[] keyHelper;
	private int currentIndex;
	
	public RelativeParentButtonWidget(int x, int y, int width, int height, boolean isX, RelativeParent relativeParent,
			String thisElementIdentifier, Consumer<RelativeParent> onClick) {
		super(x, y, width, height, createMessage(relativeParent));
		innerMap = HudContainer.getRelativeParentCache().getColumn(isX);
		innerMap.remove(thisElementIdentifier);
		keyHelper = innerMap.keySet().toArray(new String[innerMap.size()]);
		for (; currentIndex < keyHelper.length; currentIndex++) {
			if (keyHelper[currentIndex].equals(relativeParent.getIdentifier()))
				break;
		}
		
		this.onClick = onClick;
	}
	
	@Override
	public void onPress() {
		if (++currentIndex >= keyHelper.length)
			currentIndex = 0;
		RelativeParent relativeParent = innerMap.get(keyHelper[currentIndex]);
		setMessage(relativeParent);
		
		onClick.accept(relativeParent);
	}
	
	private static Text createMessage(RelativeParent relativeParent) {
		return new TranslatableText("hudtweaks.options.relative_to.display", relativeParent.getIdentifier());
	}
	
	public void setMessage(RelativeParent relativeParent) {
		setMessage(createMessage(relativeParent));
	}
}
