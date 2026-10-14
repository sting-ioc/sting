# Proton source targets spec

## Source

- The accepted JSpecify/NullAway grill and repository-wide migration remain authoritative.
- The user now requests a local Proton override at revision `2dec0fee99c5c5b3707f9f8e8b876ec800e2c40b`, using its new public targets.
- Local Proton is clean at that revision, which is also remote master. Its root `core` and `qa` Java imports expose release binary and source jars; core includes its formatter.

## Problem

Sting currently consumes Proton 0.76 Maven artifacts, so it cannot use the dependency reductions and public Bazel targets in the requested source revision.

## Required outcome

Build and test Sting through the requested Proton targets, with local development linked to the existing checkout and a reproducible default for CI and other machines.

## Scope and constraints

- Change only Proton dependency wiring, affected release inputs, generated dependency declarations, documentation and PR context.
- Preserve JSpecify/NullAway contracts, Java 17 owned/generated output and the existing processor behavior.
- Keep the artifact-check simplification already committed at `ff500208`.
- Do not change the Proton checkout or add GWT/J2CL checks.
- Do not merge the PR.

## Requirements and acceptance criteria

- `R1` / `AC1`: Processor compilation and QA tests depend on Proton's public `core` and `qa` targets; no Maven Proton downloads or custom replacement imports remain.
- `R2` / `AC2`: Local builds resolve Proton from `/Users/peter/Code/realityforge/proton`, verified at the requested revision. Tracked configuration uses a Git override pinned to that revision; machine-specific paths stay ignored.
- `R3` / `AC3`: Both dependency-resolution modes build the shaded processor, preserve its formatter and compiled JSpecify contracts, and pass the isolated packaged-consumer check. Full repository checks pass.
- `R4` / `AC4`: Development documentation and the prepared PR-description update explain the pin and local override. After implementation review passes, publish the branch and description for human review and remove the temporary plan.

## Significant decisions

| ID | Decision | Rationale | Impact | User verification |
| --- | --- | --- | --- | --- |
| D1 | Git override at the requested revision, with an ignored local Bazel rc override | Links local work without putting an absolute path in shared configuration or adding a CI checkout | CI fetches the immutable revision; local edits to Proton are immediately visible | Inspect the module pin and local override path |
| D2 | Use `@proton//:core` and `@proton//:qa`; use core's produced release jar in shading/Javadocs | Public targets are the intended Maven-equivalent interface and include formatting | Avoids custom imports and preserves processor isolation | Review dependency labels and isolated consumer evidence |

## Testing decisions

- Build processor, one focused QA/nullness target and release artifacts before the full gate.
- Inspect the test launcher classpath: Proton QA exposes TestNG 6.8 while Sting selects 7.10.2, so confirm Sting's runner takes precedence.
- Run `tools/check.sh` with the local override.
- Temporarily remove only the task-owned ignored rc file, build release artifacts from the Git pin and run the isolated consumer check; restore the rc in a finally block.
- Verify generated dependencies, the source revision, clean diff, PR head and CI checks.

## Open questions

None.
