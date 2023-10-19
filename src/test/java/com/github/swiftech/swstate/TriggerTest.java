package com.github.swiftech.swstate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.github.swiftech.swstate.TestConstants.*;

/**
 * @author switech
 * @since 2.0
 */
public class TriggerTest extends BaseStateTest {

    @Test
    public void basic() {
        StateMachine<String, String> sm = new StateMachine<>(super.createTestStateBuilder());
        sm.start();
        Assertions.assertEquals(s1, sm.getCurrentState());
        sm.accept('a');
        Assertions.assertEquals(s2, sm.getCurrentState());
        sm.accept('b');
        Assertions.assertEquals(s1, sm.getCurrentState());
        sm.accept('A');
        Assertions.assertEquals(s2, sm.getCurrentState());
        sm.accept('c');
        Assertions.assertEquals(s3, sm.getCurrentState());
        sm.acceptWithPayload('x', "payload");
        Assertions.assertEquals(s4, sm.getCurrentState());
    }

    @Test
    public void multiple() {
        StateMachine<String, String> sm = new StateMachine<>(super.createTestStateBuilder());
        sm.start();
        Assertions.assertEquals(s1, sm.getCurrentState());
        sm.accept("100");
        Assertions.assertEquals(s1, sm.getCurrentState());
        sm.accept("101");
        Assertions.assertEquals(s2, sm.getCurrentState());
    }
}
