package com.github.swiftech.swstate.trigger;

/**
 * @author swiftech
 * @since 2.0
 */
public class CharTrigger implements Trigger {

    private final Character character;

    public CharTrigger(Character character) {
        this.character = character;
    }

    @Override
    public boolean accept(Object data, Object payload) {
        return character == data;
    }
}
