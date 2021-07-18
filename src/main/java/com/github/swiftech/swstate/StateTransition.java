package com.github.swiftech.swstate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Executes processes when state in or out.
 * all user data will be passed by payload of process.
 *
 * @param <S> type of State
 * @param <P> type of Payload
 * @see StateBuilder
 */
public class StateTransition<S extends Serializable, P extends Serializable> {

    private final Logger log = LoggerFactory.getLogger(StateTransition.class);

    // state from -> state to -> action
    protected Map<S, Map<S, Action<S>>> actionMap;

    // mapping for states and actions.
    protected Mapping<S, P> stateMapping;

    /**
     * Build the state transition with {@link StateBuilder}
     *
     * @param stateBuilder
     */
    public void build(StateBuilder<S, P> stateBuilder) {
        this.actionMap = stateBuilder.actionMap;
        this.stateMapping = stateBuilder.stateMapping;
    }

    /**
     * Start the state transition with initial state.
     * @return the initial state
     */
    public S start() {
        return this.start(null);
    }

    /**
     * Start the state transition with initial state and payload.
     * @param payload
     * @return the initial state
     */
    public S start(P payload) {
        Map<S, Action<S>> actionMap = this.actionMap.get(null);
        if (actionMap == null || actionMap.isEmpty()) {
            throw new RuntimeException("StateTransition is not properly built, no initial actions.");
        }
        if (actionMap.size() > 1) {
            throw new RuntimeException("More than one initial state, use startState() instead.");
        }
        Optional<Action<S>> optAction = actionMap.values().stream().findFirst();
        S stateTo = optAction.get().getStateTo();
        optAction.ifPresent(sAction -> this.doPost(null, stateTo, payload));
        return stateTo;
    }

    /**
     * Start the state transition with specified initial state.
     *
     * @param state
     * @return
     */
    public void startState(final S state) {
        log.debug(String.format("Start state at '%s'", state));
        this.doPost(null, state, null);
    }

    /**
     * Start the state transition with specified initial state and payload.
     *
     * @param state
     * @param payload
     * @return
     */
    public void startState(final S state, P payload) {
        log.debug(String.format("Start state at '%s' with payload", state));
        this.doPost(null, state, payload);
    }

    /**
     * Post from one specified state to new state with.
     *
     * @param from
     * @param to
     * @return
     */
    public void post(final S from, final S to) {
        log.debug(String.format("Try to change state from '%s' to '%s'", from, to));
        this.doPost(from, to, null);
    }

    /**
     * Post from one specified state to new state with payload.
     *
     * @param from
     * @param to
     * @param payload
     * @return
     */
    public void post(final S from, final S to, P payload) {
        log.debug(String.format("Try to change state from '%s' to '%s' with payload", from, to));
        this.doPost(from, to, payload);
    }

    /**
     * Post from one specified state to new state with payload.
     *
     * @param from
     * @param to
     * @param payload
     */
    private void doPost(S from, S to, P payload) {
        // If no any actions initialized before, you can't change state.
        if (!this.hasRoute(from, to)) {
            throw new RuntimeException(String.format("Changing state from '%s' to '%s' is not allowed.", from, to));
        }

        // Handle OUT 'from' state
        if (from != null) {
            List<Process<P>> outProcesses = stateMapping.getOut(from);
            if (outProcesses == null || outProcesses.isEmpty()) {
                log.trace(String.format("No actions to execute for exiting state '%s'", from));
            }
            else {
                log.debug(String.format("Execute %d actions for exiting state '%s' ", outProcesses.size(), from));
                exeActions(outProcesses, payload);
            }
        }

        // Handle IN 'to' state
        List<Process<P>> inProcesses = stateMapping.getIn(to);
        if (inProcesses == null || inProcesses.isEmpty()) {
            log.trace(String.format("No actions to execute for entering state '%s'", to));
        }
        else {
            log.debug(String.format("Execute %d actions for entering state '%s' ", inProcesses.size(), to));
            exeActions(inProcesses, payload);
        }
    }

    private void exeActions(List<Process<P>> processes, P payload) {
        // All mapped processes for one state
        for (Process<P> process : processes) {

            // processes execution, if exception caught, will break the execution process.
            try {
                process.execute(payload);
            } catch (Exception e) {
                e.printStackTrace();
                break; // Prevent all other processes to be executed
            }
        }
    }

    /**
     * Whether two states have route between them.
     *
     * @param stateFrom
     * @param stateTo
     * @return
     */
    public boolean hasRoute(S stateFrom, S stateTo) {
        if (actionMap == null) {
            return false;
        }
        if (actionMap.containsKey(stateFrom)) {
            return actionMap.get(stateFrom).containsKey(stateTo);
        }
        return false;
    }

}
