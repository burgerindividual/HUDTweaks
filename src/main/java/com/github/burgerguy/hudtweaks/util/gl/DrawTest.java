package com.github.burgerguy.hudtweaks.util.gl;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL43;

import com.github.burgerguy.hudtweaks.hud.element.HudElement;

public class DrawTest {
	private static final int QUERY_TARGET = getQueryTarget();
	private final int queryId;
	
	private HudElement elementToSet;
	
	public DrawTest() {
		queryId = GL15.glGenQueries();
	}
	
	public void start(HudElement elementToSet) {
		this.elementToSet = elementToSet;
		if (elementToSet != null) GL15.glBeginQuery(QUERY_TARGET, queryId);
	}
	
	public void end() {
		if (elementToSet != null) {
			GL15.glEndQuery(QUERY_TARGET);
			elementToSet.setRendered(GL15.glGetQueryObjecti(queryId, GL15.GL_QUERY_RESULT) > 0);
		}
	}
	
	private static int getQueryTarget() {
		int queryTarget = GL15.GL_SAMPLES_PASSED;
		if (GL.getCapabilities().OpenGL33) {
			queryTarget = GL33.GL_ANY_SAMPLES_PASSED;
			if (GL.getCapabilities().OpenGL43) {
				queryTarget = GL43.GL_ANY_SAMPLES_PASSED_CONSERVATIVE;
			}
		}
		
		return queryTarget;
	}
}
