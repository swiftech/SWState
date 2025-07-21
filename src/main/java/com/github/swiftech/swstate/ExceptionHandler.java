package com.github.swiftech.swstate;

/**
 * @since 2.1
 */
@FunctionalInterface
public interface ExceptionHandler {

    void onException(StateException stateException);
}
