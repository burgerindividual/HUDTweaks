package com.github.burgerguy.hudtweaks.gui.widget;

import java.util.function.Consumer;

import com.github.burgerguy.hudtweaks.hud.element.HudElement.PosType;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class PosTypeButtonWidget extends HTButtonWidget {
	private final Consumer<PosType> onClick;
	private PosType posType;
	
	public PosTypeButtonWidget(int x, int y, int width, int height, PosType posType, Consumer<PosType> onClick) {
		super(x, y, width, height, createMessage(posType));
		this.onClick = onClick;
		this.posType = posType;
	}
	
	@Override
	public void onPress() {
		PosType[] values = PosType.values();
		int newOrdinal = posType.ordinal() + 1;
		if (newOrdinal < values.length) {
			setType(values[newOrdinal]);
		} else {
			setType(values[0]);
		}

		onClick.accept(posType);
	}

	private static Text createMessage(PosType posType) {
		return new TranslatableText("hudtweaks.options.pos_type.display", I18n.translate("hudtweaks.options.pos_type." + posType.toString().toLowerCase()));
	}

	public void setType(PosType posType) {
		this.posType = posType;
		setMessage(createMessage(posType));
	}
}
