package com.github.swiftech.swstate.trigger;

/**
 * @author swiftech
 * @since 2.0
 */
public class ObjectTrigger implements Trigger {

    private final Object object;

    public ObjectTrigger(Object object) {
        this.object = object;
    }

    @Override
    public boolean accept(Object data, Object payload) {
        return object.equals(data);
    }
}
