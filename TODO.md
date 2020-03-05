# TODO

This document is essentially a list of shorthand notes describing work yet to be completed.
Unfortunately it is not complete enough for other people to pick work off the list and
complete as there is too much un-said.

## Beta-1 Release

* Add some basic documentation
  * Usage documentation maybe take inspiration from https://dagger.dev/users-guide.
  * Recipe style examples for how to solve specific problems.
    * Changing the types published by a binding.
    * Pass in services to injector.
    * Publish an injector as a component.
    * Using StingProvider to integrate with other frameworks.
  * Terminology:
      * `Component` = the values managed by the injector.
      * `Binding` = a mechanism for creating components that can contribute to the component graph.
      * `Service` = a java type combined with a `Qualifier` that describes a value that a component either consumes or publishes.
      * `Qualifier` = an arbitrary user-supplied string that is used to distinguish between services
        that have the same `Type` but different semantics.

## Beta-2 Release

* We may need to add a separate phase at the end of compilation that detects when singular injection requests
  result in multiple candidate bindings. Note that some of these bindings can be added during resolution process.
