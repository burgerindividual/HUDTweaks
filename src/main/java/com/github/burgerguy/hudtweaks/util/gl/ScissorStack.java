package com.github.burgerguy.hudtweaks.util.gl;

import java.util.ArrayDeque;
import java.util.Deque;

import com.mojang.blaze3d.systems.RenderSystem;

public enum ScissorStack {
	;
	
	private static final Deque<ScissorArea> areaStack = new ArrayDeque<>();
	
	public static void pushScissorArea(int x1, int x2, int y1, int y2) {
		RenderSystem.enableScissor(x1, x2, y1, y2);
		areaStack.push(new ScissorArea(x1, x2, y1, y2));
	}
	
	public static void popScissorArea() {
		areaStack.poll();
		ScissorArea nextArea = areaStack.peek();
		if (nextArea != null) {
			RenderSystem.enableScissor(nextArea.x1, nextArea.x2, nextArea.y1, nextArea.y2);
		} else {
			RenderSystem.disableScissor();
		}
	}
	
	private static class ScissorArea {
		public final int x1, x2, y1, y2;
		
		public ScissorArea(int x1, int x2, int y1, int y2) {
			this.x1 = x1;
			this.x2 = x2;
			this.y1 = y1;
			this.y2 = y2;
		}
	}
}
