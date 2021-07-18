package com.github.swiftech.swstate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mapping state and processes
 */
public class Mapping<S extends Serializable, P extends Serializable> {

    // state -> sub-mapping(direction -> processes)
    private Map<S, SubMapping<P>> stateMap = new HashMap<>();

    /**
     * Sub-mapping contains both IN and OUT processes for a state.
     *
     * @param state
     * @return
     */
    public SubMapping<P> getSubMapping(S state) {
        return stateMap.computeIfAbsent(state, k -> new SubMapping<>());
    }

    /**
     * Get IN processes from sub-mapping for state
     *
     * @param state
     * @return
     */
    public List<Process<P>> getIn(S state) {
        SubMapping<P> subMapping = stateMap.computeIfAbsent(state, k -> new SubMapping<>());
        return subMapping.getProcesses(StateDirection.IN);
    }

    /**
     * Get OUT processes from sub-mapping for state
     *
     * @param state
     * @return
     */
    public List<Process<P>> getOut(S state) {
        SubMapping<P> subMapping = stateMap.computeIfAbsent(state, k -> new SubMapping<>());
        return subMapping.getProcesses(StateDirection.OUT);
    }

    public Map<S, SubMapping<P>> getStateMap() {
        return stateMap;
    }

    public void setStateMap(Map<S, SubMapping<P>> stateMap) {
        this.stateMap = stateMap;
    }

    /**
     * Mapping state direction(IN or OUT) and processes
     */
    public static class SubMapping<P extends Serializable> {
        private final Map<StateDirection, List<Process<P>>> customizedActionMapping = new HashMap<>();

        public List<Process<P>> getProcesses(StateDirection statusDirection) {
            return customizedActionMapping.computeIfAbsent(statusDirection, k -> new ArrayList<>());
        }
    }

    /**
     * The direction of a state transition.
     */
    public enum StateDirection {
        IN, // Entering a state
        OUT // Exiting a state
    }
}
