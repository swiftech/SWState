package com.github.swiftech.swstate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Default state provider that stores states in memory.
 *
 * @author swiftech
 */
public class DefaultStateProvider<S extends Serializable> implements StateProvider<S> {

    /**
     * Map of current state
     */
    private final Map<String, S> stateMap = new HashMap<>();


    @Override
    public S getCurrentState(String id) {
        return stateMap.get(id);
    }

    @Override
    public void initializeState(String id, S state) {
        stateMap.put(id, state);
    }

    @Override
    public void setState(String id, S state) {
        stateMap.put(id, state);
    }

    @Override
    public boolean isState(String id, S state) {
        if (!stateMap.containsKey(id)) {
            return false;
        }
        S currentState = stateMap.get(id);
        return currentState != null
                && currentState.equals(state);
    }

    @Override
    public boolean isStateIn(String id, S... states) {
        for (S state : states) {
            if (isState(id, state)) {
                return true;
            }
        }
        return false;
    }
}
