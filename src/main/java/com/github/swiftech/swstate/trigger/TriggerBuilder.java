package com.github.swiftech.swstate.trigger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author swiftech
 * @since 2.0
 */
public class TriggerBuilder {

    private final List<Trigger> triggers = new ArrayList<>();

    public TriggerBuilder c(Character character) {
        triggers.add(new CharTrigger(character));
        return this;
    }

    public TriggerBuilder c(Character... characters) {
        for (Character character : characters) {
            triggers.add(new CharTrigger(character));
        }
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

    public TriggerBuilder custom(Trigger trigger) {
        triggers.add(trigger);
        return this;
    }

    public Trigger[] build() {
        return triggers.toArray(new Trigger[]{});
    }
}
