# Interceptor Binding Array Values Implementation Plan

## Status

Accepted. Q-01 is resolved and user approved implementation.

## Delivery Approach

Implement this as a focused processor enhancement: extend binding-value modeling to understand array-valued annotation members, validate lifecycle parameter compatibility, then update fixtures and documentation.

## Phase Sequence

1. Planning and scope lock
   - Resolve Q-01.
   - Update this plan and task board with the decision.

2. Binding value model support
   - Add array-capable binding value kinds or equivalent metadata.
   - Pass annotation member return type into binding value extraction so empty arrays retain component type.
   - Convert array members into deterministic Java literals.

3. Lifecycle parameter validation
   - Accept compatible array parameter types.
   - Preserve existing scalar behavior.
   - Keep annotation-valued members unsupported.

4. Tests and fixtures
   - Add or convert positive fixtures covering `Class[]`, empty `Class[]`, enum scalar, enum arrays, `String[]`, and primitive arrays.
   - Include explicit generated-source assertions for `byte[]` and `short[]` casts and escaped `char[]` literals.
   - Keep negative coverage for annotation-valued members, annotation-array-valued members, and wrong array parameter types.
   - Add generated-source assertions for emitted array literals.

5. Documentation
   - Update `BindingValue` Javadoc and interceptor docs to describe supported scalar and array member mappings.
   - Add a concise `CHANGELOG.md` Unreleased entry for the new binding-value array support.

6. Validation
   - Run `bundle exec buildr processor:test`.
   - Run `bundle exec buildr test`.

## High-Risk Areas

- Empty arrays: `AnnotationValue.getValue()` only exposes an empty list, so the member return type must drive the component kind.
- Class and enum arrays: these should follow the existing scalar convention and pass `String[]`, not `Class<?>[]` or enum arrays.
- Primitive arrays: generated literals need exact primitive array types, including byte/short casts and char escaping.
- Method-level binding: adding it would affect proxy semantics and is excluded unless Q-01 changes scope.

## Required Full Gate

`bundle exec buildr test`

## Decision Log

- Q-01: Keep service-interface and implementation method-level interceptor bindings out of scope. This plan remains focused on binding-value array support and preserves existing fragment provider method binding behavior.
- PLAN-APPROVAL: User requested implementation after iterative plan review reported no findings.

## Files Expected To Change

- `processor/src/main/java/sting/processor/BindingValueKind.java`
- `processor/src/main/java/sting/processor/BindingValueModel.java`
- `processor/src/main/java/sting/processor/StingProcessor.java`
- `processor/src/test/java/sting/processor/StingProcessorTest.java`
- `processor/src/test/fixtures/input/com/example/interceptor/*.java`
- `processor/src/test/fixtures/bad_input/com/example/interceptor/*.java`
- `core/src/main/java/sting/interceptors/BindingValue.java`
- `docs/interceptors.md`
- `CHANGELOG.md`

## Acceptance Criteria

- The transactional-style annotation shape compiles when `rollbackOn` and `dontRollbackOn` are requested as `String[]` binding values.
- The supported array member type matrix has positive or generated-source coverage, including empty `Class[]`, enum arrays, `String[]`, byte/short casts, and escaped char literals.
- Existing scalar binding value tests continue to pass.
- Existing unsupported annotation-valued member behavior remains rejected, and annotation-array-valued members are rejected.
- Documentation states the exact runtime parameter types expected for `Class[]` and enum arrays.
