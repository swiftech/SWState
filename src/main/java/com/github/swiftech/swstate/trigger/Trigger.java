package com.github.swiftech.swstate.trigger;

/**
 * @author swiftech
 * @since 2.0
 * @param <T> type of data to trigger.
 */
@FunctionalInterface
public interface Trigger {

    boolean accept(Object data, Object payload);
}
