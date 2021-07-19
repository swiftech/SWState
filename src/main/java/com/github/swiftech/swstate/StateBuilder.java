package com.github.swiftech.swstate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.swiftech.swstate.Mapping.*;

/**
 * Builds states with actions and transition processing.
 *
 * @param <S> type of State
 * @param <P> type of Payload
 * @author swiftech
 * @see StateTransition
 */
public class StateBuilder<S extends Serializable, P extends Serializable> {

    // state from -> state to -> action
    final Map<S, Map<S, Action<S>>> actionMap = new HashMap<>();

    // mapping for states and transition processing.
    final Mapping<S, P> stateMapping = new Mapping<>();

    private S composeState;

    /**
     * Add an action to the initial state.
     *
     * @param initialState
     * @return
     */
    public StateBuilder<S, P> initialize(S initialState) {
        this.action("init action", null, initialState);
        return this;
    }

    /**
     * Add an action with name to the initial state.
     *
     * @param name
     * @param initialState
     * @return
     */
    public StateBuilder<S, P> initialize(String name, S initialState) {
        this.action(name, null, initialState);
        return this;
    }

    /**
     * Add an action from one state to another.
     *
     * @param name
     * @param stateFrom the state before the action happens.
     * @param stateTo   the state after the action happens.
     * @return
     */
    public StateBuilder<S, P> action(String name, S stateFrom, S stateTo) {
        if (!hasRoute(stateFrom, stateTo)) {
            Map<S, Action<S>> toMap = actionMap.computeIfAbsent(stateFrom, k -> new HashMap<>());
            toMap.put(stateTo, new Action<>(name, stateFrom, stateTo));
        }
        return this;
    }

    /**
     * Add an action from one state to another and with same name in reverse.
     *
     * @param name
     * @param s1
     * @param s2
     * @return
     */
    public StateBuilder<S, P> actionBidirectional(String name, S s1, S s2) {
        action(name, s1, s2);
        action(name, s2, s1);
        return this;
    }

    /**
     * Whether there is route (action) from one state to another state.
     *
     * @param stateFrom
     * @param stateTo
     * @return
     */
    public boolean hasRoute(S stateFrom, S stateTo) {
        if (actionMap.containsKey(stateFrom)) {
            return actionMap.get(stateFrom).containsKey(stateTo);
        }
        return false;
    }

    /**
     * With state to build transition processing.
     * after this method, call {@code in()} and {@code out()} to define processes for this state.
     *
     * @param state
     * @return
     */
    public StateBuilder<S, P> state(S state) {
        composeState = state;
        return this;
    }

    /**
     * Add {@link Process} which will be executed before entering a state.
     * Use {@code state()} method to specify a state before calling this method.
     *
     * @param process
     * @return
     */
    public StateBuilder<S, P> in(Process<P> process) {
        Mapping.SubMapping<P> subMapping = stateMapping.getSubMapping(composeState);
        List<Process<P>> processes = subMapping.getProcesses(StateDirection.IN);
        processes.add(process);
        return this;
    }

    /**
     * Add {@link Process} which will be executed after exiting a state.
     * Use {@code state()} method to specify a state before calling this method.
     *
     * @param process
     * @return
     */
    public StateBuilder<S, P> out(Process<P> process) {
        Mapping.SubMapping<P> subMapping = stateMapping.getSubMapping(composeState);
        List<Process<P>> processes = subMapping.getProcesses(StateDirection.OUT);
        processes.add(process);
        return this;
    }


}
