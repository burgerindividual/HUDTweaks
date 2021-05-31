package com.github.burgerguy.hudtweaks.util.gl;

import com.mojang.blaze3d.systems.RenderSystem;

import java.util.ArrayDeque;
import java.util.Deque;

public enum ScissorStack {
	;

	private static final Deque<ScissorArea> areaStack = new ArrayDeque<>();

	public static void pushScissorArea(int x1, int y1, int x2, int y2) {
		ScissorArea area;
		ScissorArea lastArea = areaStack.peek();
		if (lastArea != null) {
			area = new ScissorArea(Math.max(x1, lastArea.x1), Math.max(y1, lastArea.y1), Math.min(x2, lastArea.x2), Math.min(y2, lastArea.y2));
		} else {
			area = new ScissorArea(x1, y1, x2, y2);
		}
		RenderSystem.enableScissor(area.x1, area.y1, area.x2, area.y2);
		areaStack.push(area);
	}

	public static void popScissorArea() {
		areaStack.poll();
		ScissorArea nextArea = areaStack.peek();
		if (nextArea != null) {
			RenderSystem.enableScissor(nextArea.x1, nextArea.y1, nextArea.x2, nextArea.y2);
		} else {
			RenderSystem.disableScissor();
		}
	}

	private static class ScissorArea {
		public final int x1, y1, x2, y2;

		public ScissorArea(int x1, int y1, int x2, int y2) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}
	}
}
