# Implementation Plan: Optional Service Request Expansion

## Phase Sequence

1. Extend `ServiceRequest` modeling and parser recognition for Optional-based request kinds.
2. Update graph resolution rules for missing/optional bindings and collection omission.
3. Update generators, descriptors, and reports for the new request forms.
4. Add processor fixtures, integration tests, and documentation coverage.
5. Run targeted validation and record evidence.

## Delivery Approach

- Keep published-service optionality on `ServiceSpec`.
- Add request-level semantics to `ServiceRequest.Kind`.
- Reuse shared generator paths so dependency parameters and injector outputs stay consistent.
- Validate with targeted processor/integration commands before attempting broader gates.

## High-Risk Areas

- Request parsing:
  - Risk: Accidentally accepting unsupported nested parameterized shapes.
  - Mitigation: Make `ServiceRequest.Kind.extractType` exact about supported wrappers.
- Optional binding resolution:
  - Risk: Breaking existing validation around nullable providers and optional inputs.
  - Mitigation: Separate “may be absent” and “may consume optional bindings” checks from published-service optionality.
- Collection generation:
  - Risk: `Collection<T>` omission requires runtime filtering while preserving read-only collections.
  - Mitigation: Centralize generation logic in injector output/dependency emission paths and cover empty/singleton/multi cases.

## Required Full Gates

`bundle exec buildr test`

## Completion Criteria

- Processor model and generation updated
- Fixtures and integration tests added
- Docs/Javadocs updated
- Targeted validation evidence recorded
