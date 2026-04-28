# Implementation Plan: `@Factory`

## Phase Sequence

1. Finalize requirements and provider-shape decision for `@Factory`.
2. Add public API and processor model/generation support for `@Factory`.
3. Add processor fixtures and negative/positive tests for validation and generated output.
4. Add integration coverage proving injectors can consume generated factories.
5. Update docs, changelog, and any framework-integration guidance impacted by provider handling.
6. Run full gates and close out task evidence.

## Delivery Approach

- Execute one task at a time with minimal diffs.
- Validate iteratively with targeted checks.
- Run full gates before marking tasks complete.
- Keep plan/task board/docs aligned with code changes.
- Record the provider-shape decision before implementation begins.

## High-Risk Areas

- Provider integration:
  - Impact: `@Factory` may not be usable from injectors/fragments if generated types do not satisfy current `@StingProvider` resolution.
  - Mitigation: Resolve `Q-01` first, update provider validation/docs consistently, and add targeted coverage for include/autodetect flows.
- Constructor/method matching:
  - Impact: Incorrect matching could create subtle runtime bugs or confusing compile failures.
  - Mitigation: Introduce explicit validation for return type, constructor count/access, abstract method shape, parameter name/type matching, and omitted dependency injection.
- Annotation propagation:
  - Impact: Lost nullability or copied annotations on the wrong generated element can break downstream tooling or parity expectations.
  - Mitigation: Reuse existing generator utilities where possible and add fixture assertions for copied annotations and nullability placement.

## Decision Log

- `Q-01`: resolved. `@Factory` will generate an `@Injectable` implementation that publishes the factory interface via `@Typed( FactoryInterface.class )`. Documentation and stale diagnostics describing `@StingProvider` as `@Fragment`/`@Injector`-only must be corrected to match actual processor behavior.
- `Q-02`: resolved. `@Factory` generated implementations will use Sting's existing generated naming convention via `StingGeneratorUtil.getGeneratedClassName(...)`, yielding names like `Sting_MyComponentFactory`.

## Required Full Gates

`bundle exec buildr test`

## Completion Criteria

- All planned tasks completed.
- Evidence recorded for each completed task.
- Final gates passing.
- Working tree clean (or documented exception).
- Plan reviewed by the user after open questions are resolved and reflected below.

## Plan Acceptance

- status: `ready_for_review`
- blocker: none
