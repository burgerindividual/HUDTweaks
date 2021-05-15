package com.github.burgerguy.hudtweaks.mixin;

import java.util.function.BiFunction;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.OrderedText;

@Mixin(TextFieldWidget.class)
public interface TextFieldAccessor {
	@Accessor("textRenderer")
	TextRenderer getTextRenderer();
	@Accessor("focusedTicks")
	int getFocusedTicks();
	@Accessor("editable")
	boolean getEditable();
	@Accessor("firstCharacterIndex")
	int getFirstCharacterIndex();
	@Accessor("selectionStart")
	int getSelectionStart();
	@Accessor("selectionEnd")
	int getSelectionEnd();
	@Accessor("suggestion")
	String getSuggestion();
	@Accessor("renderTextProvider")
	BiFunction<String, Integer, OrderedText> getRenderTextProvider();

	@Invoker
	boolean callDrawsBackground();
	@Invoker
	void callDrawSelectionHighlight(int x1, int y1, int x2, int y2);
	@Invoker
	int callGetMaxLength();
}
