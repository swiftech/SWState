package com.github.swiftech.swstate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * {@link StateMachine} is different from {@link StateTransition}, the state is persisted by {@link StateProvider}.
 * It must starts from the initial state, which was specified by methods {@code initialize()} of {@link StateBuilder},
 * and transit states exactly as the rules built by {@link StateBuilder}. Multiple state cycles is supported by
 * giving an {@code id} to methods to differentiate them .
 * <p>
 * Usage:
 * <pre>
 *     1. Construct {@link StateMachine} with (default) or without {@link StateProvider}.
 *     2. Define states and actions by {@link StateBuilder}.
 *     3. Build the {@link StateMachine} instance from {@link StateBuilder}.
 *     4. Start the {@link StateBuilder} and post state transition request.
 * </pre>
 *
 * @param <S> type of State
 * @param <P> type of Payload
 * @author swiftech
 */
public class StateMachine<S extends Serializable, P extends Serializable> {

    private final Logger log = LoggerFactory.getLogger(StateMachine.class);

    private final StateTransition<S, P> stateTransition;

    private StateProvider<S> stateProvider;

    /**
     * Construct state machine with state builder and default state provider.
     */
    public StateMachine(StateBuilder<S, P> stateBuilder) {
        this.stateTransition = new StateTransition<>(stateBuilder);
        this.stateProvider = new DefaultStateProvider<>();
    }

    /**
     * Construct state machine with state builder and  customized state provider.
     *
     * @param stateProvider
     */
    public StateMachine(StateBuilder<S, P> stateBuilder, StateProvider<S> stateProvider) {
        this.stateTransition = new StateTransition<>(stateBuilder);
        this.stateProvider = stateProvider;
    }

    /**
     * Set user customized state provider.
     *
     * @param stateProvider
     */
    public void setStateProvider(StateProvider<S> stateProvider) {
        this.stateProvider = stateProvider;
    }

    /**
     * Check whether current state is provided state.
     *
     * @param id
     * @param state
     * @return
     */
    public boolean isState(String id, S state) {
        return stateProvider.isState(id, state);
    }

    /**
     * Check current state is in listed states.
     *
     * @param states
     * @return
     */
    public boolean isStateIn(String id, S... states) {
        return stateProvider.isStateIn(id, states);
    }

    /**
     * Get Current state for {@code id}.
     *
     * @param id
     * @return
     */
    public S getCurrentState(String id) {
        return stateProvider.getCurrentState(id);
    }

    /**
     * Reset state no matter what current state is.
     *
     * @param id
     * @param state
     */
    public void resetState(String id, S state) {
        stateProvider.setState(id, state);
    }

    /**
     * Start a new circulation from initial state.
     *
     * @param id
     */
    public void start(String id) {
        this.start(id, null);
    }

    /**
     * Start a new circulation from initial state.
     *
     * @param id
     * @param payload
     */
    public void start(String id, P payload) {
        S currentState = this.getCurrentState(id);
        if (currentState != null) {
            throw new RuntimeException(String.format("State machine for id '%s' is already started.", id));
        }
        S initialState = stateTransition.start(payload);
        stateProvider.initializeState(id, initialState);
    }

    /**
     * Start a new circulation from specified initial state.
     *
     * @param id
     * @param initialState
     */
    public void startState(String id, S initialState) {
        this.startState(id, initialState, null);
    }

    /**
     * Start a new circulation from specified initial state with payload.
     *
     * @param id
     * @param initialState
     * @param payload
     */
    public void startState(String id, S initialState, P payload) {
        S currentState = this.getCurrentState(id);
        if (currentState != null) {
            throw new RuntimeException(String.format("State machine for id '%s' is already started.", id));
        }
        stateTransition.startState(initialState, payload);
        stateProvider.initializeState(id, initialState);
    }

    /**
     * Post current state to provided state without any payload.
     *
     * @param id
     * @param toState
     * @return
     */
    public void post(String id, S toState) {
        this.post(id, toState, null);
    }

    /**
     * Post current state to provided state with payload.
     *
     * @param id
     * @param toState
     * @param payload
     * @return
     */
    public void post(String id, S toState, P payload) {
        S currentState = this.getCurrentState(id);
        log.trace(String.format("Current state for '%s' is '%s'", id, currentState));
        if (currentState == null) {
            throw new RuntimeException(String.format("State machine for '%s' is not started.", id));
        }
        stateTransition.post(currentState, toState, payload);
        stateProvider.setState(id, toState);
    }

}
