# Requirements: Optional Service Request Expansion

## Mission

Expand Sting service requests so optional services can be requested via `Optional`-based wrappers and so `Collection<T>` can consume optional bindings by omitting absent values.

## Scope

- In scope:
  - Processor parsing and modeling for new optional request shapes
  - Graph resolution updates for optional-binding consumption
  - Code generation for dependency parameters and injector outputs
  - Processor fixtures, integration tests, docs, and Javadocs
- Out of scope:
  - New request shapes beyond `Optional<T>`, `Supplier<Optional<T>>`, `Collection<Supplier<Optional<T>>>`
  - `Collection<Optional<T>>`, `Optional<Collection<T>>`, or other nested parameterized forms

## Locked Decisions And Non-Negotiables

- Supported request forms are:
  - `@Nullable T`
  - `Optional<T>`
  - `Supplier<Optional<T>>`
  - `Collection<Supplier<Optional<T>>>`
  - `Collection<T>` with optional bindings omitted
- Behavior applies uniformly to optional injector inputs and nullable provider bindings.
- Injector output methods support the same new request forms as dependency parameters.
- `@Nullable Collection<T>` and `@Nullable Collection<Supplier<T>>` remain invalid.
- Existing semantics for `T`, `Supplier<T>`, and `Collection<Supplier<T>>` remain unchanged except for `Collection<T>` now accepting optional bindings by omission.

## Behavior Expectations

1. Singular optional-wrapper requests must accept zero or one matching binding and reject multiple matches.
2. `Optional<T>` and `Supplier<Optional<T>>` must map missing or null optional bindings to `Optional.empty()`.
3. `Collection<Supplier<Optional<T>>>` must expose one supplier per matching binding and preserve laziness.
4. `Collection<T>` must include only present values when optional bindings are involved.
5. Request parsing, descriptors, reports, and generated APIs must stay aligned.

## Quality Gates

- Targeted processor tests for parsing, graph validation, and generated outputs
- Integration tests for runtime optional behavior
- Documentation/Javadoc updates
- Full gate: `bundle exec buildr test`

## Known Intentional Divergences

- None

## Open Questions Register

- None. Scope and behavior were fixed before implementation.
