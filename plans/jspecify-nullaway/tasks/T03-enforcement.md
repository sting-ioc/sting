# T03 — Default and generated NullAway enforcement

- Status: pending
- Blocked by: T02
- Spec coverage: R5, AC4

## Delivers

Every sourceful owned target and positive generated fixture is checked in NullAway JSpecify mode, with a failing generated control proving the analyzer is active.

## Acceptance criteria

- [ ] Default library/binary/test/example wrappers attach JSpecify and NullAway, following the JDBT pattern.
- [ ] Generated sources are included in checking and contain no blanket NullAway suppression or NullUnmarked holes.
- [ ] Positive fixture compilation explicitly uses an Error Prone/NullAway-capable compiler path rather than relying on Bazel options reaching plain nested javac.
- [ ] A deliberately invalid generated source fails specifically with NullAway; all valid generated fixtures pass the same checked path.
- [ ] Migrate test/example contracts exposed by strict checking, including arrays and nullable interception results.
- [ ] Invalid processor fixtures remain in a separate diagnostics path and cannot falsely satisfy positive coverage.

## Validation

- Run positive and negative generated checking controls and all processor tests.
- `bazel build //core //processor //server //doc-examples` and `bazel test //:all_tests` — verify default enforcement across owned sources and generation.
- Inspect effective compiler flags and exclusions; ensure checking cannot silently skip generated output.
- `git diff --check`.

## Evidence

- pending
