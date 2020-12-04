package com.github.burgerguy.hudtweaks.gui.widget;

import com.github.burgerguy.hudtweaks.mixin.TextFieldAccessor;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

public class HTTextFieldWidget extends TextFieldWidget {
	
	public HTTextFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text text) {
		super(textRenderer, x, y, width, height, text);
	}
	
	public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
		if (this.isVisible()) {
			TextFieldAccessor accessor = (TextFieldAccessor) this;
			
			int color;
			if (accessor.callHasBorder()) {
				color = this.isFocused() ? 0xFFFFFFFF : 0xFF000000;
				int x1 = this.x;
				int y1 = this.y;
				int x2 = this.x + this.width;
				int y2 = this.y + this.height;
				fill(matrixStack, x1,     y1,     x2,     y1 + 1, color);
				fill(matrixStack, x1,     y2,     x2,     y2 - 1, color);
				fill(matrixStack, x1,     y1 + 1, x1 + 1, y2 - 1, color);
				fill(matrixStack, x2,     y1 + 1, x2 - 1, y2 - 1, color);
			}
			
			color = accessor.getEditable() ? 0xCCFFFFFF : 0xCCA0A0A0;
			int k = accessor.getSelectionStart() - accessor.getFirstCharacterIndex();
			int l = accessor.getSelectionEnd() - accessor.getFirstCharacterIndex();
			String string = accessor.getTextRenderer().trimToWidth(this.getText().substring(accessor.getFirstCharacterIndex()), this.getInnerWidth());
			boolean bl = k >= 0 && k <= string.length();
			boolean bl2 = this.isFocused() && accessor.getFocusedTicks() / 6 % 2 == 0 && bl;
			int m = accessor.getFocused() ? this.x + 4 : this.x;
			int n = accessor.getFocused() ? this.y + (this.height - 8) / 2 : this.y;
			int o = m;
			if (l > string.length()) {
				l = string.length();
			}
			
			if (!string.isEmpty()) {
				String string2 = bl ? string.substring(0, k) : string;
				o = accessor.getTextRenderer().drawWithShadow(matrixStack, (OrderedText) accessor.getRenderTextProvider().apply(string2, accessor.getFirstCharacterIndex()), (float) m, (float) n, color);
			}
			
			boolean bl3 = accessor.getSelectionStart() < this.getText().length() || this.getText().length() >= accessor.callGetMaxLength();
			int p = o;
			if (!bl) {
				p = k > 0 ? m + this.width : m;
			} else if (bl3) {
				p = o - 1;
				--o;
			}
			
			if (!string.isEmpty() && bl && k < string.length()) {
				accessor.getTextRenderer().drawWithShadow(matrixStack, (OrderedText) accessor.getRenderTextProvider().apply(string.substring(k), accessor.getSelectionStart()), (float) o, (float) n, color);
			}
			
			if (!bl3 && accessor.getSuggestion() != null) {
				accessor.getTextRenderer().drawWithShadow(matrixStack, accessor.getSuggestion(), (float) (p - 1), (float) n, 0xFF808080);
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
					accessor.getTextRenderer().drawWithShadow(matrixStack, "_", (float) p, (float) n, color);
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
