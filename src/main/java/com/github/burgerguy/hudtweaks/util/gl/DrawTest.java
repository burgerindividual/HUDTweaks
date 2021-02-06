package com.github.burgerguy.hudtweaks.util.gl;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryUtil;

public class DrawTest implements AutoCloseable {
	private static final int QUERY_TARGET = getQueryTarget();
	private final int queryId;
	private boolean active;
	
	private final long pointer;
	
	public DrawTest() {
		queryId = GL15.glGenQueries();
		pointer = MemoryUtil.nmemAlloc(5); // use the first 4 bytes to store the result, use the last 1 byte to store the availability.
	}
	
	/**
	 * @return true if the method was called inactive active.
	 */
	public boolean start() {
		if (!active) {
			active = true;
			GL15.glBeginQuery(QUERY_TARGET, queryId);
			return true;
		}
		return false;
	}
	
	/**
	 * @return true if the method was called while active.
	 */
	public boolean end() {
		if (active) {
			GL15.glEndQuery(QUERY_TARGET);
			active = false;
			return true;
		}
		return false;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public boolean getAvailability() {
		GL15.nglGetQueryObjectiv(queryId, GL15.GL_QUERY_RESULT_AVAILABLE, pointer + 4);
		return MemoryUtil.memGetBoolean(pointer + 4);
	}
	
	public boolean getResultSync() {
		GL15.nglGetQueryObjectiv(queryId, GL15.GL_QUERY_RESULT, pointer);
		return MemoryUtil.memGetInt(pointer) > 0;
	}
	
	/**
	 * @return null if the result is not available, otherwise return the result.
	 */
	public Boolean tryGetResultAsync() {
		if (getAvailability()) {
			return getResultSync();
		}
		return null;
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
		MemoryUtil.nmemFree(pointer);
	}
}
