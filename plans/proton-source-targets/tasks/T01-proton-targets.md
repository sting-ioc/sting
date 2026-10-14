# T01 — Use Proton's public source targets

- Status: `pending`
- Blocked by: None
- Spec coverage: `R1`–`R4`, `AC1`–`AC4`

## Delivers

The processor, QA harness, shaded distribution and Javadocs use the requested Proton revision through its supported public Bazel targets, with local source linkage and a portable pin.

## Acceptance criteria

- [ ] Module and ignored local configuration select the requested source revision.
- [ ] Maven Proton entries are removed and generated dependency output is refreshed.
- [ ] Processor and QA targets use the public imports; release shading retains Proton's bundled formatter.
- [ ] Local and pinned builds pass required validation.
- [ ] Docs and prepared PR context reflect the source dependency.

## Validation

- Inspect `git rev-parse HEAD` and status in Proton; inspect module graph/repository mapping.
- Focused processor/nullness/release build and artifact check.
- Inspect the actual test launcher classpath and confirm Sting's TestNG 7.10.2 runner precedes Proton QA's 6.8 dependency.
- Full `tools/check.sh` with local source linkage.
- Git-pinned release build and isolated packaged-consumer check without the local override.
- Inspect diff and prepare the PR-description update. Implementation review, publication and CI confirmation follow task completion.

## Evidence

- Pending.
