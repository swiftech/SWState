package com.github.swiftech.swstate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

/**
 * @author swiftech
 */
class StateTransitionTest {

    @Test
    public void testException() {
        StateBuilder<String, String> stateBuilder = new StateBuilder<String, String>()
                .initialize("A")
                .state("A")
                .in(payload -> {
                    throw new RuntimeException("force exception");
                })
                .state("B")
                .in(payload -> {
                    throw new RuntimeException("force exception");
                })
                .action("a-b", "A", "B")
                .action("b-a", "B", "A");
        StateTransition<String, String> stateTransition = new StateTransition<>(stateBuilder);

        // test silent when exception happens.
        stateTransition.setSilent(false);
        Assertions.assertThrows(RuntimeException.class, () -> {stateTransition.post("A", "B", "hello");});
        stateTransition.setSilent(true);
        Assertions.assertDoesNotThrow(() -> stateTransition.post("B", "A", "hello"));

        // test exception handler
        stateTransition.setExceptionHandler(stateException -> {
            System.out.println("exception: " + stateException.getMessage());
            Assertions.assertNotNull(stateException);
        });
        stateTransition.setSilent(true);
        stateTransition.post("B", "A", "hello");
    }

    @Test
    public void testSimple() {
        StateBuilder<String, String> stateBuilder = new StateBuilder<>();
        stateBuilder.initialize("first")
                .action("f-s", "first", "second");
        stateBuilder.state("first").in(payload -> {
            System.out.println("First");
        }).state("second").in(payload -> {
            System.out.println("Second");
        });
        //
        Assertions.assertTrue(stateBuilder.hasRoute(null, "first"));
        StateTransition<String, String> transition = new StateTransition<>(stateBuilder);
        transition.start();
        Assertions.assertTrue(stateBuilder.hasRoute("first", "second"));
        transition.post("first", "second");
        Assertions.assertThrows(Exception.class, () -> transition.post("second", "first"));
    }

    @Test
    public void testSelf() {
        StateBuilder<String, String> stateBuilder = new StateBuilder<>();
        stateBuilder.state("first").in(payload -> {
            System.out.println("first");
        });
        stateBuilder.initialize("first")
                .action("self", "first");
        StateTransition<String, String> transition = new StateTransition<>(stateBuilder);
        transition.post("first", "first");
    }

    @Test
    public void testComplex() {
        String s1 = TestConstants.s1;
        String s2 = TestConstants.s2;
        String s3 = TestConstants.s3;
        StateBuilder<String, String> stateBuilder = new StateBuilder<>();
        stateBuilder.initialize("Create Order", s1)
                .action("Pay Success", s1, s2)
                .action("Pay Fail", s1, s1)
                .action("Deliver", s2, s3)
                .state(s1)
                .in((String payload) -> {
                    System.out.printf("STATE [%s] IN: %s%n", s1, payload);
                })
                .out((String payload) -> {
                    System.out.printf("STATE [%s] OUT: %s%n", s1, payload);
                })
                .state(s2)
                .in((String payload) -> {
                    System.out.printf("STATE [%s] IN: %s%n", s2, payload);
                })
                .state(s3)
                .in((String payload) -> {
                    System.out.printf("STATE [%s] IN: %s%n", s3, payload);
                });

        StateTransition<String, String> transition = new StateTransition<>(stateBuilder);
        Assertions.assertThrows(RuntimeException.class, () -> {
            transition.startState(s2, "try to start");
        },"Start at non-initial state");

        Assertions.assertThrows(RuntimeException.class, () -> {
            transition.startState(s3, "try start");
        },"Start at non-initial state");

        transition.start("payload from order creation");
        transition.post(null, s1, "");
        transition.post(s1, s1, "");
        transition.post(s1, s2, "payload for payment");
        transition.post(s2, s3, "payload for delivery");

        // Check fails
        Assertions.assertFalse(transition.hasRoute(s3, s2));
        Assertions.assertThrows(RuntimeException.class, () -> {
            transition.post(s3, s2, "no route");

        });
        Assertions.assertThrows(RuntimeException.class, () -> {
            transition.post(null, s2, "no route");
        });

    }

}