---
title: Annotation Processing
---

This page is the source-of-truth for where Sting actively processes each public annotation, where it only
tolerates an annotation for framework integration, and where a placement is ignored or invalid.

## Matrix

| Annotation | Actively processed by Sting | Silent integration exception | Ignored or invalid |
| --- | --- | --- | --- |
| `@Injectable` | Type target; top-level processor entrypoint. | None. | Invalid anywhere else. |
| `@Fragment` | Type target; top-level processor entrypoint. | None. | Invalid anywhere else. |
| `@Injector` | Type target; top-level processor entrypoint. | None. | Invalid anywhere else. |
| `@Factory` | Type target; top-level processor entrypoint. | None. | Invalid anywhere else. |
| `@InjectorFragment` | Type target; top-level processor entrypoint for validation-only integration. | None. | Does not create bindings or injector outputs. |
| `@Named` | `@Injectable` types, `@Injectable` constructor parameters, `@Fragment` provider methods, `@Fragment` provider method parameters, `@Injector` output methods. | `@InjectorFragment` methods; constructor parameters of types annotated via `@ActAsStingConsumer`; types annotated via `@ActAsStingProvider`; both placements via `@ActAsStingComponent`. | `@StingProvider`-annotated framework component types and other placements are invalid. |
| `@Typed` | `@Injectable` types, `@Fragment` provider methods. | Types annotated via `@ActAsStingProvider` or `@ActAsStingComponent`. | `@Injector` output methods, `@StingProvider`-annotated framework component types, and other placements are invalid. |
| `@Eager` | `@Injectable` types, `@Fragment` provider methods. | Types annotated via `@ActAsStingProvider` or `@ActAsStingComponent`. | `@StingProvider`-annotated framework component types and other placements are invalid. |
| `@StingProvider` | Integration meta-annotation consulted during includes and auto-discovery. | Not applicable. | Does not itself create a Sting binding. |
| `@ActAsStingConsumer` | Integration meta-annotation consulted during validation. | Not applicable. | Does not itself create a Sting binding. |
| `@ActAsStingProvider` | Integration meta-annotation consulted during validation. | Not applicable. | Does not itself create a Sting binding. |
| `@ActAsStingComponent` | Integration meta-annotation consulted during validation. | Not applicable. | Does not itself create a Sting binding. |
| `@Injector.Input` | Nested configuration processed only inside `@Injector`. | None. | Not treated as an independent processor trigger. |

## Notes

- `Feature` is not listed here because it is an enum, not an annotation.
- When `@Typed` is present on a `@Fragment` provider method, Sting publishes exactly the declared types rather
  than implicitly publishing the method return type.
- For provider-backed framework integration, Sting processes the resolved provider type, not the framework
  annotation target itself. `@Named`, `@Typed`, and `@Eager` must be placed on the resolved Sting provider
  type if they are needed for graph semantics. Providers used only as explicit include aliases usually do not
  need those annotations copied for the framework-managed type, while providers intended to support
  auto-discovery usually do when the framework-managed type should be published with those semantics.
- Sting matches `@ActAsStingConsumer`, `@ActAsStingProvider`, and `@ActAsStingComponent` by simple name and
  shape so third-party frameworks may define equivalent annotations without depending directly on Sting.
