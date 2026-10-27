# T02 — Prove generated implementation integration and document delivery

- Status: complete
- Blocked by: T01
- Spec coverage: R3, R5, R6; AC3, AC5, AC6

## Delivers

Provider-backed factories cooperate with another source generator across compiler rounds and compiled dependencies. The actual Arez proof demonstrates the intended consumer, and public documentation explains the complete contract.

## Acceptance criteria

- [x] Later-generated implementation and later-resolved constructor parameter types succeed with both processor orders; a genuinely absent implementation has a useful final error.
- [x] Separately compiled mapping metadata and implementation remain usable under the existing parameter-name metadata requirement.
- [x] Actual Arez 0.256-generated implementation compiles/runs with the changed Sting processor, proving shared injected service and separate caller-valued products without product Injectable.
- [x] Factory/StingProvider/framework-integration docs and changelog explain precedence, construction validation and existing dependency matching.
- [x] tools/check.sh passes; committed evidence records the integration proof and verification limitations accurately.

## Validation

- Current focused processor suite with JSpecify/NullAway-checked generation and multistage scenarios; isolated actual-Arez javac compile/run with pinned release artifacts and no handwritten Arez implementation.
- tools/check.sh, git diff --check, source review and documentation link checks proportionate to changed docs.

## Evidence

Validated on current upstream a586a1dc with JDK 21. Focused target `bazel --output_base=/home/peter/.cache/bazel/sting-provider-factories test //processor/src/test/java/sting/processor:factory_providers --test_output=errors`: 25/25 scenarios passed using the owning Error Prone/NullAway compiler and JSpecify-positive fixtures. Generated implementation and delayed constructor dependency cases run in both processor orders; separately compiled constructors retain names with `-parameters`. Runtime checks verify the mapped implementation, distinct caller values/products and shared injected service. Qualifier capture and disabled-deferral diagnostics have focused regressions; both exposed failures before their local corrections.

Actual integration reproduction: `python3 plans/provider-backed-factories/arez-proof.py`. The evidence script in this commit builds current Sting, downloads released Arez 0.256 and runtime dependencies from Maven Central, invokes real Arez and Sting processors in both orders under the Bazel-managed JDK 21, inspects generated constructor calls, and executes both results. Both orders passed for actual Arez-generated class and interface products. The class constructor combines a shared injected backend, caller count, and nullable generated observable initializer; product classes have no Injectable annotation. Arez 0.256 advertises source version 17, so javac 21 emits that warning in both successful runs. No handwritten Arez implementation or Rose changes were used.

Arez artifact SHA256:

- arez-core-0.256.jar: e660a1564ff4f394e28661c264263de341acb3bf396be8abbc4bce866c4a5321
- arez-processor-0.256.jar: 2a0a2e9f7b23aeb68197261fe6c33d5609b6d05e8f180062c4153fd0a94f5fc8

Full `tools/check.sh` passed with a temporary PATH wrapper selecting the task's isolated Bazel output base. It checked formatting, hermeticity, Java modules, all 12 Maven artifacts/shading, isolated Java 21 and Bazel consumers, all 18 test targets, all 14 coverage targets, and the website. Coverage: 95.53% lines (4599/4814), 86.73% branches (1856/2140). Site: 2669 local file/fragment links, zero broken. Upstream protobuf emits deprecation warnings while building its tools; no repository checks failed. `git diff --check` and changed documentation relative-link checks passed. Simplification review found no additional abstraction or cleanup needed in the delivered code.

Temporary script, local wrapper and raw logs are removed with exactly this active plan tree after implementation review; this committed evidence and the PR retain the result and reproduction source.
