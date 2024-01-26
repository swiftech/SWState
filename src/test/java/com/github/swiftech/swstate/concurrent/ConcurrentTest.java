package com.github.swiftech.swstate.concurrent;


import com.github.swiftech.swstate.StateBuilder;
import com.github.swiftech.swstate.StateTransition;
import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Test concurrent running of state transition.
 *
 * @author swiftech
 */
public class ConcurrentTest {
    public static final String S1 = "State1";
    public static final String S2 = "State2";
    public static final String S3 = "State3";
    public static final List<String> states = new ArrayList<>() {
        {
            add(S1);
            add(S2);
            add(S3);
        }
    };
    public static final int START_INCLUSIVE = 1000;
    public static final int END_EXCLUSIVE = 2000;

    private static final Object lock = new Object();

    public void testConcurrent() {
        StateBuilder<String, String> stateBuilder = new StateBuilder<>();
        stateBuilder.initialize(S1)
                .action("", S1, S2)
                .action("", S2, S3)
                .state(S1)
                .in((payload -> {
                    System.out.println(payload);
                    sleepNoException(RandomUtils.nextInt(START_INCLUSIVE, END_EXCLUSIVE));
                }))
                .state(S2)
                .in(payload -> {
                    System.out.println(payload);
                    sleepNoException(RandomUtils.nextInt(START_INCLUSIVE, END_EXCLUSIVE));
                })
                .state(S3)
                .in(payload -> {
                    System.out.println(payload);
                    sleepNoException(RandomUtils.nextInt(START_INCLUSIVE, END_EXCLUSIVE));
                });

        StateTransition<String, String> transition = new StateTransition<>(stateBuilder);
        for (int i = 0; i < 100; i++) {
            Thread t = new Thread(() -> {
                for (int j = 0; j < states.size(); j++) {
                    String from = j <= 0 ? null : states.get(j - 1);
                    String to = states.get(j);
                    transition.post(from, to, String.format("%s -> %s", from, to));
//					sleepNoException(RandomUtils.nextInt(START_INCLUSIVE, END_EXCLUSIVE));
                    sleepNoException(1000);
//					synchronized (lock){
//						lock.notify();
//					}
                }
            });
            t.start();
            sleepNoException(1000);
        }
//		try {
//			synchronized (lock){
//				lock.wait();
//			}
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
        sleepNoException(600 * 1000);

    }

    private void sleepNoException(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ConcurrentTest concurrentTest = new ConcurrentTest();
        concurrentTest.testConcurrent();
    }
}
