package com.github.burgerguy.hudtweaks;

import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import com.github.burgerguy.hudtweaks.hud.HudContainer;
import com.github.burgerguy.hudtweaks.hud.element.DefaultBossBarEntry;
import com.github.burgerguy.hudtweaks.hud.element.HudElementEntry;
import com.github.burgerguy.hudtweaks.util.Util;

import net.fabricmc.loader.api.FabricLoader;

public class HudTweaksMixinPlugin implements IMixinConfigPlugin {
	
	@Override
	public void onLoad(String mixinPackage) {
	}
	
	@Override
	public String getRefMapperConfig() {
		return null;
	}
	
	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		return true;
	}
	
	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
	}
	
	@Override
	public List<String> getMixins() {
		return null;
	}

	private boolean bossBarClassModified;
	
	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
		// note to self:
		// if asm for other classes is eventually needed, consider moving everything to a mixin extension
		// so the class doesn't have to have a mixin assigned to it. store a hashset of the pre-mapped class
		// names to see if it's already been modified (if extensions can have classes loaded multiple times).
		// see this link for adding an extension:
		// https://github.com/Chocohead/Fabric-ASM/blob/239e122a532ba8cabf0714df2aa9fdd80f2082d4/src/com/chocohead/mm/Plugin.java#L248
		//
		// if more classes are eventually required for asm, and they all do have mixins associated with them,
		// stick with a mixin plugin because it won't have to check the name of every class loaded. just make
		// sure to keep a hashset like described before, because classes with multiple mixins WILL be run
		// through this function multiple times.
		if (!bossBarClassModified && targetClassName.equals(FabricLoader.getInstance().getMappingResolver().mapClassName("intermediary", "net.minecraft.class_337"))) { // BossBarHud class
			for (MethodNode methodNode : targetClass.methods) {
				if (methodNode.name.equals("render")) {
					ListIterator<AbstractInsnNode> itr = methodNode.instructions.iterator();
					while (itr.hasNext() && itr.next().getOpcode() != Opcodes.ICONST_3) {} // find ICONST_3 to start injection
					itr.remove();
					itr.next();
					itr.remove();
					LabelNode label = ((JumpInsnNode) itr.next()).label; // we know the next opcode is a comparison jump, so we extract the label from it
					itr.remove();
					itr.add(new InsnNode(Opcodes.I2F));
					itr.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(HudTweaksMixinPlugin.class), "getScreenPercent", Type.getMethodDescriptor(Type.FLOAT_TYPE)));
					itr.add(new InsnNode(Opcodes.FMUL));
					itr.add(new InsnNode(Opcodes.FCMPL));
					itr.add(new JumpInsnNode(Opcodes.IFLT, label));
					while (itr.hasPrevious() && itr.previous().getOpcode() != Opcodes.ALOAD) {} // find ALOAD to convert local var to float
					itr.add(new InsnNode(Opcodes.I2F));
					break;
				}
			}
			bossBarClassModified = true;
			Util.LOGGER.info("BossBarHud class (" + targetClassName + ") successfully modified.");
		}
	}

	public static float getScreenPercent() {
		HudElementEntry entry = HudContainer.getElementRegistry().getActiveEntry(DefaultBossBarEntry.IDENTIFIER.getElementType());
		if (entry instanceof DefaultBossBarEntry) {
			return ((DefaultBossBarEntry) entry).getScaledMaxHeight();
		}
		return 1.0f / 3.0f;
	}
	
	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}

}
