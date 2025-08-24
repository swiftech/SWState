package com.github.swiftech.swstate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Executes processes when state in or out.
 * all user data will be passed by payload of a process.
 * Call {@code setSilent()} to set whether to throw an exception when an internal exception occurs.
 * If you want to be notified when an exception occurs despite setting silence, just call {@code setExceptionHandler()} to set an exception callback.
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

    private ExceptionHandler exceptionHandler;

    // silent if exception in state processes happens.
    // if false, a {@link StateException} throws.
    private boolean isSilent = true;

    // @since 2.2, if true, the IN process will not be executed for self-circulation state transition.
    private boolean isNoInProcessForSelfCirculation = false;

    // @since 2.2, if true, the OUT process will not be executed for self-circulation state transition.
    private boolean isNoOutProcessForSelfCirculation = false;

    /**
     * Construct state transition with state builder.
     *
     * @param stateBuilder
     */
    public StateTransition(StateBuilder<S, P> stateBuilder) {
        this.actionMap = stateBuilder.actionMap;
        this.stateMapping = stateBuilder.stateMapping;
    }

    /**
     * Start the state transition with initial state.
     *
     * @return the initial state
     */
    public S start() {
        return this.start(null);
    }

    /**
     * Start the state transition with initial state and payload.
     *
     * @param payload
     * @return the initial state
     */
    public S start(P payload) {
        Map<S, Action<S>> actionMap = this.actionMap.get(null);
        if (actionMap == null || actionMap.isEmpty()) {
            throw new StateException("StateTransition is not properly built, no initial actions.");
        }
        if (actionMap.size() > 1) {
            throw new StateException("More than one initial state, use startState() instead.");
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
        if (log.isDebugEnabled()) log.debug(String.format("Start state at '%s'", state));
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
        if (log.isDebugEnabled()) log.debug(String.format("Start state at '%s' with payload", state));
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
        if (log.isDebugEnabled()) log.debug(String.format("Try to change state from '%s' to '%s'", from, to));
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
        Map<S, Action<S>> toMap = this.actionMap.get(from);
        if (toMap == null)
            throw new StateException("No state transitions from '%s' have been defined.".formatted(from));
        Action<S> action = toMap.get(to);
        if (log.isDebugEnabled())
            log.debug(String.format("%s: '%s'(%s) -> '%s'", action == null ? "null" : action.getName(),
                    from, Utils.payloadSummary(payload), to));
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

        if (isNoOutProcessForSelfCirculation && from == to) {
            log.info("Ignore executing OUT process from state %s to state %s".formatted(from, to));
        }
        else {
            // Handle OUT 'from' state
            if (from != null) {
                List<Process<P>> outProcesses = stateMapping.getOut(from);
                if (outProcesses == null || outProcesses.isEmpty()) {
                    if (log.isTraceEnabled())
                        log.trace(String.format("No actions to execute for exiting state '%s'", from));
                }
                else {
                    if (log.isDebugEnabled())
                        log.debug(String.format("Execute %d actions for exiting state '%s' ", outProcesses.size(), from));
                    execProcesses(outProcesses, payload);
                }
            }
        }

        if (isNoInProcessForSelfCirculation && from == to) {
            log.info("Ignore executing IN process from state %s to state %s".formatted(from, to));
        }
        else {
            // Handle IN 'to' state
            List<Process<P>> inProcesses = stateMapping.getIn(to);
            if (inProcesses == null || inProcesses.isEmpty()) {
                if (log.isTraceEnabled()) log.trace(String.format("No actions to execute for entering state '%s'", to));
            }
            else {
                if (log.isDebugEnabled())
                    log.debug(String.format("Execute %d actions for entering state '%s' ", inProcesses.size(), to));
                execProcesses(inProcesses, payload);
            }
        }

    }

    private void execProcesses(List<Process<P>> processes, P payload) {
        // All mapped processes for one state
        for (Process<P> process : processes) {
            // processes execution, if exception caught, will break the execution processing.
            try {
                process.execute(payload);
            } catch (Exception e) {
                log.error("Failed to execute process", e);
                if (exceptionHandler != null) {
                    exceptionHandler.onException(new StateException("Failed to execute process", e));
                }
                if (isSilent) {
                    break; // Prevent all other processes to be executed
                }
                else {
                    throw new StateException("Failed to execute process", e);
                }
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

    /**
     * Handler to be notified when an internal exception occurs.
     *
     * @param exceptionHandler
     */
    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    /**
     * Set whether throws exception when an internal exception occurs.
     *
     * @param silent
     */
    public void setSilent(boolean silent) {
        isSilent = silent;
    }

    /**
     * if true, the IN process will not be executed for self-circulation state transition.
     *
     * @param noInProcessForSelfCirculation
     * @since 2.2
     */
    public void setNoInProcessForSelfCirculation(boolean noInProcessForSelfCirculation) {
        isNoInProcessForSelfCirculation = noInProcessForSelfCirculation;
    }

    /**
     * if true, the OUT process will not be executed for self-circulation state transition.
     *
     * @param noOutProcessForSelfCirculation
     * @since 2.2
     */
    public void setNoOutProcessForSelfCirculation(boolean noOutProcessForSelfCirculation) {
        isNoOutProcessForSelfCirculation = noOutProcessForSelfCirculation;
    }
}
