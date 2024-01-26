package com.github.swiftech.swstate;

import com.github.swiftech.swstate.db.DatabaseStateProvider;

import java.io.Serializable;

/**
 * Demo for tutorial document.
 *
 * @author swiftech
 */
public class Demo {

    public static void main(String[] args) {
        Demo demo = new Demo();
//        demo.forStateTransition();
        demo.forStateMachine();
    }

    public void forStateTransition() {
        String STATE_CREATED = "Created";
        String STATE_PAYED = "Payed";
        String STATE_CANCELED = "Canceled";
        String STATE_RECEIVED = "Received";
        StateBuilder<String, Order> stateBuilder = new StateBuilder<>();
        stateBuilder
                .state(STATE_CREATED)
                .in(order -> {
                    // Handle the order is created .
                })
                .state(STATE_PAYED)
                .in(order -> {
                    // Handle the order is payed.
                })
                .state(STATE_CANCELED)
                .in(order -> {
                    // Handle the order is canceled
                })
                .state(STATE_RECEIVED)
                .in(order -> {
                    // Handle the delivery
                })
                .initialize("create order", STATE_CREATED)
                .action("pay order", STATE_CREATED, STATE_PAYED)
                .action("cancel order", STATE_CREATED, STATE_CANCELED)
                .action("deliver goods", STATE_PAYED, STATE_RECEIVED);

        StateTransition<String, Order> stateTransition = new StateTransition<>(stateBuilder);
        stateTransition.post(STATE_CREATED, STATE_PAYED);
    }

    public void forStateMachine() {
        // setup
        StateBuilder<String, Serializable> stateBuilder = new StateBuilder<>();
        stateBuilder
                .state("Locked")
                .in(order -> {
                    // Handle before the turnstile is locked.
                    System.out.println("turnstile is locked");
                })
                .state("Unlocked")
                .in(order -> {
                    // Handle before the turnstile is unlocked.
                    System.out.println("turnstile is unlocked");
                })
                .initialize("Locked")
                .action("coin_locked", "Locked", "Unlocked")
                .action("push_unlocked", "Unlocked", "Locked")
                .action("coin_unlocked", "Unlocked", "Unlocked")
                .action("push_unlocked", "Locked", "Locked");
        StateMachine<String, Serializable> stateMachine = new StateMachine<>(stateBuilder);
        stateMachine.setStateProvider(new DatabaseStateProvider());

        // using
        String id = "turnstile-01";
        stateMachine.start(id);
        stateMachine.post(id, "Unlocked");
        stateMachine.post(id, "Unlocked");
        stateMachine.post(id, "Locked");
        stateMachine.post(id, "Locked");
    }


    public static class Order implements Serializable {
        // NOTHING
    }
}
