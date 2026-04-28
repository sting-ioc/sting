---
title: Factories
---

The {@link: sting.Factory @Factory} annotation is used to generate a dependency-injected factory implementation.
This is useful when a type requires a mixture of values supplied by application code and services supplied by Sting.

For example, consider a component that needs a `SomeService` from Sting and a runtime `int` parameter:

```java
class MyComponent
{
  MyComponent( @Nonnull final SomeService someService, final int someParameter )
  {
  }
}
```

You can define a factory interface such as:

```java
@Factory
interface MyComponentFactory
{
  @Nonnull
  MyComponent create( int someParameter );
}
```

Sting will generate an implementation using the normal generated naming convention. The generated type is an
{@link: sting.Injectable @Injectable} that publishes the factory interface via {@link: sting.Typed @Typed}, so it can
be included in injectors like any other provider-backed component.

```java
@Injector( fragmentOnly = false, includes = MyComponentFactory.class )
interface MyInjector
{
  @Nonnull
  MyComponentFactory factory();
}
```

## Rules

- The `@Factory` target must be an interface.
- The interface may declare any number of default methods.
- Abstract instance methods are treated as candidate factory methods and must return a class type.
- The created type must have exactly one constructor and that constructor must be accessible from the factory
  interface package.
- Factory method parameters must match constructor parameters by both name and type.
- Constructor parameters omitted from the factory method are treated as Sting-managed dependencies and injected into
  the generated factory implementation.
- A single `@Factory` interface may define multiple factory methods for different created types.

## Generated Type

Given a factory interface named `MyComponentFactory`, Sting generates `Sting_MyComponentFactory` for top-level types,
or the usual flat-enclosing-name variant for nested types.

The generated implementation:

- copies standard whitelisted annotations from the factory interface,
- propagates nullability annotations to generated fields, constructor parameters, and methods,
- implements the factory interface directly,
- is itself a Sting `@Injectable`, allowing injectors and other generated providers to depend on it.
