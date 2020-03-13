---
title: Dagger Comparison
---

[Dagger](https://github.com/google/dagger) is a mature, established and widely used injection toolkit
for Java. Like Sting, Dagger is a compile-time framework that uses "...no reflection or runtime bytecode
generation, does all its analysis at compile-time, and generates plain Java source code.". Most of the original
Sting applications were migrated from Dagger so a comparison with Dagger would help explain why applications
moved from one toolkit to the other.

Dagger began as an injection framework for Android applications and expanded into other domains while Sting
is primarily focused on web applications and relatively small command line applications. Dagger is much more
feature rich while Sting has a more narrow focus.

Some of the additional features present in Dagger and missing from Sting include:

* [Producers](https://dagger.dev/producers) or asynchronous dependency injection capabilities. Asynchronously
  produced types can exist within a normal application and there is limited control over scheduling construction
  of these asynchronous types within the application. The only real constraint is that non-asynchronous types
  can not be **directly** dependent on asynchronous types. This is not supported within Sting as asynchronicity
  is handled differently within a web application if it is ever required. Even if there was a valid use case for
  asynchronous injector construction, a radically different implementation is required.

* [Android integration](https://dagger.dev/android) exists to make it relatively easy to integrate into
  Android applications and expose Android specific types into the Dagger component graph and to expose Dagger
  components to the Android framework. Sting provides no builtin support for Android ... *but* providing Android
  types to a Sting injector is trivial.

* [gRPC integration](https://dagger.dev/grpc). Sting provides no integration with gRPC.

* Dagger uses the annotations and types defined in the `javax.inject` package which was standardized as part of
  the ["JSR 330: Dependency Injection for Java"](https://jcp.org/en/jsr/detail?id=330) specification. Dagger still
  uses dagger-specific annotations and types for dagger specific functionality. Sting uses a non-standard set of
  annotations that are partially inspired by the best bits of
  ["JSR 330: Dependency Injection for Java"](https://jcp.org/en/jsr/detail?id=330) and
  ["JSR 365: Contexts and Dependency Injection for Java"](https://docs.jboss.org/cdi/spec/2.0/cdi-spec.html).

* Dagger supports arbitrary typed qualifier annotations. (See the javadocs of `@javax.inject.Qualifier` for
  further details). This eliminates the chance of accidental collision between qualifiers defined in different
  frameworks. Sting only supports string based qualifiers and relies on different toolkits using reverse DNS
  qualified names to guarantee that no accidental collision occurs.

* Dagger supports field-based, method-based and constructor based injection strategies. Sting only supports
  constructor based injection. This can result in Sting applications containing more user written boilerplate
  code to accept dependencies in the constructor and assign the dependencies to fields. Sting takes this approach
  as it makes it easier for users to understand the initialization sequence and it makes it easy to integrate
  Sting into downstream frameworks that contain a `@PostConstruct` style annotation. Before this restriction,
  developers may need to be aware of the order that dependencies are injected in (i.e. the order of
  field-versus-method injections, are superclass injections resolved first, do injections occur in declaration
  order in are they sorted etc). The injection ordering differs between injection toolkits which can also lead
  to confusion.

* Dagger supports the injection of a wider variety of types. Sting does not allow the injection of array types
  nor does it support the injection of parameterized types with the exception of specific framework types (i.e.
  `java.util.Collection` and `java.util.function.Supplier`).

* Dagger has the concept of subcomponents which is not present in Sting. Sting can model them as injectable
  `@Injector` annotated types but this is not as efficient as it could be.

* Dagger has much better support for scopes as well as the (dagger specific) caching
  [reusable scope](https://dagger.dev/users-guide#reusable-scope). Sting assumes every binding within the
  injector has a single instance (at most) which is conceptually similar to `@Singleton` scoped instances. It is
  possible to model scopes in Sting but it is laborious and inefficient.

It is possible that Sting will gain the functionality of subcomponents and scopes in the future and may even
improve on the Dagger model by adding features such as
[disposable injectors](https://github.com/sting-ioc/sting/issues/4) but there is no concrete plans to implement
this at this time.

Sting does have some significant advantages over Dagger from a usability and performance perspective. Some
strengths of Sting relative to Dagger include:

* Sting optimizes for fast incremental build times. It is not unsurprising to see a 2x-3x build speed improvement
  when using Sting rather than Dagger. See the [performance](performance.md) report for further details.

* Small code size. Sting can generated injectors that are 50% to 70% the size of the equivalent dagger injector.

* Fast initialization time. Sting is often faster to initialize and that speedup is typically proportional
  to the code size improvement.

* Sting generates code that has zero runtime dependencies other than the JRE. Dagger has a runtime dependency
  on the `javax.inject` package as well as several dagger packages depending on which features of the framework
  are used.

* The sting annotation processor is small, self-contained and vendors it's dependencies. This makes it easy to add
  a single dependency to the processor path that does not interfere with other annotation processors. As of version
  `2.25.2`, the Dagger annotation processor requires ~14 artifacts to be added to the processor path including
  several artifacts that are commonly used by other annotation processors such as `com.google.auto:auto-common`,
  `com.google.guava:guava` and `com.squareup:javapoet`. Version conflicts can occasionally cause conflicts
  when different annotation processors are using different versions of these libraries.

* Sting components can be annotated with the {@link: sting.Eager @Eager} annotation. This will result in the
  component being constructed when the injector is constructed. Lazy components are those without this annotation
  and will be constructed the first time they are accessed. This is particularly useful in a web context when
  components will often register event listeners when they are constructed and thus need to be created when the
  application is initializing.

* Sting supports the {@link: sting.Typed @Typed} annotation to control the types published by a component. The same
  capability is present within dagger but this capability requires that the bindings are declared using a
  `@dagger.Module` annotated type which is significantly more verbose.

* Stings supports publishing and consuming package-access components, even when the package-access component is in
  a different package from the injector.

* Sting suppresses any warnings in the generated code to ensure that if the code is compiled with javac linting
  enabled then no warnings will be generated. Sting verifies this behaviour by enabling linting in tests and
  failing tests when linting errors are detected.

* Sting generates errors or (suppressable) warnings when problematic or confusing code constructs are present in
  the analyzed code. i.e. {@link: sting.Injectable @Injectable} annotated types should either not specify a
  constructor or should specify a single package access constructor, annotations from other injection frameworks
  should not be intermingled with sting code etc.

* Sting works hard to detect and report problems with the component graph before code generation occurs rather
  than generating incorrect code and leaving it to javac to detect problems ... and for the user to work back
  from the javac error message to the problem in the code.

* Sting makes it easy to omit optional dependencies from the component graph and will just pass nulls or omit
  the component from collections.

* Sting has specific constructs designed to make [integration](framework_integration.md) into other frameworks easy.

The goal of Sting is to have a great developer experience and a great end-user experience. It is particularly
oriented towards challenges usually faced when developing modern web applications.
