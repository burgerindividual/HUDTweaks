package com.github.burgerguy.hudtweaks.util.gl;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL43;

public class DrawTest implements AutoCloseable {
	private static final int QUERY_TARGET = getQueryTarget();
	private final int queryId;
	
	public DrawTest() {
		queryId = GL15.glGenQueries();
	}
	
	public void start() {
		GL15.glBeginQuery(QUERY_TARGET, queryId);
	}
	
	public void end() {
		GL15.glEndQuery(QUERY_TARGET);
	}
	
	public Boolean tryGetResultAsync() {
		if (GL15.glGetQueryObjecti(queryId, GL15.GL_QUERY_RESULT_AVAILABLE) == 1) {
			return GL15.glGetQueryObjecti(queryId, GL15.GL_QUERY_RESULT) > 0;
		}
		return null;
	}
	
	public boolean getResultSync() {
		return GL15.glGetQueryObjecti(queryId, GL15.GL_QUERY_RESULT) > 0;
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

	@Override
	public void close() {
		GL15.glDeleteQueries(queryId);
	}
}
