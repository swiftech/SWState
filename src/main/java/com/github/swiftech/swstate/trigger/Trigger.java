package com.github.swiftech.swstate.trigger;

/**
 * Trigger to automatically make state transit by input data.
 *
 * @author swiftech
 * @since 2.0
 */
@FunctionalInterface
public interface Trigger {

    /**
     * accept user input data to trigger the state transition.
     *
     * @param data
     * @param payload
     * @return true if data accepted.
     */
    boolean accept(Object data, Object payload);
}
