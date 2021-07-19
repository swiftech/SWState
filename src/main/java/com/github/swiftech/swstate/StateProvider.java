package com.github.swiftech.swstate;

import java.io.Serializable;

/**
 * State provider let you implement your own state provider to store and retrieve states.
 *
 * @param <S> type of state object
 * @author swiftech
 */
public interface StateProvider<S extends Serializable> {

    /**
     * Get Current state
     *
     * @return
     */
    S getCurrentState(String id);

    /**
     * Initialize state
     *
     * @param id
     * @param state
     */
    void initializeState(String id, S state);

    /**
     * Set state
     *
     * @param id
     * @param state
     */
    void setState(String id, S state);

    /**
     * Check whether current state is provided state.
     *
     * @param id
     * @param state
     * @return
     */
    boolean isState(String id, S state);

    /**
     * Check current state is in listed states.
     *
     * @param id
     * @param states
     * @return
     */
    boolean isStateIn(String id, S... states);

}
