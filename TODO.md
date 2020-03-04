# TODO

This document is essentially a list of shorthand notes describing work yet to be completed.
Unfortunately it is not complete enough for other people to pick work off the list and
complete as there is too much un-said.

## Beta-1 Release

* Add some basic documentation
  * Usage documentation maybe take inspiration from https://dagger.dev/users-guide.
  * Recipe style examples for how to solve specific problems.
  * Maybe terminology should be (more) inspired by OSGI service ala https://www.osgi.org/developer/architecture/
    * Figure out terminology. Currently it is a mixed bag derived from various injector frameworks that it has
      been inspired from. Terms that are misused and should be cleaned up. This would involved cleaning up lots
      of code, tests and javadocs so should be done sooner rather than later.
      * `Service` = the instances present in the object?
      * `Type` = the java type that a service consumes or publishes.
      * `Qualifier` = an arbitrary user-supplied string that is used to distinguish between services
        that have the same `Type` but different semantics.
      * `Coordinate` = the combination of `Type`+`Qualifier` used to address a service
      * `Binding` = a mechanism for creating a value that can contribute to the object graph.
      * `Dependency` = a declaration by a binding that indicates the services that it consumes.
      * `Node` = the inclusion of a `Binding` in an object graph
      * `Edge` = a list of nodes that provide a service to a `Node` to satisfy a `Dependency`

## Beta-2 Release

* We may need to add a separate phase at the end of compilation that detects when singular injection requests
  result in multiple candidate bindings. Note that some of these bindings can be added during resolution process.

* Generate error if `@Named` present but zero types are published.
