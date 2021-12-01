package com.github.burgerguy.hudtweaks.asm;

import com.github.burgerguy.hudtweaks.hud.HudContainer;
import com.github.burgerguy.hudtweaks.hud.element.DefaultBossBarElement;
import com.github.burgerguy.hudtweaks.hud.element.DefaultHealthElement;
import com.github.burgerguy.hudtweaks.hud.element.DefaultHungerElement;
import com.github.burgerguy.hudtweaks.hud.element.HudElement;
import com.github.burgerguy.hudtweaks.util.Util;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.*;

public class HTMixinPlugin implements IMixinConfigPlugin {

	private static boolean UNRESTRICT_BOSS_BAR_ALLOWED = false;
	private static boolean FORCE_DISPLAY_HUNGER_ALLOWED = false;
	private static boolean FLIP_HEALTH_LINES_ALLOWED = false;

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

	public static boolean canUnrestrictBossBar() {
		return UNRESTRICT_BOSS_BAR_ALLOWED;
	}

	public static boolean canForceDisplayHunger() {
		return FORCE_DISPLAY_HUNGER_ALLOWED;
	}

	public static boolean canFlipHealthLines() {
		return FLIP_HEALTH_LINES_ALLOWED;
	}

	private static final Set<String> appliedClasses = new HashSet<>();

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
		if (!appliedClasses.contains("net.minecraft.class_337") && classNameEqualsMapped("net.minecraft.class_337", targetClassName)) { // BossBarHud class
			methodLoop:
			for (MethodNode methodNode : targetClass.methods) {
				if (methodEqualsMapped("net.minecraft.class_337", "method_1796", "(Lnet/minecraft/class_4587;)V", methodNode.name, methodNode.desc)) { // render method
					InsnList oldInstructions = cloneInsnList(methodNode.instructions);
					try {
						ListIterator<AbstractInsnNode> itr = methodNode.instructions.iterator();
						while (true) {
							if (itr.hasNext()) {
								if (itr.next().getOpcode() == Opcodes.ICONST_3)
									break; // find ICONST_3 to start injection
							} else {
								Util.LOGGER.error("BossBarHud class (" + targetClassName + ") unable to find injection point for screen percent modification.");
								continue methodLoop;
							}
						}
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
						while (true) if (!itr.hasPrevious() || itr.previous().getOpcode() == Opcodes.ALOAD)
							break; // find ALOAD to convert local var to float
						itr.add(new InsnNode(Opcodes.I2F));
						UNRESTRICT_BOSS_BAR_ALLOWED = true;

						Util.LOGGER.info("BossBarHud class (" + targetClassName + ") screen percent modification successful.");
					} catch (Exception e) {
						Util.LOGGER.error("Error injecting into BossBarHud class (" + targetClassName + ") for screen percent modification, reverting changes...");
						methodNode.instructions = oldInstructions;
					}
				}
			}
			appliedClasses.add("net.minecraft.class_337");
		}

		if (!appliedClasses.contains("net.minecraft.class_329") && classNameEqualsMapped("net.minecraft.class_329", targetClassName)) { // InGameHud class
			methodLoop:
			for (MethodNode methodNode : targetClass.methods) {
				if (methodEqualsMapped("net.minecraft.class_329", "method_1760", "(Lnet/minecraft/class_4587;)V", methodNode.name, methodNode.desc)) { // renderStatusBard
					InsnList oldInstructions = cloneInsnList(methodNode.instructions);
					try {
						ListIterator<AbstractInsnNode> itr = methodNode.instructions.iterator();
						boolean foundLdc = false; // find LDC "food" to get bearings, then move back to before jump (likely IFNE) to start injection
						while (true) {
							if ((!foundLdc && itr.hasNext()) || (foundLdc && itr.hasPrevious())) {
								if (foundLdc) {
									if (itr.previous().getType() == AbstractInsnNode.JUMP_INSN) break;
								} else if (itr.next() instanceof LdcInsnNode ldcInsn && ldcInsn.cst.equals("food")) {
									foundLdc = true;
								}
							} else {
								Util.LOGGER.error("InGameHud class (" + targetClassName + ") unable to find injection point for force hunger modification.");
								continue methodLoop;
							}
						}
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
						FORCE_DISPLAY_HUNGER_ALLOWED = true;
						Util.LOGGER.info("InGameHud class (" + targetClassName + ") force hunger modification successful.");
					} catch (Exception e) {
						Util.LOGGER.error("Error injecting into InGameHud class (" + targetClassName + ") for force hunger modification, reverting changes...");
						methodNode.instructions = oldInstructions;
					}
				} else if (methodEqualsMapped("net.minecraft.class_329", "method_37298", "(Lnet/minecraft/class_4587;Lnet/minecraft/class_1657;IIIIFIIIZ)V", methodNode.name, methodNode.desc)) { // renderHealthBar
					InsnList oldInstructions = cloneInsnList(methodNode.instructions);
					try {
						ListIterator<AbstractInsnNode> itr = methodNode.instructions.iterator();
						boolean foundBipush = false; // find BIPUSH 8 because it's right before our target. from there, we can continue to the ISUB.
						while (true) {
							if (itr.hasNext()) {
								AbstractInsnNode insn = itr.next();
								if (foundBipush) {
									if (insn.getOpcode() == Opcodes.ISUB) break;
								} else if (insn instanceof IntInsnNode intInsn && intInsn.operand == 8) {
									foundBipush = true;
								}
							} else {
								Util.LOGGER.error("InGameHud class (" + targetClassName + ") unable to find injection point for flip health lines modification.");
								continue methodLoop;
							}
						}
						itr.previous();
						itr.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(HTMixinPlugin.class), "getFlipHealthLines", Type.getMethodDescriptor(Type.BOOLEAN_TYPE)));
						LabelNode addLabel = new LabelNode();
						itr.add(new JumpInsnNode(Opcodes.IFNE, addLabel));
						itr.next();
						LabelNode preStoreLabel = new LabelNode();
						itr.add(new JumpInsnNode(Opcodes.GOTO, preStoreLabel));
						itr.add(addLabel);
						itr.add(new InsnNode(Opcodes.IADD));
						itr.add(new JumpInsnNode(Opcodes.GOTO, preStoreLabel));
						itr.add(preStoreLabel);
						FLIP_HEALTH_LINES_ALLOWED = true;
						Util.LOGGER.info("InGameHud class (" + targetClassName + ") flip health lines modification successful.");
					} catch (Exception e) {
						Util.LOGGER.error("Error injecting into InGameHud class (" + targetClassName + ") for flip health lines modification, reverting changes...");
						methodNode.instructions = oldInstructions;
					}
				}
			}
			appliedClasses.add("net.minecraft.class_329");
		}
	}

	private static boolean classNameEqualsMapped(String className, String runtimeName) {
		return runtimeName.equals(FabricLoader.getInstance().getMappingResolver().mapClassName("intermediary", className));
	}

	private static boolean methodEqualsMapped(String methodOwner, String methodName, String methodDescriptor, String runtimeName, String runtimeDescriptor) {
		return runtimeName.equals(FabricLoader.getInstance().getMappingResolver().mapMethodName("intermediary", methodOwner, methodName, methodDescriptor)) && runtimeDescriptor.equals(MixinEnvironment.getCurrentEnvironment().getRemappers().mapDesc(methodDescriptor));
	}

	// FIXME: USE VISITOR API!!! THIS IS GROSS!!!!
	private static Map<LabelNode, LabelNode> cloneLabels(InsnList insns) {
		HashMap<LabelNode, LabelNode> labelMap = new HashMap<>();
		for (AbstractInsnNode insn = insns.getFirst(); insn != null; insn = insn.getNext()) {
			if (insn.getType() == AbstractInsnNode.LABEL) {
				labelMap.put((LabelNode) insn, new LabelNode());
			}
		}
		return labelMap;
	}

	private static InsnList cloneInsnList(InsnList insns) {
		return cloneInsnList(cloneLabels(insns), insns);
	}

	private static InsnList cloneInsnList(Map<LabelNode, LabelNode> labelMap, InsnList insns) {
		InsnList clone = new InsnList();
		for (AbstractInsnNode insn = insns.getFirst(); insn != null; insn = insn.getNext()) {
			clone.add(insn.clone(labelMap));
		}

		return clone;
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

	public static boolean getFlipHealthLines() {
		HudElement activeHealthElement = HudContainer.getElementRegistry().getActiveElement(DefaultHealthElement.IDENTIFIER);
		return activeHealthElement instanceof DefaultHealthElement && ((DefaultHealthElement) activeHealthElement).isFlipped();
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}

}
