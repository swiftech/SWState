package com.github.swiftech.swstate.trigger;

/**
 * @author swiftech
 * @since 2.0
 */
public class StringTrigger implements Trigger {

    private final String text;

    public StringTrigger(String text) {
        this.text = text;
    }

    @Override
    public boolean accept(Object data, Object payload) {
        return text.equals(data);
    }
}
