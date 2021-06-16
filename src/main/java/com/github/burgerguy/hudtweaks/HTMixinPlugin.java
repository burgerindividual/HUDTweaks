package com.github.burgerguy.hudtweaks;

import com.github.burgerguy.hudtweaks.hud.HudContainer;
import com.github.burgerguy.hudtweaks.hud.element.DefaultBossBarElement;
import com.github.burgerguy.hudtweaks.hud.element.DefaultHungerElement;
import com.github.burgerguy.hudtweaks.hud.element.HudElement;
import com.github.burgerguy.hudtweaks.util.Util;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

public class HTMixinPlugin implements IMixinConfigPlugin {

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

	private static final Set<String> appliedClasses = new HashSet<>();

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
		if (!appliedClasses.contains("net.minecraft.class_337") && classNameEqualsMapped(targetClassName, "net.minecraft.class_337")) { // BossBarHud class
			for (MethodNode methodNode : targetClass.methods) {
				if (methodNode.name.equals("render")) {
					ListIterator<AbstractInsnNode> itr = methodNode.instructions.iterator();
					boolean errorInjecting = false;
					while (true) {
						if (itr.hasNext()) {
							if (itr.next().getOpcode() == Opcodes.ICONST_3) break; // find ICONST_3 to start injection
						} else {
							Util.LOGGER.error("Unable to inject into BossBarHud class: unable to find injection point");
							errorInjecting = true;
							break;
						}
					}
					if (errorInjecting) continue; // maybe there's another method with the same name and different params?
					itr.remove();
					itr.next();
					itr.remove();
					LabelNode label = ((JumpInsnNode) itr.next()).label; // we know the next opcode is a comparison jump, so we extract the label from it
					itr.remove();
					itr.add(new InsnNode(Opcodes.I2F));
					itr.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(HTMixinPlugin.class), "getBossBarScreenPercent", Type.getMethodDescriptor(Type.FLOAT_TYPE)));
					itr.add(new InsnNode(Opcodes.FMUL));
					itr.add(new InsnNode(Opcodes.FCMPL));
					itr.add(new JumpInsnNode(Opcodes.IFLT, label));
					while (true) if (!itr.hasPrevious() || itr.previous().getOpcode() == Opcodes.ALOAD) break; // find ALOAD to convert local var to float
					itr.add(new InsnNode(Opcodes.I2F));
					break;
				}
			}
			appliedClasses.add("net.minecraft.class_337");
			Util.LOGGER.debug("BossBarHud class (" + targetClassName + ") successfully modified.");
		}

		if (!appliedClasses.contains("net.minecraft.class_329") && classNameEqualsMapped(targetClassName, "net.minecraft.class_329")) { // InGameHud class
			for (MethodNode methodNode : targetClass.methods) {
				if (methodNode.name.equals("renderStatusBars")) {
					ListIterator<AbstractInsnNode> itr = methodNode.instructions.iterator();
					boolean errorInjecting = false;
					while (true) {
						if (itr.hasNext()) {
							AbstractInsnNode insn = itr.next();
							if (insn instanceof LdcInsnNode ldcInsn) {
								if (ldcInsn.cst.equals("food")) break; // find LDC "food" to get bearings
							}
						} else {
							Util.LOGGER.error("Unable to inject into InGameHud class: unable to find injection point");
							errorInjecting = true;
							break;
						}
					}
					if (errorInjecting) continue; // maybe there's another method with the same name and different params?
					while (true) if (!itr.hasPrevious() || itr.previous().getType() == AbstractInsnNode.JUMP_INSN) break; // move back to before jump (likely IFNE) to start injection
					itr.next();
					// even though in the bytecode there's a label here, it is only there for debugging and isn't reliable.
					// instead, we just make our own right next to it.
					LabelNode postJump = new LabelNode();
					itr.add(postJump);
					itr.previous();
					itr.previous();
					itr.previous();
					itr.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(HTMixinPlugin.class), "getForceDisplayHunger", Type.getMethodDescriptor(Type.BOOLEAN_TYPE)));
					itr.add(new JumpInsnNode(Opcodes.IFNE, postJump)); // we do IFNE here because the default constant on the stack is 0. We want out boolean to be true to jump, and true is equal to 1.
					break;
				}
			}
			appliedClasses.add("net.minecraft.class_329");
			Util.LOGGER.debug("InGameHud class (" + targetClassName + ") successfully modified.");
		}
	}

	private static boolean classNameEqualsMapped(String className, String classNameToBeMapped) {
		return className.equals(FabricLoader.getInstance().getMappingResolver().mapClassName("intermediary", classNameToBeMapped));
	}

	public static float getBossBarScreenPercent() {
		HudElement activeBossBarElement = HudContainer.getElementRegistry().getActiveElement(DefaultBossBarElement.IDENTIFIER);
		if (activeBossBarElement instanceof DefaultBossBarElement) {
			return ((DefaultBossBarElement) activeBossBarElement).getScaledMaxHeight();
		}
		return 1.0f / 3.0f;
	}

	public static boolean getForceDisplayHunger() {
		HudElement activeHungerElement = HudContainer.getElementRegistry().getActiveElement(DefaultHungerElement.IDENTIFIER);
		return activeHungerElement instanceof DefaultHungerElement && ((DefaultHungerElement) activeHungerElement).getForceDisplay();
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}

}
