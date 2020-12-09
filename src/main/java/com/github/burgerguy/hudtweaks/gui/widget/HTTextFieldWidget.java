package com.github.burgerguy.hudtweaks.gui.widget;

import com.github.burgerguy.hudtweaks.mixin.TextFieldAccessor;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class HTTextFieldWidget extends TextFieldWidget {
	
	public HTTextFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text text) {
		super(textRenderer, x, y, width, height, text);
	}
	
	@Override
	public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
		if (isVisible()) {
			TextFieldAccessor accessor = (TextFieldAccessor) this;
			
			int color;
			if (accessor.callHasBorder()) {
				color = isFocused() ? 0xFFFFFFFF : 0xFF000000;
				int x1 = x;
				int y1 = y;
				int x2 = x + width;
				int y2 = y + height;
				fill(matrixStack, x1,     y1,     x2,     y1 + 1, color);
				fill(matrixStack, x1,     y2,     x2,     y2 - 1, color);
				fill(matrixStack, x1,     y1 + 1, x1 + 1, y2 - 1, color);
				fill(matrixStack, x2,     y1 + 1, x2 - 1, y2 - 1, color);
			}
			
			color = accessor.getEditable() ? 0xCCFFFFFF : 0xCCA0A0A0;
			int k = accessor.getSelectionStart() - accessor.getFirstCharacterIndex();
			int l = accessor.getSelectionEnd() - accessor.getFirstCharacterIndex();
			String string = accessor.getTextRenderer().trimToWidth(getText().substring(accessor.getFirstCharacterIndex()), getInnerWidth());
			boolean bl = k >= 0 && k <= string.length();
			boolean bl2 = isFocused() && accessor.getFocusedTicks() / 6 % 2 == 0 && bl;
			int m = accessor.getFocused() ? x + 4 : x;
			int n = accessor.getFocused() ? y + (height - 8) / 2 : y;
			int o = m;
			if (l > string.length()) {
				l = string.length();
			}
			
			if (!string.isEmpty()) {
				String string2 = bl ? string.substring(0, k) : string;
				o = accessor.getTextRenderer().drawWithShadow(matrixStack, accessor.getRenderTextProvider().apply(string2, accessor.getFirstCharacterIndex()), m, n, color);
			}
			
			boolean bl3 = accessor.getSelectionStart() < getText().length() || getText().length() >= accessor.callGetMaxLength();
			int p = o;
			if (!bl) {
				p = k > 0 ? m + width : m;
			} else if (bl3) {
				p = o - 1;
				--o;
			}
			
			if (!string.isEmpty() && bl && k < string.length()) {
				accessor.getTextRenderer().drawWithShadow(matrixStack, accessor.getRenderTextProvider().apply(string.substring(k), accessor.getSelectionStart()), o, n, color);
			}
			
			if (!bl3 && accessor.getSuggestion() != null) {
				accessor.getTextRenderer().drawWithShadow(matrixStack, accessor.getSuggestion(), p - 1, n, 0xFF808080);
			}
			
			int var10002;
			int var10003;
			int var10004;
			if (bl2) {
				if (bl3) {
					var10002 = n - 1;
					var10003 = p + 1;
					var10004 = n + 1;
					fill(matrixStack, p, var10002, var10003, var10004 + 9, 0xFFD0D0D0);
				} else {
					accessor.getTextRenderer().drawWithShadow(matrixStack, "_", p, n, color);
				}
			}
			
			if (l != k) {
				int q = m + accessor.getTextRenderer().getWidth(string.substring(0, l));
				var10002 = n - 1;
				var10003 = q - 1;
				var10004 = n + 1;
				accessor.callDrawSelectionHighlight(p, var10002, var10003, var10004 + 9);
			}
			
		}
	}
	
}
