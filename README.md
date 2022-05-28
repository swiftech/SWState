# SWState

A really easy to use but powerful State Machine implementation based on Java 8 with zero dependencies.

## Usage

Assume that we have a turnstile with 2 states `Locked`, `Unlocked` and 2 actions `Coin`, `Push`, as the diagram shows:

![](docs/state_machine1.png)

The main class that you use SWState is `StateMachine`, before using that, you should build it by class `StateBuilder`.

### Build the `StateMachine`
  
defined states：

```java
final String STATE_LOCKED = "Locked";
final String STATE_UNLOCKED = "Unlocked";
```  

Use `StateBuilder` to define Actions and Processes:

```java
StateBuilder<String, Serializable> stateBuilder = new StateBuilder<>();
stateBuilder
        .state(STATE_LOCKED)
        .in(order -> {
            // Handle before the turnstile is locked.
            System.out.println("turnstile is locked");
        })
        .state(STATE_UNLOCKED)
        .in(order -> {
            // Handle before the turnstile is unlocked.
            System.out.println("turnstile is unlocked");
        })
        .initialize(STATE_LOCKED)
        .action("coin_locked", STATE_LOCKED, STATE_UNLOCKED)
        .action("push_unlocked", STATE_UNLOCKED, STATE_LOCKED)
        .action("coin_unlocked", STATE_UNLOCKED, STATE_UNLOCKED)
        .action("push_unlocked", STATE_LOCKED, STATE_LOCKED);
StateMachine<String, Serializable> stateMachine = new StateMachine<>(stateBuilder);
```

As you can see, we have set up 2 states and 4 actions to change states.
the method `in()` bind your actual processing code block


### Use `StateMachine` to transit states as per previous definitions. 

```java
String id = "turnstile0-1";
stateMachine.start(id);
...
stateMachine.post(id, STATE_UNLOCKED);
...
stateMachine.post(id, STATE_LOCKED);
```

> The parameter `id` of `start()` or `post()` identifies the object that using this state machine, which means different ids have their own state.


## Advanced

The `StateMachine` stores states in memory by default, if you want to store states into other storages like database or nosql,
there are 2 ways to get this done, implement a `StateProvide` or use `StateTransition` directly.

Example:
Assume that we have a simplified online shopping order processing with some order states, as the diagram shows:

![](docs/state_machine2.png)

defined states

```java
final String STATE_CREATED = "Created";
final String STATE_PAYED = "Payed";
final String STATE_CANCELED = "Canceled";
final String STATE_RECEIVED = "Received";
```

Set up Actions and Process with `StateBuilder`:

```java
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
```

### Method 1: Customized State Provider

To store states, you need to implement a `StateProvider`, SWState provides a `DefaultStateProvider` which stores states
in memory, but it is probably not suit your situation. Usually, the states you want to manage are in a column of
DB tables, so let's implement a database version `StateProvider`.

`MyDatabaseStateProvider.java`

```java
import com.github.swiftech.swstate.StateProvider;

public class MyDatabaseStateProvider implements StateProvider<String> {
  public MyDatabaseStateProvider() {
      // do some necessary init
  }
  ... // implement all methods for storing or retrieving state from database.
}
```

Replace the default state provider of state machine with yours:

```java
stateMachine.setStateProvider(new MyDatabaseStateProvider());
```

### Method 2: Use `StateTransition`

Instead of `StateMachine`, `StateTransition` is at lower level, it doesn't store current state but only process state transition.

First, construct instance of `StateTransition` just like `StateMachine` does.

```java
StateTransition<String, Order> stateTransition = new StateTransition<>(stateBuilder);
```

Second, use `stateTransition` to transit states which are loaded from other storage:

```java
public void pay(String id){
    String currentState = repository.getState(id); // repository is your own data access API
    stateTransition.post(currentState, STATE_PAYED); // if current state is not 'Created', it fails as per previous setting
}
```

### Maven

* Stable version

```xml
<dependency>
    <groupId>com.github.swiftech</groupId>
    <artifactId>swstate</artifactId>
    <version>1.1</version>
</dependency>
```

* Unstable version

```xml
<dependency>
    <groupId>com.github.swiftech</groupId>
    <artifactId>swstate</artifactId>
    <version>1.1</version>
</dependency>
```