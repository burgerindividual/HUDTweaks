package com.github.burgerguy.hudtweaks.gui.widget;

import java.util.function.Supplier;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public abstract class HudTweaksNumberBox extends TextFieldWidget implements ValueUpdatable {

	public HudTweaksNumberBox(TextRenderer textRenderer, int x, int y, int width, int height, Text text) {
		super(textRenderer, x, y, width, height, text);
	}
	
	@Override
	public void write(String string) {
		super.write(stripInvalidChars(string));
	}
	
	/**
	 * This filters out everything except for the characters 0123456789.-+
	 */
	private static boolean isValidChar(char chr) {
		return (chr >= 48 && chr <= 57) || chr == 46 || chr == 45 || chr == 43;
	}
	
	private static String stripInvalidChars(String input) {
		StringBuilder sb = new StringBuilder();
		char[] charArray = input.toCharArray();
		
		for (int i = 0; i < charArray.length; ++i) {
			char chr = charArray[i];
			if (isValidChar(chr)) {
				sb.append(chr);
			}
		}
		
		return sb.toString();
	}
	
}
