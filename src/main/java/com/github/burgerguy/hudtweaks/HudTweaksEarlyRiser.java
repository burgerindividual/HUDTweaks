package com.github.burgerguy.hudtweaks;

import java.util.ListIterator;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.chocohead.mm.api.ClassTinkerers;

public class HudTweaksEarlyRiser implements Runnable {
	
	@Override
	public void run() {
		ClassTinkerers.addTransformation("net.minecraft.client.gui.hud.BossBarHud", classNode -> {
			for(int i = 0; i < classNode.methods.size(); i++) {
				MethodNode methodNode = classNode.methods.get(i);
				if (methodNode.name.equals("render")) {
					ListIterator<AbstractInsnNode> itr = methodNode.instructions.iterator();
					while (itr.hasNext()) {
						AbstractInsnNode node = itr.next();
						if (node.getOpcode() == Opcodes.ICONST_3) {
							itr.remove();
							itr.next();
							itr.remove();
							break;
						}
					}
					break;
				}
			}
		});
	}
	
}
