# Interceptor Binding Array Values Requirements

## Status

Accepted. Scope question resolved and user approved implementation.

## Mission

Enable interceptor binding annotations shaped like a transactional binding:

```java
@InterceptorBinding( implementedBy = "...", priority = ... )
@Target( { ElementType.TYPE, ElementType.METHOD } )
@Retention( RetentionPolicy.CLASS )
public @interface Transactional
{
  TxType value() default TxType.REQUIRED;

  enum TxType { REQUIRED, REQUIRES_NEW, MANDATORY, SUPPORTS, NOT_SUPPORTED, NEVER }

  Class[] rollbackOn() default {};

  Class[] dontRollbackOn() default {};
}
```

## Code-Backed Findings

- `TxType value()` already fits the existing scalar enum binding value path. Enum values are passed to lifecycle parameters as `String`.
- `Class<?> type()` already fits the existing scalar class binding value path. Class values are passed to lifecycle parameters as `String` class names.
- Array-valued binding members are rejected today. `BindingValueArrayModel` is a negative fixture and expects `has an unsupported v1 value type`.
- Empty array defaults such as `Class[] rollbackOn() default {}` require the processor to inspect the annotation member return type, not just the runtime `AnnotationValue` payload.
- Generated lifecycle calls use `BindingValueModel.javaLiteral()` directly, so array support should be modeled in binding value extraction and validation before proxy emission.
- Service-interface method and implementation method interceptor bindings are explicitly unsupported today. Fragment provider methods are supported as binding sources.

## Scope

In scope:

- Support array annotation members whose component type is already supported as a scalar binding value:
  `String`, primitive types, `char`, `Class`, and enum constants.
- Preserve the existing class and enum representation pattern:
  `Class` and enum scalar members map to `String`; `Class[]` and enum arrays map to `String[]`.
- Support empty array defaults by using annotation member return types during extraction.
- Keep annotation-valued members and annotation-array-valued members unsupported.
- Update processor fixtures, generated-source assertions, interceptor docs, and the changelog.

Out of scope unless Q-01 resolves otherwise:

- Service-interface method-level interception.
- Implementation method-level interception.
- Runtime annotation lookup or reflection-based binding behavior.
- Backward compatibility shims for unsupported v1 array behavior.

## Behavior Expectations

- An interceptor lifecycle parameter may request `@BindingValue("rollbackOn") String[] rollbackOn` when the binding member is `Class[] rollbackOn()`.
- An interceptor lifecycle parameter may request `@BindingValue("value") String txType` when the binding member is an enum.
- Generated proxy code should inline deterministic array literals such as `new String[] { "java.io.IOException" }` and `new String[] {}`.
- Wrong lifecycle parameter types should fail with the existing compatibility diagnostic style.
- Annotation-valued and annotation-array-valued members should continue to fail as unsupported.

## Binding Value Member Type Matrix

| Annotation member type | Lifecycle parameter type | Expected generated literal shape |
| --- | --- | --- |
| `String[]` | `String[]` | `new String[] { "alpha", "beta" }` |
| `boolean[]` | `boolean[]` | `new boolean[] { true, false }` |
| `byte[]` | `byte[]` | `new byte[] { (byte) 1 }` |
| `short[]` | `short[]` | `new short[] { (short) 2 }` |
| `int[]` | `int[]` | `new int[] { 3 }` |
| `long[]` | `long[]` | `new long[] { 4L }` |
| `float[]` | `float[]` | `new float[] { 5.0F }` |
| `double[]` | `double[]` | `new double[] { 6.0 }` |
| `char[]` | `char[]` | `new char[] { '\\n', '\\'' }` with existing escaping rules |
| `Class[]` or `Class<?>[]` | `String[]` | `new String[] { "java.io.IOException" }` and `new String[] {}` |
| `EnumType[]` | `String[]` | `new String[] { "REQUIRED", "NEVER" }` |

Unsupported:

- Annotation-valued members such as `Nested nested()`.
- Annotation-array-valued members such as `Nested[] nested()`, including empty defaults.

## Quality Gates

- Targeted processor verification: `bundle exec buildr processor:test`.
- Full repository gate before closeout: `bundle exec buildr test`.

## Open Questions Register

### Q-01

- status: resolved
- question: Should this change include service-interface or implementation method-level interceptor binding support, or should method usage remain limited to existing fragment provider methods?
- context: The sample annotation target includes `ElementType.METHOD`. The current processor allows binding annotations on service types, injectable implementation types, and fragment provider methods, but rejects service-interface methods and implementation methods.
- options:
  - Keep method-level service and implementation bindings out of scope.
  - Add service-interface method-level binding support.
  - Add implementation method-level binding support.
  - Add both service-interface and implementation method-level binding support.
- tradeoffs:
  - Keeping the current restriction makes this a focused binding-value enhancement and matches existing documentation.
  - Service-interface method support changes proxy resolution semantics because interceptors would vary by invoked method rather than only by intercepted service.
  - Implementation method support adds override/signature matching concerns between the published service method and implementation method.
  - Adding both expands the feature from value extraction into interception semantics and requires broader validation, conflict, and ordering rules.
- recommended_default: Keep method-level service and implementation bindings out of scope for this change. The immediate blocker for the transactional annotation shape is `Class[]` binding values; method-level interception is a separate behavioral feature.
- user_decision: Accepted recommended default. Keep service-interface and implementation method-level interceptor bindings out of scope; preserve existing fragment provider method support.
- artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`, `20-task-board.yaml`
