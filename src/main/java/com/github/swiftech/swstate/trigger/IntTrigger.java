package com.github.swiftech.swstate.trigger;

/**
 * @author swiftech
 * @since 2.0
 */
public class IntTrigger implements Trigger {

    private final Integer integer;

    public IntTrigger(Integer integer) {
        this.integer = integer;
    }

    @Override
    public boolean accept(Object data, Object payload) {
        return integer.equals(data);
    }
}
