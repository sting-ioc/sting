# TODO

This document is essentially a list of shorthand notes describing work yet to completed.
Unfortunately it is not complete enough for other people to pick work off the list and
complete as there is too much un-said.

* Emit descriptors as binary data. Make emitting descriptors as json optional and only enabled by an explicit
  annotation parameter.
* On processing an `@Injector` annotated class we load the binary descriptors. If the descriptors are not present
  then we defer processing the `@Injector` annotated class until the descriptors are present or we generate an
  error when the processor reaches the last round.
* Add a separate annotation processor that provides [AssistedInject](https://github.com/square/AssistedInject)
  capabilities if it is desired. The reason for using a separate annotation processor is that it actually
  generates code used by users and thus has to be marked as a API-generating processor in Bazel which can reduce
  the degree of parallelization that is possible. Moving it to a separate annotation processor means we can
  explicitly control whether we accept this cost.
* Consider generating warnings if `@Fragment` types will not resolve unless the user supplies an explicit
  `incomplete` parameter. By resolve it means that every component should have all of it's dependencies
  present.
* Support collection dependency types. (So along with `INSTANCE` and `SUPPLIER` we should also support
  `COLLECTION_INSTANCE`, `COLLECTION_SUPPLIER`). Partial support present but not yeat parsed from code.
* Add a `@PostConstruct` hook so that custom code can be run after eager beans are constructed.
* Consider removing `@Factory` support from `includes` parameters of `@Fragments` and `@Injector`
  and just assume that they are detected if they are referenced from within the object graph. The
  `@Injectable` types are also detected but can be manually added if you want them eagerly instantiated and
  they are not a dependency of any other element.
* Generate an error if an `@Injector` declares no dependencies and no `eager` bindings.
* Add test suite for `Registry`
* Injectors that are inherited by another injector can disable eager services? For usage in tests.
* Add test for unresolved `@Injector`. Add a separate test for unresolved include and another for
  unresolved dependency.

## Old Notes

## Phase 1:

* Introduce the `Factory` annotation that allows the construction of components by supplying parameters.
  Abstract methods return the type that is produced. Any parameters must match the parameter types and/or
  names of parameters in components constructor. Components produced must have a single constructor. Any
  parameter supplied is assumed to be from the object graph.

## Differences from Dagger

The significant differences from Dagger:

* No scopes supported, a single instance of each binding is in the `Injector`.
* `Eager` and `Lazy` beans are supported with `Lazy` being the default.
* `Typed` is used to shape the possible edges in graph.
* Add the ability for modules to define a hook method that is invoked post-Object-graph construction.
  (i.e. to link into react runtime or to cache values in statics as in Rose)
* Constructor injection the only form supported. No field or method injection.
* Qualifiers are represented as a single string which is empty.
* `@Nullable` provider will provide for `@Nullable` dependency but a null will also be provided to dependency if it
  is not declared in graph.
* No parameterized types can be part of object graph except specific framework types (i.e. `Supplier` and
  `Collection` in the future)
* Components may have an optional factory that accepts any dependencies from outside the system but these are
  incorporated into ObjectGraph
* Can very easily extend an existing component and replace some beans in object with those used in testing.
* Also supports runtime graph construction in jre to speed up tests and potentially development.
* Package access components are supported by generating factory glue in the package (the equivalent of `@Provide`
  annotated class in the same package) and it is assumed any consumers are also in the same package.

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
* Need to incorporate factory functionality (i.e. [AssistedInject](https://github.com/square/AssistedInject))
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
