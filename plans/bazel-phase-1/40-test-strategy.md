# Bazel Phase 1 Test Strategy

Status: accepted
Last updated: 2026-07-04

## Validation Goals

- Prove Bazel can build the Phase 1 modules in parallel with Buildr.
- Prove Bazel can run existing TestNG tests.
- Prove annotation processing works for server, doc examples, integration tests, and server integration tests.
- Prove depgen outputs are reproducible.
- Prove Java formatting and buildifier checks are enforceable.
- Establish and enforce processor/server unit coverage baseline.

## Targeted Checks

Run while implementing:

```bash
bazel build //core:core
bazel build //processor:processor
bazel build //server:server
bazel build //doc-examples:doc_examples
bazel test //processor/src/test/java/sting/processor:processor_tests
bazel test //server/src/test/java/sting/server/interceptors:server_tests
bazel test //integration-tests/src/test/java/sting/integration:integration_tests
bazel test //server-integration-tests/src/test/java/sting/server/integration:server_integration_tests
```

Exact labels may change during implementation to match final package names. The scope must remain equivalent.

## Full Gate

```bash
tools/check.sh
```

`tools/check.sh` should perform:

1. `tools/update_java_deps.sh`
2. Stale generated output check
3. buildifier check
4. Java format check
5. `bazel build //...`
6. `bazel test //...`
7. Bazel coverage for processor and server unit-test targets only
8. Coverage threshold check

## Coverage Gate

Coverage inputs:

- `//processor/src/test/java/sting/processor:processor_tests`
- `//server/src/test/java/sting/server/interceptors:server_tests`

Coverage measurement:

- Include `processor/src/main/java/**`
- Include `server/src/main/java/**`
- Exclude test sources, fixtures, generated sources, doc examples, integration-test code, and third-party code

Threshold policy:

- Run first passing Bazel coverage report after tests pass.
- Record observed line/branch coverage.
- Round down slightly to create stable initial thresholds.
- Do not add tests just to raise thresholds.

## Failure Handling

- Error Prone/strict-deps findings:
  - Fix narrow findings directly.
  - Stop and ask before broad churn or weaker test rules.

- Depgen drift:
  - Treat as failure.
  - Regenerate and commit updated depgen outputs.

- Coverage instability:
  - Verify measured file filter first.
  - Adjust threshold only based on reproducible evidence, not preference.

- Processor fixture path failures:
  - Prefer runfiles-aware fixture path changes in Bazel/test macro configuration.
  - Avoid copying fixtures into source paths.
