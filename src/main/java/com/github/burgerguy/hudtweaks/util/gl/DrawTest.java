package com.github.burgerguy.hudtweaks.util.gl;

import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;

public class DrawTest implements AutoCloseable {
	private static final int QUERY_TARGET = getQueryTarget();

	private final int queryId;
	private final long pointer;

	// these two variables have to be kept track of separately to avoid hard syncing the call state to the query state.
	private QueryState queryState;
	private LastCallState lastCallState;
	private boolean latestResult;

	public DrawTest() {
		queryId = GL15C.glGenQueries();
		// use the first 4 bytes to store the result, use the last 1 byte to store the availability.
		pointer = MemoryUtil.nmemAlloc(4 + 1);
	}

	public void markStart() {
		if (lastCallState != null && lastCallState.equals(LastCallState.BEGIN)) {
			throw new IllegalStateException("Start called twice in a row");
		}
		lastCallState = LastCallState.BEGIN;

		// ACTIVE is indirectly handled above through BEGIN
		// ignore if FINISHED
		if (queryState == null || queryState.equals(QueryState.RETRIEVED)) {
			queryState = QueryState.ACTIVE;
			GL15C.glBeginQuery(QUERY_TARGET, queryId);
		}
	}

	public void markEnd() {
		if (lastCallState == null || lastCallState.equals(LastCallState.UPDATE)) {
			throw new IllegalStateException("End called before start");
		} else if (lastCallState.equals(LastCallState.END)) {
			throw new IllegalStateException("End called twice in a row");
		}
		lastCallState = LastCallState.END;

		// RETRIEVED is indirectly handled above through UPDATE
		// ignore if FINISHED as long as END wasn't called multiple times in a row
		// queryState null indirectly handled by lastCallState null
		if (queryState.equals(QueryState.ACTIVE)) {
			GL15C.glEndQuery(QUERY_TARGET);
			queryState = QueryState.FINISHED;
		}
	}

	public boolean isActive() {
		return queryState != null && queryState.equals(QueryState.ACTIVE);
	}

	private void updateResult() {
		if (lastCallState == null || lastCallState.equals(LastCallState.UPDATE)) {
			// this happens when begin and end weren't called, so we can assume that the entire rendering method wasn't
			// called.
			latestResult = false;
			lastCallState = LastCallState.UPDATE;
			return;
		} else if (lastCallState.equals(LastCallState.BEGIN)) {
			throw new IllegalStateException("Update called before end");
		}
		lastCallState = LastCallState.UPDATE;

		// ACTIVE indirectly handled above through BEGIN
		// RETRIEVED indirectly handled above through multiple calls to UPDATE
		// queryState null indirectly handled by lastCallState null
		if (queryState.equals(QueryState.FINISHED)) {
			// get availability
			GL15C.nglGetQueryObjectiv(queryId, GL15C.GL_QUERY_RESULT_AVAILABLE, pointer + 4);
			boolean available = MemoryUtil.memGetBoolean(pointer + 4);
			if (available) {
				// get result
				GL15C.nglGetQueryObjectiv(queryId, GL15C.GL_QUERY_RESULT, pointer);
				latestResult = MemoryUtil.memGetInt(pointer) > 0;
				queryState = QueryState.RETRIEVED;
			}
		}
	}

	public boolean getResult() {
		updateResult();
		return latestResult;
	}

	private static int getQueryTarget() {
		// occlusion queries are slower and buggier in some instances, so let's just stick to primitives generated.
		return GL30C.GL_PRIMITIVES_GENERATED;
	}
	
	@Override
	public void close() {
		GL15C.glDeleteQueries(queryId);
		MemoryUtil.nmemFree(pointer);
	}

	private enum QueryState {
		/**
		 * State for when the query is between its beginning and end. GL calls during this will be recorded if applicable.
		 */
		ACTIVE,
		/**
		 * State for when the query is past its end, but hasn't had its value retrieved.
		 */
		FINISHED,
		/**
		 * State for when the query has had its value retrieved.
		 */
		RETRIEVED
	}

	private enum LastCallState {
		BEGIN,
		END,
		UPDATE
	}
}
