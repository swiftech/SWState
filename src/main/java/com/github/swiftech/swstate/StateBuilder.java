package com.github.swiftech.swstate;

import com.github.swiftech.swstate.trigger.Trigger;
import com.github.swiftech.swstate.trigger.TriggerBuilder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.github.swiftech.swstate.Mapping.StateDirection;

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
    // set state from to null means initial state.
    final Map<S, Map<S, Action<S>>> actionMap = new HashMap<>();

    final Map<S, Map<Trigger, S>> triggerMap = new HashMap<>();

    // mapping for states and transition processing.
    final Mapping<S, P> stateMapping = new Mapping<>();

    private S composeState;

    /**
     * Start to build triggers for one action.
     *
     * @return
     * @since 2.0
     */
    public TriggerBuilder triggerBuilder() {
        return new TriggerBuilder();
    }

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
     * @param name         name of the action
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
     * @param name      name of the action
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
     * Add an action from one state to another.
     *
     * @param name      name of the action
     * @param stateFrom the state before the action happens.
     * @param stateTo   the state after the action happens.
     * @param triggers  triggers to automatically transit the state.
     * @return
     * @since 2.0
     */
    public StateBuilder<S, P> action(String name, S stateFrom, S stateTo, Trigger... triggers) {
        if (!hasRoute(stateFrom, stateTo)) {
            Map<S, Action<S>> toMap = actionMap.computeIfAbsent(stateFrom, k -> new HashMap<>());
            toMap.put(stateTo, new Action<>(name, stateFrom, stateTo));

            if (triggers != null && triggers.length > 0) {
                Map<Trigger, S> toByTriggerMap = triggerMap.computeIfAbsent(stateFrom, k -> new LinkedHashMap<>());
                for (Trigger trigger : triggers) {
                    toByTriggerMap.put(trigger, stateTo);
                }
            }
        }
        return this;
    }


    /**
     * Add an action that loop the state itself.
     *
     * @param name name of the action
     * @param state
     * @return
     */
    public StateBuilder<S, P> action(String name, S state) {
        return action(name, state, state);
    }

    /**
     *
     * @param name name of the action
     * @param state
     * @param triggers  triggers to automatically transit the state.
     * @return
     * @since 2.0
     */
    public StateBuilder<S, P> action(String name, S state, Trigger... triggers) {
        return action(name, state, state, triggers);
    }

    /**
     * Add an action of from one state to another and with the same name in reverse.
     *
     * @param name name of the action
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
     * Add an action of from one state to another and with the same name in reverse.
     * The 2 actions are sharing triggers, which means both of the states are affected by the given triggers.
     * If you don't want that, define the 2 actions separately with different triggers.
     *
     * @param name name of the action
     * @param s1
     * @param s2
     * @param triggers  triggers to automatically transit the state.
     * @return
     * @since 2.0
     */
    public StateBuilder<S, P> actionBidirectional(String name, S s1, S s2, Trigger... triggers) {
        action(name, s1, s2, triggers);
        action(name, s2, s1, triggers);
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

    public String getMetaInfo() {
        String template = """
                State Machine info:
                - %d states defined in total and %d state has process.
                %s""";
        int statesCount = actionMap.containsKey(null) ? actionMap.size() - 1 : actionMap.size();
        StringBuilder buf = new StringBuilder();
        Map<S, Integer> toCount = new HashMap<>();
        for (S from : actionMap.keySet()) {
            Map<S, Action<S>> toMap = actionMap.get(from);
            buf.append("- State ").append(from == null ? "default" : from.toString())
                    .append(" has ").append(toMap.size()).append(" outbounds.\n");
            // count 'to'
            for (S to : toMap.keySet()) {
                Integer count = toCount.getOrDefault(to, 0);
                toCount.put(to, ++count);
                // count using which only has inbounds
                if (!actionMap.containsKey(to)) {
                    statesCount++;
                }
            }

        }
        for (S to : toCount.keySet()) {
            buf.append("- State ").append(to)
                    .append(" has ").append(toCount.get(to)).append(" inbounds.\n");
        }

        return String.format(template, statesCount, stateMapping.getSize(), buf);
    }

}
