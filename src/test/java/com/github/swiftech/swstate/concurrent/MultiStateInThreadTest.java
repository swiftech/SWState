package com.github.swiftech.swstate.concurrent;

import com.github.swiftech.swstate.BaseStateTest;
import com.github.swiftech.swstate.DefaultStateProvider;
import com.github.swiftech.swstate.StateMachine;
import com.github.swiftech.swstate.TestConstants.StateIterator;
import org.junit.jupiter.api.Test;

import static com.github.swiftech.swstate.TestConstants.s1;

/**
 * TODO
 *
 * @author swiftech
 */
public class MultiStateInThreadTest extends BaseStateTest {

    private final Thread[] threads = new Thread[10];

    public MultiStateInThreadTest() {
        StateMachine<String, String> stateMachine = new StateMachine<>(this.createTestStateBuilder());
        DefaultStateProvider<String> provider = new DefaultStateProvider<>();
        stateMachine.setStateProvider(provider);
        for (int i = 0; i < threads.length; i++) {
            String targetName = "issue-" + i;
            stateMachine.startState(targetName, s1);
            Thread thread = new Thread(() -> {
                StateIterator si = new StateIterator(100);
                while (si.hasNext()) {
                    stateMachine.post(targetName, si.next());
                }
            });
            threads[i] = thread;
        }
    }

    /**
     * Test multi states, each state run in it's own thread.
     */
    @Test
    public void testMultiStateInThread() {

        super.waitUntil(10);
    }
}
