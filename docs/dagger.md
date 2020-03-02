---
title: Dagger Comparison
---

[Dagger](https://github.com/google/dagger) is a mature, established and widely used injection toolkit
for Java. Like Sting, Dagger is a compile-time framework that uses "...no reflection or runtime bytecode
generation, does all its analysis at compile-time, and generates plain Java source code.". Most of the original
Sting applications were migrated from Dagger so a comparison with Dagger would help explain why applications
moved from one toolkit to the other.

Dagger began as an injection framework for Android applications and expanded into other domains while Sting
is primarily focused on web applications and relatively small command line applications.

### Dagger is feature rich, Sting is more focused

Dagger has many more features which increases it's complexity but also increases the capabilities in
specific contexts. Some of the additional features present in Dagger and missing from Sting include:

* [Producers](https://dagger.dev/producers) or asynchronous dependency injection capabilities. Asynchronously
  produced types can exist within a normal application and there is limited control over scheduling construction
  of these asynchronous types within the application. The only real constrain is that non-asynchronous types
  can not be **directly** dependent on asynchronous types. This is not supported within Sting as asynchronicity
  is handled differently within a web application if it is ever required. Even if there was a valid use case for
  asynchronous injector construction, a radically different implementation is required.

* [Android integration](https://dagger.dev/android) exists to make it relatively easy to integrate into
  Android applications and expose Android specific types into the Dagger component graph and to expose Dagger
  components to Android framework. Sting provides no builtin support although providing Android types to a
  Sting injector is trivial.

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
  possible to model similar behaviour in Sting but it is laborious and inefficient.

It is possible that Sting will gain the functionality of subcomponents and scopes in the future and may even
improve on the Dagger model by adding features such as
[disposable injectors](https://github.com/sting-ioc/sting/issues/4) but there is no concrete plans to implement
this at this time.
