package com.github.swiftech.swstate.trigger;

/**
 * @author swiftech
 * @since 2.0
 */
public class FloatTrigger implements Trigger {

    private final Float aFloat;

    public FloatTrigger(Float aFloat) {
        this.aFloat = aFloat;
    }

    @Override
    public boolean accept(Object data, Object payload) {
        return aFloat.equals(data);
    }
}
