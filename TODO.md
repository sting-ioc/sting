# TODO

This document is essentially a list of shorthand notes describing work yet to be completed.
Unfortunately it is not complete enough for other people to pick work off the list and
complete as there is too much un-said.

## Beta Release TODO Items

* We may need to add a separate phase at the end of compilation that detects when singular injection requests
  result in multiple candidate bindings. Note that some of these bindings can be added during resolution process.

* Integrate `@StingProvider` into auto-discovery process. We may need to add a flag to `@StingProvider` that
  controls whether that provider is subject to auto-discovery ... if there is a valid use case. In this scenario
  we will need to verify that the provider publishes a binding that exposes the originating type as a published
  service.

* Add some basic documentation
  * Usage documentation.
  * Recipe style examples for how to solve specific problems.
  * Comparison to other technologies (i.e. Dagger/IOC) in terms of functionality and performance.
  * Development process FAQ - just like in Arez but reflective of Stings approach.
  * Maybe terminology should be (more) inspired by OSGI service ala https://www.osgi.org/developer/architecture/

### Performance Documentation and Tests

* Compare "time to initialize and access the injector at runtime" between dagger and sting in jre mode and/or js mode.

* Compare "Code size of the the injector when compiled to GWT" between dagger and sting.

* regenerate performance data for compile times

* Add trial times to the statistics in performance tests. So we add something like
    > 0.5.medium.output.sting.all.trial.1=123
    > 0.5.medium.output.sting.all.trial.2=222
    > 0.5.medium.output.sting.all.trial.3=222

* Add standard deviation to calculated values for each scenario so we can add to table something like `± 0.3`

* Patch the tables in documentation with performance test data. Do this as part of the build. Patch version
  in documentation

* Fail release if there is no performance data for current version UNLESS the user specifies a environment
  variable override.


## Other TODO

* Add support for different optional services in code generator. We should support the following patterns:
  - `@Nullable T`
  - `Optional<T>`
  - `Supplier<Optional<T>`
  - `Collection<Supplier<Optional<T>`

  and we should also support nullability in `Collection<T>` by just not adding service to collection.

* Add the mechanisms for overriding bindings already added to object graph. Perhaps by adding an
  `override=ENABLE|DISABLE|AUTODETECT` parameter which indicates whether the binding can override
  existing bindings. Order in `includes` matters in this scenario. This will default to `AUTODETECT`
  which will evaluate to `DISABLE` all scenarios except when the binding is from a descriptor declared
  as nested class of the injector. Overrides can either be by id or published types.

* Add support to dependencyType so that the boxed types and primitives interoperate. ie. Can have a collection of `Integer` that derived from `int` values.

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

* Generate a [.dot](https://en.wikipedia.org/wiki/DOT_(graph_description_language) formatted version of
  object graph so that it can be fed into graphviz. This could be generated at build time or potentially
  at runtime via spy infrastructure.

* Generate a website where you can view the graph similar to [dagger-browser](https://github.com/Snapchat/dagger-browser).
  This could be built at build time or potentially at runtime via spy infrastructure.

* Should we make it possible to close/dispose injectors? We could support bindings declaring how they are
  closed/disposed/release. Something like `@DisposedBy(Closeable.class)`, `@DisposedBy(value = arez.Disposable.class, method="dispose")`
  and maybe allow the `@Injector` to declare default dispose mechanism ala `@DefaultDisposedBy(value = arez.Disposable.class, method="dispose")`.
  Alternatively we could invoke methods on a `@Fragment` type to dispose an instance. The injector would only generate
  the dispose operation if explicitly requested.

* We could add a "gathering" annotation processor that collected all the bindings into an injector. Each `@Injectable`
  and `@Fragment` etc could have an annotation like `@ContributeTo('SomeKey')` and we would have a type annotated with
  `@CollectContributors('SomeKey')` that is converted into a `@Fragment` type that could be included. Emitting the
  generated `@Fragment` type before processing completes may be difficult in normal adhoc builds but in more controlled
  build environments (i.e. Bazel), this would work well.

## Differences from Dagger

The significant differences from Dagger:

* The sting compiler is small, self-contained and vendors it's dependencies so it is easy to integrate and
  does not collide with other annotation processors.
* No scopes supported, a single instance of each binding is in the `Injector`.
* `Eager` and `Lazy` beans are supported with `Lazy` being the default.
* `Typed` is used to shape the possible edges in graph.
* Constructor injection the only form supported. No field or method injection.
* Qualifiers are represented as a single string which is empty.
* `@Nullable` provider will provide for `@Nullable` dependency but a null will also be provided to dependency if it
  is not declared in graph.
* No parameterized types can be part of the object graph except specific framework types (i.e. `Supplier` and
  `Collection`)
* No Array types can be part of the object graph.
* Injectors may specify dependencies that accepts values from outside the injector but are added to the ObjectGraph
* Can very easily extend an existing component and replace some beans in object with those used in testing.
* Consuming and providing package access components is supported by generating glue in the package.
* Generated code suppresses warnings and thus can be run with compiler linting enabled without generating any warnings. This is verified by the test suite.
* Problems with the object graph are detected before the code is generated rather than generating incorrect code
  and leaving it to javac to detect problems and for the user to work back from the javac error message to the
  problem in the code. This is particularly problematic with code visibility.
* Generates errors or (suppressable) warnings when problematic or confusing code constructs are present in the compiled code. i.e. `@Injectable` types should either not specify any constructor or should use package access constructors, annotations from other injection frameworks should not be intermingled with sting code etc.
* Dagger has a lot more complexity to support features not useful in a web context such as producers, android integration etc.
