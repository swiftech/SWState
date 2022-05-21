package com.github.swiftech.swstate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * {@link StateMachine} is different from {@link StateTransition}, the state is persisted by {@link StateProvider}.
 * It must start from the initial state, which was specified by methods {@code initialize()} of {@link StateBuilder},
 * and transit states exactly as the rules built by {@link StateBuilder}. Multiple state cycles is supported by
 * giving a parameter {@code id} to methods to differentiate them, for only one cycle circumstance,
 * call methods without parameter {@code id}.
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

    private final String DEFAULT_ID = "DEFAULT_ID";

    /**
     * Construct state machine with state builder and default state provider.
     */
    public StateMachine(StateBuilder<S, P> stateBuilder) {
        this.stateTransition = new StateTransition<>(stateBuilder);
        this.stateProvider = new DefaultStateProvider<>();
        this.printInfo(stateBuilder);
    }

    /**
     * Construct state machine with state builder and  customized state provider.
     *
     * @param stateProvider
     */
    public StateMachine(StateBuilder<S, P> stateBuilder, StateProvider<S> stateProvider) {
        this.stateTransition = new StateTransition<>(stateBuilder);
        this.stateProvider = stateProvider;
        this.printInfo(stateBuilder);
    }

    private void printInfo(StateBuilder<S, P> stateBuilder) {
        log.debug(stateBuilder.getMetaInfo());
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
     * Check whether state for default id is provided state.
     *
     * @param state
     * @return
     */
    public boolean isState(S state) {
        return this.isState(DEFAULT_ID, state);
    }

    /**
     * Check whether state for {@code id} is provided state.
     *
     * @param id
     * @param state
     * @return
     */
    public boolean isState(String id, S state) {
        return stateProvider.isState(id, state);
    }

    /**
     * Check state for default id is in listed states.
     *
     * @param states
     * @return
     */
    public boolean isStateIn(S... states) {
        return stateProvider.isStateIn(DEFAULT_ID, states);
    }

    /**
     * Check state for {@code id} is in listed states.
     *
     * @param states
     * @return
     */
    public boolean isStateOfIdIn(String id, S... states) {
        return stateProvider.isStateIn(id, states);
    }

    /**
     * Get Current state for default id.
     *
     * @return
     */
    public S getCurrentState() {
        return getCurrentState(DEFAULT_ID);
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
     * Reset state no matter what state for default id is.
     *
     * @param state
     */
    public void resetState(S state) {
        this.resetState(DEFAULT_ID, state);
    }

    /**
     * Reset state no matter what state for {@code id} is.
     *
     * @param id
     * @param state
     */
    public void resetState(String id, S state) {
        stateProvider.setState(id, state);
    }

    /**
     * Start a new circulation from initial state for default id.
     */
    public void start() {
        this.start(DEFAULT_ID);
    }

    /**
     * Start a new circulation from initial state for {@code id}.
     *
     * @param id
     */
    public void start(String id) {
        this.start(id, null);
    }

    /**
     * Start a new circulation from initial state for {@code id}.
     *
     * @param id
     * @param payload
     */
    public void start(String id, P payload) {
        S currentState = this.getCurrentState(id);
        if (currentState != null) {
            throw new StateException(String.format("State machine for id '%s' is already started.", id));
        }
        S initialState = stateTransition.start(payload);
        stateProvider.initializeState(id, initialState);
    }

    /**
     * Start a new circulation from specified initial state for default id.
     *
     * @param initialState
     */
    public void startState(S initialState) {
        this.startState(DEFAULT_ID, initialState);
    }

    /**
     * Start a new circulation from specified initial state with payload for default id.
     *
     * @param initialState
     * @param payload
     */
    public void startStateWithPayload(S initialState, P payload) {
        this.startStateWithPayload(DEFAULT_ID, initialState, payload);
    }

    /**
     * Start a new circulation from specified initial state for {@code id}.
     *
     * @param id
     * @param initialState
     */
    public void startState(String id, S initialState) {
        this.startStateWithPayload(id, initialState, null);
    }

    /**
     * Start a new circulation from specified initial state with payload for {@code id}.
     *
     * @param id
     * @param initialState
     * @param payload
     */
    public void startStateWithPayload(String id, S initialState, P payload) {
        S currentState = this.getCurrentState(id);
        if (currentState != null) {
            throw new StateException(String.format("State machine for id '%s' is already started.", id));
        }
        stateTransition.startState(initialState, payload);
        stateProvider.initializeState(id, initialState);
    }

    /**
     * Post current state to provided state without any payload for default id.
     *
     * @param toState
     */
    public void post(S toState) {
        this.post(DEFAULT_ID, toState);
    }

    /**
     * Post current state to provided state with payload for default id.
     *
     * @param toState
     * @param payload
     */
    public void postWithPayload(S toState, P payload) {
        this.postWithPayload(DEFAULT_ID, toState, payload);
    }

    /**
     * Post current state to provided state without any payload for {@code id}.
     *
     * @param id
     * @param toState
     * @return
     */
    public void post(String id, S toState) {
        this.postWithPayload(id, toState, null);
    }

    /**
     * Post current state to provided state with payload for {@code id}.
     *
     * @param id
     * @param toState
     * @param payload
     * @return
     */
    public void postWithPayload(String id, S toState, P payload) {
        S currentState = this.getCurrentState(id);
        log.trace(String.format("Current state for '%s' is '%s'", id, currentState));
        if (currentState == null) {
            throw new StateException(String.format("State machine for '%s' is not started.", id));
        }
        stateTransition.post(currentState, toState, payload);
        stateProvider.setState(id, toState);
    }

}
