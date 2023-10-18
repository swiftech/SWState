package com.github.swiftech.swstate.trigger;

/**
 * @author swiftech
 * @since 2.0
 */
public class LongTrigger implements Trigger {

    private final Long l;

    public LongTrigger(Long l) {
        this.l = l;
    }

    @Override
    public boolean accept(Object data, Object payload) {
        return l.equals(data);
    }
}
