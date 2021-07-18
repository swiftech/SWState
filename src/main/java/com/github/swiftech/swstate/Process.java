package com.github.swiftech.swstate;

import java.io.Serializable;

/**
 * A process represents things to do after entering a state or before exiting a state.
 *
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
