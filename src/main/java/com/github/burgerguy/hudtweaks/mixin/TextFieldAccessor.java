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
	public TextRenderer getTextRenderer();
	@Accessor("focusedTicks")
	public int getFocusedTicks();
	@Accessor("editable")
	public boolean getEditable();
	@Accessor("firstCharacterIndex")
	public int getFirstCharacterIndex();
	@Accessor("selectionStart")
	public int getSelectionStart();
	@Accessor("selectionEnd")
	public int getSelectionEnd();
	@Accessor("suggestion")
	public String getSuggestion();
	@Accessor("renderTextProvider")
	public BiFunction<String, Integer, OrderedText> getRenderTextProvider();
	
	@Invoker
	public boolean callDrawsBackground();
	@Invoker
	public void callDrawSelectionHighlight(int x1, int y1, int x2, int y2);
	@Invoker
	public int callGetMaxLength();
}
