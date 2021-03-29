package com.github.burgerguy.hudtweaks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

@Mixin(InGameHud.class)
public interface InGameHudAccessor {
	@Accessor("currentStack")
	ItemStack getCurrentStack();
	
	@Accessor("overlayMessage")
	Text getActionBarText();
	
	@Accessor("overlayRemaining")
	int getActionBarRemaining();
	
	@Accessor("title")
	Text getTitleText();
	
	@Accessor("subtitle")
	Text getSubtitleText();
}
