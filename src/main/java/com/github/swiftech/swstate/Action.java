package com.github.swiftech.swstate;

import java.io.Serializable;

/**
 * Represents an action that triggers the state from one to another.
 *
 * @author swiftech
 */
public class Action<T extends Serializable> {

    private String name;
    private T stateFrom;
    private T stateTo;

    public Action(String name, T stateFrom, T stateTo) {
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

    public T getStateFrom() {
        return stateFrom;
    }

    public void setStateFrom(T stateFrom) {
        this.stateFrom = stateFrom;
    }

    public T getStateTo() {
        return stateTo;
    }

    public void setStateTo(T stateTo) {
        this.stateTo = stateTo;
    }
}
