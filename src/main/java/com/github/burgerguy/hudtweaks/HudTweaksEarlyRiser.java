package com.github.burgerguy.hudtweaks;

import java.util.ListIterator;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

import com.chocohead.mm.api.ClassTinkerers;
import com.github.burgerguy.hudtweaks.util.hud.StaticBossBarHelper;

public class HudTweaksEarlyRiser implements Runnable {
	
	@Override
	public void run() {
		ClassTinkerers.addTransformation("net.minecraft.client.gui.hud.BossBarHud", classNode -> {
			for(int i = 0; i < classNode.methods.size(); i++) {
				MethodNode methodNode = classNode.methods.get(i);
				if (methodNode.name.equals("render")) {
					ListIterator<AbstractInsnNode> itr = methodNode.instructions.iterator();
					while (itr.hasNext() && itr.next().getOpcode() != Opcodes.ICONST_3) {} // find ICONST_3 to start injection
					itr.remove();
					itr.next();
					itr.remove();
					LabelNode label = ((JumpInsnNode) itr.next()).label; // we know the next opcode is a comparison jump, so we extract the label from it
					itr.remove();
					itr.add(new InsnNode(Opcodes.I2F));
					itr.add(new FieldInsnNode(Opcodes.GETSTATIC, Type.getInternalName(StaticBossBarHelper.class), "SCREEN_PERCENT", Type.FLOAT_TYPE.getDescriptor()));
					itr.add(new InsnNode(Opcodes.FMUL));
					itr.add(new InsnNode(Opcodes.FCMPL));
					itr.add(new JumpInsnNode(Opcodes.IFLT, label));
					while (itr.hasPrevious() && itr.previous().getOpcode() != Opcodes.ALOAD) {} // find ALOAD to convert local var to float
					itr.add(new InsnNode(Opcodes.I2F));
					break;
				}
			}
		});
	}
	
}
