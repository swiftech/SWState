# SWState

A really simple but useful state machine implementation based on Java 8 with zero dependencies.

# Usage

### StateMachine

Assume that we have a turnstile with 2 states `Locked`, `Unlocked` and 2 actions `Coin`, `Push`, just like the diagram shows:

![](docs/state_machine1.png)

* Build the `StateMachine`
  
  Before using `StateMachine` you should set it up by define Actions and Process with `StateBuilder`:
  
```java
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
StateMachine<String, Serializable> stateMachine = new StateMachine<>();
stateMachine.build(stateBuilder);
```
As you can see, we have set up 2 states and 4 actions to change states.
the method `in()` bind your actual processing code block


* Use `StateMachine` to transit states as per previous definitions. 
```java
String id = "turnstile";
stateMachine.start(id);
...
stateMachine.post(id, "Unlocked");
...
stateMachine.post(id, "Locked");
```

> The parameter `id` of `start()` or `post()` identifies  

### More

The `StateMachine` stores stats in memory by default, if you want to store states into other storage like database or nosql,
there are 2 ways to get this done, implement a `StateProvide` or use `StateTransition` directly.

Example:
Assume that we have a simplified online shopping order processing with some order states, just like the diagram shows:

![](docs/state_machine2.png)

Set up Actions and Process with `StateBuilder`:

```java
StateBuilder<String, Serializable> stateBuilder = new StateBuilder<>();
stateBuilder
.state("Created")
.in(p -> {
// Handle the order is created .
})
.state("Payed")
.in(p -> {
// Handle the order is payed.
})
.state("Canceled")
.in(p -> {
// Handle the order is canceled
})
.state("Received")
.in(p->{
// Handle the delivery is received
})
.initialize("create order", "Created")
.action("pay order", "Created", "Payed")
.action("cancel order", "Created", "Canceled")
.action("deliver goods", "Payed", "Received");
```

* Method 1: Customized State Provider

To store states, you have to implement a `StateProvider`, SWState provides a `DefaultStateProvider` which stores states
in memory, but it is probably not fulfill your situation. Usually, the states you want to manage stores in a column of
DB tables, so let's do it as example:

`MyDatabaseStateProvider.java`

```java
import com.github.swiftech.swstate.StateProvider;

public class MyDatabaseStateProvider implements StateProvider<String> {
  public MyDatabaseStateProvider() {
      // do some init
  }
  ... // implement all methods for storing or retrieving state from database.
}
```

Replace the default state provider of state machine with yours:

```java
stateMachine.setStateProvider(new MyDatabaseStateProvider());
```

* Method 2: Use `StateTransition`

```java
StateTransition<String, Order> stateTransition = new StateTransition<>();
stateTransition.build(stateBuilder);
```

use `stateTransition` to transit states which are load from other storages:

```java
public void pay(String id){
    String currentState = repository.getState(id); // repository is your own data access API
    stateTransition.post(currentState, "Payed"); // if current state is not 'Created', it fails as per previous setting
}
```

# Maven

```xml
<dependency>
    <groupId>com.github.swiftech</groupId>
    <artifactId>swstate</artifactId>
    <version>1.0</version>
</dependency>
```