package com.github.swiftech.swstate;

import com.github.swiftech.swstate.trigger.Trigger;

import static com.github.swiftech.swstate.TestConstants.*;

/**
 * @author swiftech
 */
public class BaseStateTest {

    // for testing custom trigger
    protected static final Trigger customTrigger = (data, payload) -> {
        int i = Integer.parseInt(String.valueOf(data));
        return i > 100;
    };

    // for output payload
    protected static final Trigger payloadTrigger = (data, payload) -> {
        if (payload != null) System.out.println(payload);
        return true;
    };

    /**
     * create a builder for testing which takes issue state processing.
     *
     * @return StateBuilder for testing.
     */
    protected StateBuilder<String, String> createTestStateBuilder() {
        StateBuilder<String, String> stateBuilder = new StateBuilder<>();
        stateBuilder.initialize("Create Issue", s1)
                .action("Fix Issue", s1, s2, stateBuilder.triggerBuilder().c('a', 'A').i(1).custom(customTrigger).build())
                .action("Reopen Issue", s2, s1, stateBuilder.triggerBuilder().c('b', 'B').i(2).build())
                .action("Test Pass", s2, s3, stateBuilder.triggerBuilder().c('c', 'C').f(3.0f).build())
                .action("Reopen Issue", s3, s1, stateBuilder.triggerBuilder().c('d', 'D').d(4.0).build())
                .action("Close Issue", s3, s4, payloadTrigger)
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
                })
                .state(s4)
                .in((String payload) -> {
                    System.out.printf("STATE [%s] IN: %s%n", s4, payload);
                });
        return stateBuilder;
    }

    protected void waitUntil(int second) {
        try {
            Thread.sleep(second * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
