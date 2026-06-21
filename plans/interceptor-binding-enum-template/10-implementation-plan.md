# Interceptor Binding Enum Template Implementation Plan

Status: accepted.

## Delivery Approach

Implement template support as a narrow processor feature:

1. Keep `InterceptorBinding.implementedBy` as a string template/literal.
2. Resolve templates to concrete classnames while creating `InterceptorBindingDescriptor`.
3. Store the resolved classname on the descriptor so downstream resolver/proxy code remains unchanged.
4. Reuse existing binding-value extraction and existing interceptor validation wherever possible.
5. Validate only the effective enum value used by each reachable interceptor binding usage.

## Phase Sequence

### Phase 1: Template Parsing And Validation

- Add a small parser for `{memberName}` placeholders.
- Treat strings without `{` or `}` as literal classnames and validate through the existing path.
- Replace the current raw `validateImplementedByName( implementedBy, ... )` call during interceptor binding annotation validation with template-aware validation:
  - literal classnames still call canonical dotted-name validation immediately.
  - template strings validate placeholder syntax and referenced member compatibility before substitution.
  - canonical dotted-name validation runs only on the resolved classname after substitution.
- Reject malformed template syntax:
  - unmatched `{` or `}`
  - empty placeholder name
  - placeholder name that is not a Java identifier
  - placeholder references missing annotation member
  - placeholder references unsupported value kind
- Restrict supported placeholder values to scalar `BindingValueKind.ENUM`.

### Phase 2: Enum Fragment Conversion

- Add enum constant to PascalCase conversion.
- Convert `enumConstantName()` from `BindingValueModel`, not `javaLiteral()`.
- Use `Locale.ROOT` for all case conversion.
- Reject enum constants with empty name segments after splitting on `_`.
- Substitute converted fragments into the template.
- Validate the resolved classname with the existing canonical dotted Java name check.

### Phase 3: Resolution Integration

- Resolve `implementedBy` before constructing or while constructing `InterceptorBindingDescriptor`.
- Keep `resolveGenericInterceptor` operating on `interceptor.getImplementedBy()`.
- Ensure diagnostics use the original binding usage and annotation mirror.
- Preserve existing literal classname behavior and existing third-party `InterceptorBinding` simple-name compatibility.

### Phase 4: Fixture Coverage

- Add a positive fixture with enum-backed template expansion:
  - default enum value path
  - explicit non-default enum value path
  - at least one underscore conversion such as `REQUIRES_NEW` -> `RequiresNew`
  - third-party simple-name `InterceptorBinding` meta-annotation compatibility
- Add negative fixtures:
  - unknown placeholder member
  - placeholder references `String`
  - placeholder references enum array
  - malformed template
  - syntactically valid template that resolves to a non-canonical classname
  - resolved missing interceptor class
  - invalid enum constant shape for PascalCase conversion, such as `_BAD`, `BAD_`, or `BAD__NAME`
- Add generated source assertions where useful to verify the selected interceptor classes are used.

### Phase 5: Documentation

- Update `core/src/main/java/sting/interceptors/InterceptorBinding.java`.
- Update `docs/interceptors.md`.
- Update `docs/framework_integration.md` if third-party equivalent annotations need the same template description.
- Update `docs/annotation_processing.md` if the compatibility table needs to mention templates.
- Update `CHANGELOG.md`.

### Phase 6: Validation

- Run targeted processor tests during iteration.
- Run the required full gate before implementation is considered complete:

```bash
bundle exec buildr test
```

## High-Risk Areas And Mitigations

- Risk: Template validation weakens existing classname validation.
  - Mitigation: Keep literal validation unchanged and validate resolved classnames with the current `validateImplementedByName`.
- Risk: Placeholder substitution accidentally allows string-driven arbitrary classnames.
  - Mitigation: Accept only scalar enum `BindingValueKind.ENUM` values.
- Risk: Diagnostics point at internal generated descriptors instead of user annotations.
  - Mitigation: Throw `ProcessorException` with the binding usage element and annotation mirror already stored in `InterceptorBindingDescriptor` creation.
- Risk: Unused enum constants with missing interceptor classes will not fail until that enum value is used on a reachable binding.
  - Mitigation: This is the accepted Option A behavior and matches the current reachable-binding validation model.

## Required Full Gate

```bash
bundle exec buildr test
```

## Optional Pre-PR Gate

```bash
bundle exec buildr ci J2CL=no
```

## Decision Log

### Locked

- Enum-only placeholders: plan restricts placeholders to scalar enum annotation members.
- No string placeholders: arbitrary string values are intentionally excluded for this change.
- No runtime selection: selected interceptor is resolved at compile time.
- Existing literal classnames remain valid.

### Resolved

- Q-01: Option A selected. Template validation covers only the effective enum value used by each reachable interceptor binding usage. This keeps implementation aligned with the current lazy interceptor validation model and requires no all-constant enumeration task.

## Expected Files To Touch

- `processor/src/main/java/sting/processor/StingProcessor.java`
- `processor/src/main/java/sting/processor/InterceptorBindingDescriptor.java` if the descriptor should retain both raw template and resolved classname
- `processor/src/test/java/sting/processor/StingProcessorTest.java`
- `processor/src/test/fixtures/input/com/example/interceptor/*.java`
- `processor/src/test/fixtures/bad_input/com/example/interceptor/*.java`
- `core/src/main/java/sting/interceptors/InterceptorBinding.java`
- `docs/interceptors.md`
- `docs/framework_integration.md`
- `docs/annotation_processing.md`
- `CHANGELOG.md`

## Acceptance Criteria

- Literal `implementedBy` values behave exactly as before.
- Enum template `implementedBy` values resolve to the expected interceptor class for default and explicit enum values.
- Non-enum placeholders fail with a clear compile-time error.
- Malformed placeholders fail with a clear compile-time error.
- Missing resolved interceptor classes fail through a clear compile-time error.
- Generated proxies depend on and invoke the resolved interceptor classes.
- Documentation describes syntax, enum-only limitation, and PascalCase conversion.
- `bundle exec buildr test` passes.

## Plan Acceptance

This plan must not be marked accepted until:

- Q-01 is resolved and recorded in this file.
- The task board records user review and approval.
- Any requested changes are applied to the planning artifacts.
