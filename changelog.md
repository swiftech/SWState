# Changelogs

## v2.2
* add more conditional `post*` methods to `StateMachine` to transit states conditionally on current state.
* add `setNoInProcessForSelfCirculation()` and `setNoOutProcessForSelfCirculation()` to set whether the IN/OUT processes will be executed for self-circulation state transition.
* update dependencies.

## v2.1
* add `setSilent()` to set whether to throw an exception when an internal exception occurs.
* add `setExceptionHandler()` to set an exception callback.

## v2.0.1
* add trigger to automatic transit between states.
* update java version to 17 and update other dependencies.

## v1.1
* some improvement and bug fixing.
* update docs.

## v1.0
* initial release.