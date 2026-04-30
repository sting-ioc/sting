---
title: Framework Integration
---

Sting integrates cleanly with other annotation‑based frameworks without requiring those frameworks to
depend on Sting directly. Integration relies on validation and provider-resolution meta-annotations
that Sting detects by simple name and shape:

- StingProvider: identifies a framework annotation that points to a provider type Sting should include.
- ActAsStingConsumer: marks a framework annotation whose annotated types may consume qualified Sting
  services via constructor parameters.
- ActAsStingProvider: marks a framework annotation whose annotated types may carry Sting provider
  metadata for validation purposes.
- ActAsStingComponent: the union of `ActAsStingConsumer` and `ActAsStingProvider`.

Sting matches these meta‑annotations by simple name and shape (not FQN) so a framework may either:

- Depend on Sting and use `sting.StingProvider`, `sting.ActAsStingConsumer`,
  `sting.ActAsStingProvider`, and `sting.ActAsStingComponent`, or
- Define annotations with the same simple names and method signatures in its own package.

Using StingProvider

- Apply a `@StingProvider` meta‑annotation to a framework’s component annotation. The `value()` defines a
  naming pattern for the provider class that Sting should include. The provider class must exist, be in the same
  package as the referenced type, and be annotated with `@Fragment` or `@Injectable`.
- The meta-annotation does not itself create a Sting binding. Sting still processes the resolved provider type.
- `@StingProvider` does not suppress validation errors for `@Named`, `@Typed`, or `@Eager` on the
  framework-managed type. Sting only observes those annotations on the resolved provider. Providers used only as
  explicit include aliases usually do not need to propagate those annotations for the framework type.
  Providers intended to support auto-discovery usually do need to propagate them when the framework type
  should be published with those semantics. If the provider is generated as an `@Injectable`, copy the
  annotations to that type. If the provider is generated as a `@Fragment`, copy them to the relevant
  provider method rather than leaving them only on the framework component type.
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
If the resolved provider type is an `@Injectable` rather than a `@Fragment`, then
`@Injector(fragmentOnly = false, includes = Foo.class)` is required.

Explicit include aliasing does not require the resolved provider to publish `Foo`. For example,
`@Injector(fragmentOnly = false, includes = Foo.class)` may resolve to a generated `@Injectable`
provider that publishes only its own provider type so other components can depend on that generated
provider directly.

Provider-backed auto-discovery is a separate integration pattern. If an injector requests `Foo`
without an explicit include, Sting can auto-discover the provider-backed component only when the
resolved provider publishes `Foo` with the default qualifier. This is the case where propagating
`@Named`, `@Typed`, and `@Eager` from the framework-managed type onto the resolved provider usually
matters.

ActAsStingConsumer

- Apply `@ActAsStingConsumer` (or an equivalent annotation with the same shape) to a framework annotation to
  suppress Sting warnings for constructor parameters that legitimately use `@Named` but are processed by another
  framework.
- This is validation-only integration. It does not make the annotated type a Sting-managed component and it
  does not participate in include resolution or auto-discovery.

ActAsStingProvider

- Apply `@ActAsStingProvider` (or an equivalent annotation with the same shape) to a framework annotation to
  suppress Sting warnings for framework-managed types that legitimately use type-level `@Named`,
  `@Typed`, or `@Eager`.
- This is validation-only integration. It does not make the annotated type a Sting-managed component and it
  does not participate in include resolution or auto-discovery.

ActAsStingComponent

- Apply `@ActAsStingComponent` (or an equivalent annotation with the same shape) to opt into both
  validation roles.
- This is a convenience shorthand for frameworks that need both consumer-side constructor qualifier
  support and provider-side type metadata support.

For a full placement matrix, see [Annotation Processing](annotation_processing.md).

See also: sting/ActAsStingConsumer, sting/ActAsStingProvider, sting/ActAsStingComponent, and
sting/StingProvider Javadoc for API details.
