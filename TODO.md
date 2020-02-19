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

* Write integration tests that compare with dagger the following performance characteristics.
  * time to compile the injector
  * time to incrementally compile the injector and no contributors
  * time to initialize and access the injector at runtime
  * Code size of the the injector when compiled to GWT

  The integration tests will generate an injector with various numbers of fragments, injectables with
  a dependency tree that has various shapes (i.e. width and depth of tree varied). The integration tests
  will be in a separate module and will use javapoet to generate the code, existing test infrastructure
  to compile the code repeatedly until we get stable builds.

* Add some basic documentation
  * Usage documentation.
  * Recipe style examples for how to solve specific problems.
  * Comparison to other technologies (i.e. Dagger/IOC) in terms of functionality and performance.
  * Development process FAQ - just like in Arez but reflective of Stings approach.
  * Maybe terminology should be (more) inspired by OSGI service ala https://www.osgi.org/developer/architecture/

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

* Generate a website where you can view the graph similar to [dagger-browser](https://github.com/Snapchat/dagger-browser). This could be built at build time or potentially at runtime via spy infrastructure.

## Old Notes

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

----

The following notes were extracted from Arez's TODO.md and provided the initial motivation for this project.

## Better Injection Framework

Dagger2 is not a great injection framework for our context. Some annoyances that have arisen after usage:

* The code size is sub-optimal and even simple changes can significantly decrease code size. See
  `org.realityforge.dagger:dagger-gwt-lite:jar` for some simple optimizations although there is a lot more
  possible.
* The code for the compiler is spread across multiple jars and can collide with other annotation processors.
  The annotation processor should have dependencies shaded and placed in a single jar.
* Compile warnings as the code generated uses "unchecked or unsafe" operations and does not suppress them.
* Scopes do not really make sense in the context of the web application. It is unclear what the web context
  cares about. Maybe `@Singleton`, (Component) `TreeLocal` and per code-split. This may be hierarchical scopes
  for statically determinable scopes and some other construct for dynamic `TreeLocal` dependencies or maybe these
  are pushed to the web-application framework ala react4j and can only appear there.
* Applications have been refactored to use constructor based injection rather than field or method based injection.
  We could probably enforce this in Arez and thus eliminate a lot of overhead in our generator. If only we could
  lock this down at dagger level it would be better.
* There has been zero use of qualifier annotations in the downstream projects. So qualifiers could potentially be
  eliminated from dagger with no ill effect.
* We also add lots of dagger modules to replicate the behaviour of `@javax.enterprise.inject.Typed`. This could be
  baked into a the DI.
* `@dagger.Provides` is used but only in smallest degree.
* It is unclear how easy it is or even if it is possible to have per-instance dispose invocations for components
  when their scopes are closed.
* Code-splitting is complex ... if at all possible.
* Dagger includes a lot more complex support code for Android and friends which seems less useful for web.
* Dagger often does not detect errors at annotation processing time (particularly wrt visibility of code)
  and instead leaves the compiler responsible for failing to compile incorrectly generated code.
* Arez needs 6 different code paths to handle all the different ways in which we need to generate dagger support
  code depending on the features we use and this is error prone and complex. These code paths are based around
  the following features:
  - Is the component solely a consumer of dependencies or can it be provided to other components.
    (a.k.a. Should the Arez component be placed in main Dagger component or a Dagger sub-component)
  - Does the component have schedule-able elements or postConstruct lifecycle steps that requires that
    the component is injected correctly before the constructor complete or not.
  - Does the component need to be provided parameters at the time of creation of the component or not.
* Building the Dagger components is extremely complex. There are many different ways in which the dagger artifacts
  need to be combined to form a component (i.e. added as a module or not, extending the component or not,
  explicitly calling bind helper methods or not).
* Need to incorporate auto-factory functionality (i.e. [AssistedInject](https://github.com/square/AssistedInject))
  that has been duplicated through numerous downstream consumers.
* There is no way to indicate that certain components are not lazy/`@Eager` and should be created when the Dagger
  component is created. Our applications end up adding accessors on the component just so that we can call them
  and create them after the component is instantiated.
* Not only should objects be eagerly instantiated we should have the ability to run arbitrary code snippets
  after eager components are created. (i.e. binding code in react4j codebase).
* It should be a code warning if `@Inject` occurs on a protected or public constructor
* It should be easy to override module for test scenarios and override specific parts of module locally.
  We are often forced to use dagger for "production" cases and guice for unit testing.

In the future we may have the cycles to address these issues. However a solution seems to be to either replace
dagger with a better injection framework or build tooling on top of dagger that hides it's complexities.

### An Ideal Injection framework

To integrate with dagger we store data in a bunch of static fields. It may be better to store that data in the
`ArezContext` somehow. In an ideal world we would also be able to inspect the static injections into components
via the spy API.

Angular also has an interesting injection framework. The services themselves declared that they are `@Injectable`
and explicitly declare the module that they are provided to. i.e. `@Injectable({ providedIn: 'root' })`. There
is also several other interesting ideas that are particularly relevant for code-split web apps.
See https://angular.io/guide/dependency-injection

Another interesting project underway is [crysknife](https://github.com/treblereel/crysknife).
