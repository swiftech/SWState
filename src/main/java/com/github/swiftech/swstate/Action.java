package com.github.swiftech.swstate;

import java.io.Serializable;

/**
 * Defines an action that trigger the state from one to another.
 *
 * @param <S> type of State
 * @author swiftech
 */
public class Action<S extends Serializable> {

    /**
     * Name of the action.
     */
    private String name;

    /**
     * The state before the action happens.
     */
    private S stateFrom;

    /**
     * The state after the action happens.
     */
    private S stateTo;

    public Action(String name, S stateFrom, S stateTo) {
        this.name = name;
        this.stateFrom = stateFrom;
        this.stateTo = stateTo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public S getStateFrom() {
        return stateFrom;
    }

    public void setStateFrom(S stateFrom) {
        this.stateFrom = stateFrom;
    }

    public S getStateTo() {
        return stateTo;
    }

    public void setStateTo(S stateTo) {
        this.stateTo = stateTo;
    }
}
