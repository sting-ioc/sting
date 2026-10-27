# T01 — Resolve and validate factory construction mappings

- Status: complete
- Blocked by: None
- Spec coverage: R1, R2, R4; AC1, AC2, AC4

## Delivers

Factory interfaces return authored classes/interfaces and construct the concrete class selected by existing framework metadata. Authoritative concrete-return mappings and unmapped ordinary concrete factories both work. Invalid targets produce focused errors.

## Acceptance criteria

- [x] Abstract, interface and concrete declared return types resolve valid mappings, including a custom simple-name integration annotation and implementation-only caller parameters.
- [x] A mapped product without Injectable can be created repeatedly with captured services and caller values.
- [x] Invalid metadata/target/constructor/parameter cases are rejected; existing direct factory behavior remains covered.

## Validation

- Focused new processor and integration tests, then existing processor and integration aggregate targets.
- Inspect generated code for the authored return declaration and mapped constructor call; inspect diff and syntax/format changes.

## Evidence

Processor suite: 568/568 passed; integration suite: 59/59 passed. Command: bazel --output_base=/home/peter/.cache/bazel/sting-provider-factories test //processor/src/test/java/sting/processor:all_tests //integration-tests/src/test/java/sting/integration:all_tests --test_output=errors. New mapping cases initially failed against the unmodified processor; mapped runtime scenarios now verify concrete implementation, caller values, nullable values, distinct products and shared injected interface binding. Formatting and git diff --check passed. Generated factory source retains the authored return type and invokes the mapped constructor.

These counts describe pre-rebase evidence. Current upstream harness and full-gate revalidation are owned by T02.
