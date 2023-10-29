package com.github.swiftech.swstate.trigger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Builder to build trigger for StateBuilder.
 *
 * @author swiftech
 * @since 2.0
 * @see com.github.swiftech.swstate.StateBuilder
 */
public class TriggerBuilder {

    private final List<Trigger> triggers = new ArrayList<>();

    public TriggerBuilder c(Character character) {
        triggers.add(new CharTrigger(character));
        return this;
    }

    public TriggerBuilder c(Character... characters) {
        triggers.addAll(Arrays.stream(characters).map(CharTrigger::new).toList());
        return this;
    }

    public TriggerBuilder s(String s) {
        triggers.add(new StringTrigger(s));
        return this;
    }

    public TriggerBuilder s(String... strs) {
        triggers.addAll(Arrays.stream(strs).map(StringTrigger::new).toList());
        return this;
    }

    public TriggerBuilder i(Integer i) {
        triggers.add(new IntTrigger(i));
        return this;
    }

    public TriggerBuilder i(Integer... is) {
        triggers.addAll(Arrays.stream(is).map(IntTrigger::new).toList());
        return this;
    }

    public TriggerBuilder l(Long l) {
        triggers.add(new LongTrigger(l));
        return this;
    }

    public TriggerBuilder l(Long... ls) {
        triggers.addAll(Arrays.stream(ls).map(LongTrigger::new).toList());
        return this;
    }

    public TriggerBuilder f(Float f) {
        triggers.add(new FloatTrigger(f));
        return this;
    }

    public TriggerBuilder f(Float... fs) {
        triggers.addAll(Arrays.stream(fs).map(FloatTrigger::new).toList());
        return this;
    }

    public TriggerBuilder d(Double d) {
        triggers.add(new DoubleTrigger(d));
        return this;
    }

    public TriggerBuilder d(Double... ds) {
        triggers.addAll(Arrays.stream(ds).map(DoubleTrigger::new).toList());
        return this;
    }

    public TriggerBuilder custom(Trigger trigger) {
        triggers.add(trigger);
        return this;
    }

    public Trigger[] build() {
        return triggers.toArray(new Trigger[]{});
    }
}
