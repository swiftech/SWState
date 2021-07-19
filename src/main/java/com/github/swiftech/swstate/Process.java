package com.github.swiftech.swstate;

import java.io.Serializable;

/**
 * A process represents things to do after entering a state or before exiting a state.
 *
 * @param <P> type of Payload
 * @author swiftech
 */
public interface Process<P extends Serializable> {

	/**
	 * Execute process with a payload.
	 *
	 * @param payload
	 */
	void execute(P payload);
}
