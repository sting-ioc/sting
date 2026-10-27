# T01 — Resolve and validate factory construction mappings

- Status: pending
- Blocked by: None
- Spec coverage: R1, R2, R4; AC1, AC2, AC4

## Delivers

Factory interfaces return authored classes/interfaces and construct the concrete class selected by existing framework metadata. Authoritative concrete-return mappings and unmapped ordinary concrete factories both work. Invalid targets produce focused errors.

## Acceptance criteria

- [ ] Abstract, interface and concrete declared return types resolve valid mappings, including a custom simple-name integration annotation and implementation-only caller parameters.
- [ ] A mapped product without Injectable can be created repeatedly with captured services and caller values.
- [ ] Invalid metadata/target/constructor/parameter cases are rejected; existing direct factory behavior remains covered.

## Validation

- Focused new processor and integration tests, then existing processor and integration aggregate targets.
- Inspect generated code for the authored return declaration and mapped constructor call; inspect diff and syntax/format changes.

## Evidence

Pending.
