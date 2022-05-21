package com.github.swiftech.swstate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author swiftech
 */
public class StateBuilderTest {
    static final String s1 = "s1";
    static final String s2 = "s2";
    static final String s3 = "s3";
    static final String s4 = "s4";
    static final String s5 = "s5";

    @Test
    public void testNormal() {
        StateBuilder<String, String> stateBuilder = new StateBuilder<>();
        stateBuilder.action("action a", s1, s2);
        stateBuilder.action("action b", s1, s3);
        stateBuilder.action("action c", s3, s4);
        stateBuilder.action("action a", s2);
        // True
        Assertions.assertTrue(stateBuilder.hasRoute(s1, s2));
        Assertions.assertTrue(stateBuilder.hasRoute(s1, s3));
        Assertions.assertTrue(stateBuilder.hasRoute(s3, s4));
        Assertions.assertTrue(stateBuilder.hasRoute(s2, s2));
        // False
        Assertions.assertFalse(stateBuilder.hasRoute(s1, s1));
        Assertions.assertFalse(stateBuilder.hasRoute(s3, s2));
        Assertions.assertFalse(stateBuilder.hasRoute(s3, s5));
    }

    @Test
    public void testInitialState(){
        StateBuilder<String, String> stateBuilder = new StateBuilder<>();
        stateBuilder.initialize(s1);
        stateBuilder.initialize(s2);
    }

}
