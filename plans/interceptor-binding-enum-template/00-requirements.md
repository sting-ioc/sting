# Interceptor Binding Enum Template Requirements

Status: accepted.

## Mission

Add support for enum-backed placeholders in `@InterceptorBinding.implementedBy` so an interceptor binding annotation can select an interceptor implementation class from an enum annotation member value.

Example target behavior:

```java
@InterceptorBinding( implementedBy = "sting.server.interceptors.{value}TransactionInterceptor", priority = 50 )
@Target( ElementType.TYPE )
@Retention( RetentionPolicy.CLASS )
public @interface Transactional
{
  TxType value() default TxType.REQUIRED;

  enum TxType {REQUIRED, REQUIRES_NEW, MANDATORY, SUPPORTS, NOT_SUPPORTED, NEVER}
}
```

Effective interceptor classname examples:

- `TxType.REQUIRED` -> `sting.server.interceptors.RequiredTransactionInterceptor`
- `TxType.REQUIRES_NEW` -> `sting.server.interceptors.RequiresNewTransactionInterceptor`
- `TxType.MANDATORY` -> `sting.server.interceptors.MandatoryTransactionInterceptor`
- `TxType.SUPPORTS` -> `sting.server.interceptors.SupportsTransactionInterceptor`
- `TxType.NOT_SUPPORTED` -> `sting.server.interceptors.NotSupportedTransactionInterceptor`
- `TxType.NEVER` -> `sting.server.interceptors.NeverTransactionInterceptor`

## Scope Boundaries

In scope:

- Parse `{memberName}` placeholders in `implementedBy`.
- Resolve placeholders from the effective annotation values already extracted for interceptor bindings.
- Permit placeholders only when the referenced annotation member is a scalar enum value.
- Convert enum constant names to PascalCase fragments before substitution.
- Validate the resolved classname using the existing canonical dotted Java name validation.
- Resolve and validate the selected interceptor through the existing interceptor resolution path.
- Add positive and negative processor fixture coverage.
- Update public docs and Javadoc to describe enum-only placeholder behavior.
- Add a `CHANGELOG.md` entry because this is user-visible API behavior.

Out of scope:

- String, primitive, class, annotation, or array placeholder values.
- Runtime selection. Resolution remains compile-time only.
- Method-level interceptor binding support.
- Changing interceptor lifecycle validation, proxy invocation ordering, dependency graph behavior, or priority semantics.
- Backward compatibility shims for malformed template syntax.

## Locked Decisions

- `implementedBy` remains a `String`.
- Placeholder values are restricted to scalar enum annotation members for this change.
- Existing literal canonical classnames must continue to work unchanged.
- The processor must keep simple-name meta-annotation detection for `InterceptorBinding`.
- Template expansion happens before class lookup and before proxy generation.
- Generated proxy code should consume the resolved interceptor descriptor and not re-evaluate templates.
- Template validation checks only the effective enum value used by each reachable interceptor binding usage.

## Command Surface And Behavior Expectations

- Literal current form:
  - `@InterceptorBinding( implementedBy = "com.example.TraceInterceptor", priority = 100 )`
  - Behavior remains unchanged.
- Template form:
  - `@InterceptorBinding( implementedBy = "com.example.{mode}TraceInterceptor", priority = 100 )`
  - `{mode}` must refer to an enum-valued member on the interceptor binding annotation.
  - The effective enum constant is converted to PascalCase and substituted into the classname.
- Error cases should produce compile-time diagnostics on the binding usage or binding annotation:
  - Unknown placeholder member.
  - Placeholder references a non-enum member.
  - Placeholder references an array-valued member.
  - Malformed template syntax.
  - Resolved classname is not a canonical dotted Java name.
  - Resolved interceptor class does not exist.
  - Resolved interceptor class fails existing `@Injectable`, visibility, or lifecycle validation.

## PascalCase Conversion Rules

Proposed default:

- Split enum constant names on underscores.
- Lowercase and uppercase segments using `Locale.ROOT` so conversion is locale-independent.
- Concatenate segments without separators.
- Reject empty segments caused by leading, trailing, or repeated underscores.

Examples:

- `REQUIRED` -> `Required`
- `REQUIRES_NEW` -> `RequiresNew`
- `NOT_SUPPORTED` -> `NotSupported`
- `V2_ONLY` -> `V2Only`

## Quality, Test, And Coverage Gates

Targeted checks:

- Processor compile tests covering positive enum placeholder expansion.
- Processor compile tests covering negative diagnostics.
- Generated source assertions that verify the selected interceptor type appears in generated proxy code.

Required full gate:

```bash
bundle exec buildr test
```

Optional broader gate before PR:

```bash
bundle exec buildr ci J2CL=no
```

## Known Intentional Divergences

- String placeholders are intentionally unsupported even though they are technically easy to substitute. The enum restriction keeps the possible classname fragments bounded by the annotation type.
- Unsupported placeholders should fail clearly rather than silently falling back to the unresolved literal string.

## Evidence From Existing Code

- Interceptor binding values are already extracted with defaults in `processor/src/main/java/sting/processor/StingProcessor.java`.
- Enum binding values are already represented with their enum constant names in `BindingValueModel`.
- `implementedBy` currently validates as a canonical dotted Java name before lookup, so template validation must be split into template validation and resolved-classname validation.
- Interceptor resolution is centralized in `resolveGenericInterceptor`, which should remain the single point for class lookup and lifecycle validation.

## Open Questions Register

### Q-01

- status: resolved
- question: Should template validation check only effective enum values used by reachable interceptor bindings, or all enum constants declared by the referenced annotation member?
- context: Current interceptor validation is largely reachable-binding driven. For example, interceptor bindings are processed when a binding publishes a service and the resolved interceptor is then looked up. Validating every enum constant would catch missing interceptor classes earlier, but it would require extra class lookups for values that may never be used.
- options:
  - A: Validate only the effective enum value on each reachable binding usage.
  - B: Validate every enum constant for every template placeholder when the binding annotation is encountered.
- tradeoffs:
  - A preserves the current lazy validation model, keeps unreachable or unused enum values from failing builds, and minimizes processor work. A typo in an unused enum variant is detected only when that value is used.
  - B proves the full enum-to-class mapping up front and better matches the idea of a fixed enum set. It can fail builds for enum values that exist for API completeness but are not wired yet.
- recommended_default: A, because it aligns with the existing processor flow and avoids introducing a broader validation policy than current interceptor class lookup.
- decision impact: If B is selected, the implementation plan and task board must add explicit work for enumerating all enum constants, choosing diagnostic locations for unused enum values, class lookup for unused mappings, and an unused invalid enum value fixture.
- user_decision: Option A. Validate only the effective enum value on each reachable binding usage.
- artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`, `20-task-board.yaml`
