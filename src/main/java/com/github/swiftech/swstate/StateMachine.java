package com.github.swiftech.swstate;

import com.github.swiftech.swstate.trigger.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Map;

/**
 * {@link StateMachine} is different from {@link StateTransition}, the state is persisted by {@link StateProvider}.
 * It must start from the initial state, which was specified by methods {@code initialize()} of {@link StateBuilder},
 * and transit states exactly as the rules built by {@link StateBuilder}. Multiple state cycles is supported by
 * giving a parameter {@code id} to methods to differentiate them, for only one cycle circumstance,
 * call methods without parameter {@code id}.
 * <p>
 * Usage:
 * <pre>
 *     1. Define states and actions by {@link StateBuilder}.
 *     2. Define your own {@link StateProvider} if required.
 *     3. Construct the {@link StateMachine} instance from {@link StateBuilder} with or without customized {@link StateProvider}.
 *     4. Start the {@link StateMachine}.
 *     5. Post state transition request at anywhere you want to transit states.
 * </pre>
 *
 * @param <S> type of State
 * @param <P> type of Payload
 * @see StateBuilder
 * @author swiftech
 */
public class StateMachine<S extends Serializable, P extends Serializable> {

    private final Logger log = LoggerFactory.getLogger(StateMachine.class);

    private final StateTransition<S, P> stateTransition;

    private StateProvider<S> stateProvider;

    private final Map<S, Map<Trigger, S>> triggerMap;

    private final String DEFAULT_ID = "DEFAULT_ID";

    /**
     * Construct state machine with state builder and default state provider.
     */
    public StateMachine(StateBuilder<S, P> stateBuilder) {
        this.stateTransition = new StateTransition<>(stateBuilder);
        this.triggerMap = stateBuilder.triggerMap;
        this.stateProvider = new DefaultStateProvider<>();
        this.printInfo(stateBuilder);
    }

    /**
     * Construct state machine with state builder and customized state provider.
     *
     * @param stateProvider
     */
    public StateMachine(StateBuilder<S, P> stateBuilder, StateProvider<S> stateProvider) {
        this.stateTransition = new StateTransition<>(stateBuilder);
        this.triggerMap = stateBuilder.triggerMap;
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
     * Get the current state for default id.
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
     * Start a new circulation from initial state for default id.
     *
     * @param payload
     */
    public void startWithPayload(P payload) {
        this.start(DEFAULT_ID, payload);
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
     * Post current state to be provided state without any payload for default id.
     *
     * @param toState
     */
    public void post(S toState) {
        this.post(DEFAULT_ID, toState);
    }

    /**
     * Post current state to be provided state with payload for default id.
     *
     * @param toState
     * @param payload
     */
    public void postWithPayload(S toState, P payload) {
        this.postWithPayload(DEFAULT_ID, toState, payload);
    }

    /**
     * Post current state to be provided state without any payload for {@code id}.
     *
     * @param id
     * @param toState
     * @return
     */
    public void post(String id, S toState) {
        this.postWithPayload(id, toState, null);
    }

    /**
     * Post to the target state based on the current state and the condition state provided.
     * If the current state is not matched to the conditional state, nothing will happen.
     *
     * @param toState
     * @param conditionState
     */
    public void postOnState(final S toState, final S conditionState) {
        this.postOnState(DEFAULT_ID, toState, conditionState);
    }

    /**
     * Post to the target state based on the current state and the condition states provided.
     * If no state matches, nothing will happen.
     *
     * @param toState1
     * @param conditionState1 Conditional state to state 1
     * @param toState2
     * @param conditionState2 Conditional state to state 1
     */
    public void postOnState(final S toState1, final S conditionState1, final S toState2, final S conditionState2) {
        this.postOnState(DEFAULT_ID, toState1, conditionState1, toState2, conditionState2);
    }

    /**
     * Determine the target state of the current state according to the provided conditional state mapping,
     * and post the current state carrying the payload into the target state.
     * If no state matches, nothing will happen.
     *
     * @param conditionStateMap Conditional state mapping used to determine the target state based on the current state
     */
    public void postOnState(Map<S, S> conditionStateMap) {
        this.postWithPayloadOnState(DEFAULT_ID, conditionStateMap, null);
    }

    /**
     * Post to the target state based on the current state and the condition state provided.
     * If the current state is not matched to the conditional state, nothing will happen.
     *
     * @param toState
     * @param conditionState
     */
    public void postOnState(String id, final S toState, final S conditionState) {
        this.postWithPayloadOnState(id, toState, conditionState, null);
    }

    /**
     * Post to the target state based on the current state and the condition states provided.
     * If no state matches, nothing will happen.
     *
     * @param toState1
     * @param conditionState1 Conditional state to state 1
     * @param toState2
     * @param conditionState2 Conditional state to state 1
     */
    public void postOnState(String id, final S toState1, final S conditionState1, final S toState2, final S conditionState2) {
        this.postWithPayloadOnState(id, toState1, conditionState1, toState2, conditionState2, null);
    }

    /**
     * Determine the target state of the current state according to the provided conditional state mapping,
     * and post the current state carrying the payload into the target state.
     * If no state matches, nothing will happen.
     *
     * @param id
     * @param conditionStateMap Conditional state mapping used to determine the target state based on the current state
     */
    public void postOnState(String id, Map<S, S> conditionStateMap) {
        this.postWithPayloadOnState(id, conditionStateMap, null);
    }

    /**
     * Post current state to be provided state with payload for {@code id}.
     *
     * @param id
     * @param toState
     * @param payload
     * @return
     */
    public void postWithPayload(String id, S toState, P payload) {
        S currentState = this.getCurrentState(id);
        if (log.isTraceEnabled()) log.trace(String.format("Current state for '%s' is '%s'", id, currentState));
        if (currentState == null) {
            throw new StateException(String.format("State machine for '%s' is not started.", id));
        }
        stateTransition.post(currentState, toState, payload);
        stateProvider.setState(id, toState);
    }

    /**
     * Post to the target state based on the current state and the condition state provided.
     * If the current state is not matched to the conditional state, nothing will happen.
     *
     * @param toState
     * @param conditionState
     * @param payload
     */
    public void postWithPayloadOnState(final S toState, final S conditionState, P payload) {
        this.postWithPayloadOnState(DEFAULT_ID, toState, conditionState, payload);
    }

    /**
     * Post to the target state based on the current state and the condition state provided.
     * If the current state is not matched to the conditional state, nothing will happen.
     *
     * @param id
     * @param toState
     * @param conditionState
     * @param payload
     */
    public void postWithPayloadOnState(String id, final S toState, final S conditionState, P payload) {
        if (this.isState(conditionState)) {
            this.postWithPayload(id, toState, payload);
        }
    }

    /**
     * Post to the target state based on the current state and the condition states provided.
     * If no state matches, nothing will happen.
     *
     * @param toState1
     * @param conditionState1 Conditional state to state 1
     * @param toState2
     * @param conditionState2 Conditional state to state 1
     * @param payload
     */
    public void postWithPayloadOnState(final S toState1, final S conditionState1, final S toState2, final S conditionState2, P payload) {
        this.postWithPayloadOnState(DEFAULT_ID, toState1, conditionState1, toState2, conditionState2, payload);
    }

    /**
     * Post to the target state based on the current state and the condition states provided.
     * If no state matches, nothing will happen.
     *
     * @param id
     * @param toState1
     * @param conditionState1 Conditional state to state 1
     * @param toState2
     * @param conditionState2 Conditional state to state 1
     * @param payload
     */
    public void postWithPayloadOnState(String id, final S toState1, final S conditionState1, final S toState2, final S conditionState2, P payload) {
        if (this.isState(conditionState1)) {
            this.postWithPayload(id, toState1, payload);
        }
        else if (this.isState(conditionState2)) {
            this.postWithPayload(id, toState2, payload);
        }
    }

    /**
     * Determine the target state of the current state according to the provided conditional state mapping,
     * and post the current state carrying the payload into the target state.
     * If no state matches, nothing will happen.
     *
     * @param id
     * @param conditionStateMap Conditional state mapping used to determine the target state based on the current state
     * @param payload
     */
    public void postWithPayloadOnState(String id, Map<S, S> conditionStateMap, P payload) {
        S targetState = conditionStateMap.get(this.getCurrentState());
        if (targetState != null) {
            this.postWithPayload(id, targetState, payload);
        }
        else {
            log.debug("No target state found for current state %s".formatted(this.getCurrentState()));
        }
    }

    /**
     * Accept data from the client to trigger state transition.
     *
     * @param data
     * @return true if data is accepted.
     * @since 2.0
     */
    public boolean accept(Object data) {
        return this.accept(DEFAULT_ID, data);
    }

    /**
     * Accept data from the client to trigger state transition.
     *
     * @param id
     * @param data
     * @return true if data is accepted.
     * @since 2.0
     */
    public boolean accept(String id, Object data) {
        return this.acceptWithPayload(id, data, null);
    }

    /**
     * Accept data from the client to trigger state transition with payload.
     *
     * @param data
     * @param payload
     * @return true if data is accepted.
     * @since 2.0
     */
    public boolean acceptWithPayload(Object data, P payload) {
        return this.acceptWithPayload(DEFAULT_ID, data, payload);
    }

    /**
     * Accept data from the client to trigger state transition with payload.
     *
     * @param id
     * @param data
     * @param payload
     * @return true if data is accepted.
     * @since 2.0
     */
    public boolean acceptWithPayload(String id, Object data, P payload) {
        Map<Trigger, S> toByTriggerMap = triggerMap.get(this.getCurrentState());
        for (Trigger trigger : toByTriggerMap.keySet()) {
            if (trigger.accept(data, payload)) {
                if (log.isDebugEnabled())
                    log.debug("Accept '%s' with payload '%s'".formatted(data, Utils.payloadSummary(payload)));
                S stateTo = toByTriggerMap.get(trigger);
                // transit to the next state
                this.postWithPayload(id, stateTo, payload);
                return true;
            }
        }
        return false;
    }

    /**
     * Handler to be notified when an internal exception occurs.
     *
     * @param exceptionHandler
     */
    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.stateTransition.setExceptionHandler(exceptionHandler);
    }

    /**
     * Set whether throws exception when an internal exception occurs.
     *
     * @param silent
     */
    public void setSilent(boolean silent) {
        this.stateTransition.setSilent(silent);
    }

    /**
     * if true, the IN process will not be executed for self-circulation state transition.
     *
     * @param noInProcessForSelfCirculation
     * @since 2.2
     */
    public void setNoInProcessForSelfCirculation(boolean noInProcessForSelfCirculation) {
        this.stateTransition.setNoInProcessForSelfCirculation(noInProcessForSelfCirculation);
    }

    /**
     * if true, the OUT process will not be executed for self-circulation state transition.
     *
     * @param noOutProcessForSelfCirculation
     * @since 2.2
     */
    public void setNoOutProcessForSelfCirculation(boolean noOutProcessForSelfCirculation) {
        this.stateTransition.setNoOutProcessForSelfCirculation(noOutProcessForSelfCirculation);
    }
}
