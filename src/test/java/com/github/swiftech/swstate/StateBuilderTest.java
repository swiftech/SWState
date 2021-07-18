package com.github.swiftech.swstate;

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
        // True
        System.out.println(stateBuilder.hasRoute(s1, s2));
        System.out.println(stateBuilder.hasRoute(s1, s3));
        System.out.println(stateBuilder.hasRoute(s3, s4));
        // False
        System.out.println(stateBuilder.hasRoute(s3, s2));
        System.out.println(stateBuilder.hasRoute(s3, s5));
    }

    @Test
    public void testInitialState(){
        StateBuilder<String, String> stateBuilder = new StateBuilder<>();
        stateBuilder.initialize(s1);
        stateBuilder.initialize(s2);
    }

}
