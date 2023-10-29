package com.github.swiftech.swstate.trigger;

/**
 * @author swiftech
 * @since 2.0
 */
public class DoubleTrigger implements Trigger {

    private final Double aDouble;

    public DoubleTrigger(Double aDouble) {
        this.aDouble = aDouble;
    }

    @Override
    public boolean accept(Object data, Object payload) {
        return aDouble.equals(data);
    }
}
