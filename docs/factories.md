---
title: Factories
---

The {@link: sting.Factory @Factory} annotation is used to generate a dependency-injected factory implementation.
This is useful when a type requires a mixture of values supplied by application code and services supplied by Sting.

The examples use `org.jspecify.annotations.NullMarked` for nonnull defaults.

For example, consider a component that needs a `SomeService` from Sting and a runtime `int` parameter:

```java
@NullMarked
class MyComponent
{
  MyComponent( final SomeService someService, final int someParameter )
  {
  }
}
```

You can define a factory interface such as:

```java
@NullMarked
@Factory
interface MyComponentFactory
{
  MyComponent create( int someParameter );
}
```

Sting will generate an implementation using the normal generated naming convention. The generated type is an
{@link: sting.Injectable @Injectable} that publishes the factory interface via {@link: sting.Typed @Typed}, so it can
be included in injectors like any other provider-backed component.

```java
@NullMarked
@Injector( fragmentOnly = false, includes = MyComponentFactory.class )
interface MyInjector
{
  MyComponentFactory factory();
}
```

## Rules

- The `@Factory` target must be an interface.
- The interface may declare any number of default methods.
- Abstract instance methods are treated as candidate factory methods. Return types must be concrete classes or
  classes/interfaces mapped to concrete implementations through `StingProvider`.
- The created type must have exactly one constructor and that constructor must be accessible from the factory
  interface package.
- Factory method parameters must match constructor parameters by both name and type.
- Constructor parameters omitted from the factory method are treated as Sting-managed dependencies and injected into
  the generated factory implementation.
- A single `@Factory` interface may define multiple factory methods for different created types.

## Framework-generated implementations

A factory can return an authored component type while constructing its generated implementation. Sting reads the
`StingProvider` metadata on the return type's framework annotation, using the same simple-name convention described in
[framework integration](framework_integration.md).

For example, if a framework maps `MyComponent` to `MyComponentImpl`, this declaration returns `MyComponent` and constructs
`MyComponentImpl`:

```java
@Factory
interface MyComponentFactory
{
  MyComponent create( int someParameter );
}
```

The mapping always takes precedence, including when `MyComponent` is concrete. Its implementation must be a concrete,
non-generic, assignable class with one constructor accessible from the factory's package. The declared return type must
also be non-generic. An invalid mapping is an error; Sting does not fall back to constructing the declared type.

The implementation does not need `@Injectable`. The factory captures services and creates a fresh product for each
call without registering that product as a Sting service. Include resolution and service auto-discovery retain their
existing provider requirements.

Sting waits across processing rounds for a generated implementation and its constructor types. Matching uses that
implementation's constructor, including additional parameters introduced by the framework. For Arez components, this
includes observable initializer parameters. Keep the factory in the component's package when the generated constructor
has package access. If the implementation was compiled separately, retain its constructor parameter names, for example
with `javac -parameters`.

## Generated Type

Given a factory interface named `MyComponentFactory`, Sting generates `Sting_MyComponentFactory` for top-level types,
or the usual flat-enclosing-name variant for nested types.

The generated implementation:

- copies standard whitelisted annotations from the factory interface,
- propagates nullability annotations to generated fields, constructor parameters, and methods,
- implements the factory interface directly,
- is itself a Sting `@Injectable`, allowing injectors and other generated providers to depend on it.
