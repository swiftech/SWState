package com.github.swiftech.swstate;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.github.swiftech.swstate.TestConstants.*;

/**
 * @author swiftech
 */
public class StateMachineWithDefaultProviderTest extends BaseStateTest{

    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    @Test
    public void trigger() {
        // TODO
    }

    @Test
    public void inSingleThread() {
        StateMachine<String, String> stateMachine = new StateMachine<>(super.createTestStateBuilder());

        String sid = "issue-01";
        stateMachine.start(sid);
        Assertions.assertThrows(RuntimeException.class, () -> stateMachine.post(sid, s1));
        Assertions.assertThrows(RuntimeException.class, () -> stateMachine.post(sid, s3));
        stateMachine.post(sid, s2);
        stateMachine.post(sid, s3);
        stateMachine.post(sid, s4);
    }

    @Test
    public void oneStateInMultiThreads() throws ExecutionException, InterruptedException {
        StateMachine<String, String> stateMachine = new StateMachine<>(this.createTestStateBuilder());

        String sid = "target1";
        stateMachine.start(sid);

        List<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Future<?> future = executor.submit(() -> {
                String s = randomState();
                for (int j = 0; j < 100; j++) {
                    try {
                        Thread.sleep(RandomUtils.nextInt(0, 5));
                        stateMachine.post(sid, s);
                    } catch (Exception e) {
                        System.out.println(e.getLocalizedMessage());
                    }
                }
            }, "thread " + i);
            futures.add(future);
        }

        for (Future<?> future : futures) {
            Object result = future.get();
            System.out.println("one thread done with: " + result);
        }
    }

}
