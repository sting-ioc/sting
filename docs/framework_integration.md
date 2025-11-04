---
title: Framework Integration
---

Sting integrates cleanly with other annotation‑based frameworks without requiring those frameworks to
depend on Sting directly. Integration relies on two meta‑annotations that Sting detects by simple name:

- StingProvider: identifies a framework annotation that points to a provider type Sting should include.
- ActAsStingComponent: marks a framework annotation whose annotated types should be treated as Sting components
  for the purposes of validation (e.g., allowing @Named on constructors) even if Sting does not process them.

Sting matches these meta‑annotations by simple name and shape (not FQN) so a framework may either:

- Depend on Sting and use `sting.StingProvider` / `sting.ActAsStingComponent`, or
- Define annotations with the same simple names and method signatures in its own package.

Using StingProvider

- Apply a `@StingProvider` meta‑annotation to a framework’s component annotation. The `value()` defines a
  naming pattern for the provider class that Sting should include. The provider class must exist, be in the same
  package as the referenced type, and be annotated with `@Fragment` or `@Injector`.
- Supported tokens in the pattern: `[SimpleName]`, `[CompoundName]`, `[EnclosingName]`, `[FlatEnclosingName]`.

Example

// Framework annotation
@interface MyFrameworkComponent {
  @interface StingProvider { String value(); } // or use sting.StingProvider directly
}

@MyFrameworkComponent.StingProvider("[FlatEnclosingName]Fw_[SimpleName]_Provider")
@interface Component {}

// App code
@Component
class Foo {}

// Expected provider type (same package): Fw_Foo_Provider
@Fragment
interface Fw_Foo_Provider {
  default Foo provideFoo() { return new Foo(); }
}

Then `@Fragment(includes = Foo.class)` or `@Injector(includes = Foo.class)` will resolve and include `Fw_Foo_Provider`.

ActAsStingComponent

- Apply `@ActAsStingComponent` (or an equivalent annotation with the same shape) to a framework annotation to
  suppress Sting warnings for types/constructors that legitimately use `@Named` or `@ContributeTo` but are processed
  by another framework.

See also: sting/ActAsStingComponent and sting/StingProvider Javadoc for API details.
