# Repository Guidelines

This guide helps contributors work effectively on the Sting codebase.

## User Interaction

When asked to perform a task, ask the user questions one at a time until you have enough context. Feel free to make
reasonable assumptions based on patterns present in the code and ask the user to confirm the assumptions if there are
reasonable alternatives.

## Project Structure & Module Organization

- Java modules: `core/` (runtime/annotations), `processor/` (annotation processor).
- Integration Tests: `integration-tests/`, `performance-tests/`, `downstream-test/`.
- Source code used in documentation: `doc-examples/`.
- Docs and site: `docs/`, `website/` (Docusaurus v1), `assets/`.
- Build configuration: `buildfile` (Buildr), `tasks/*.rake` (CI/site tasks).
- Source layout: `*/src/main/java/...`; tests: `*/src/test/java/...`.
- Generated binaries and build artifacts should stay out of version control and should stay untouched unless you are
  troubleshooting a local build.
- Keep `README.md` and `docs/` aligned with new features so downstream teams stay informed.

## General Principles

- Readability: Write code that is easy to read and understand. Prioritize clarity over overly clever or obscure
  solutions.
- Consistency: Strive for consistency in naming, formatting, and architectural patterns throughout the project.
- Simplicity (KISS): Keep It Simple, Stupid. Avoid unnecessary complexity.
- Don't Repeat Yourself (DRY): Avoid code duplication. Utilize functions, classes, and reusable components.
- Commenting:
    - Comment code that is complex, non-obvious, or critical.
    - Explain why something is done, not just what is being done (if the what is clear from the code).
    - Keep comments up-to-date with code changes.
- Modularity: Design components to be as self-contained and reusable as possible.
- Performance: Be mindful of performance implications, especially for real-time operations. Profile and optimize
  critical code paths.
- Error Handling: Implement robust error handling and provide clear feedback to users or logs when errors occur.

## Build, Test, and Development Commands

Prerequisites: JDK 17+, Ruby 2.7.x with Bundler, Node.js (for docs site) and Yarn.

- Bootstrap once: `bundle install` and `yarn install`.
- Build all modules: `bundle exec buildr clean package`.
- Run tests: `bundle exec buildr test`.
- CI-equivalent locally: `bundle exec buildr ci J2CL=no`.
- Docs site (optional): `bundle exec buildr site:serve` (dev), `bundle exec buildr site:build` (static output under `reports/site`).

## Bazel Build Invariants

- Keep Bazel parallel to Buildr until the migration explicitly changes that; do not remove or weaken Buildr behavior
  while adding Bazel support.
- Phase 1 Bazel coverage is limited to building and testing `core`, `doc-examples`, `integration-tests`, `processor`,
  `server`, and `server-integration-tests`.
- Keep Java 17 and preserve Sting's current strict compiler stance. Bazel Java macros should use `-Werror`,
  `-Xlint:all,-processing,-serial`, Error Prone, and the formatter `--add-exports` where annotation processing needs
  them.
- Do not adopt NullAway or JSpecify as part of the Bazel migration. Sting currently uses `javax.annotation`
  `@Nonnull`/`@Nullable`; keep that model unless a separate migration is requested.
- Compile tests with the same strict options as production code, including Error Prone. If this causes excessive source
  churn, stop and ask before introducing a less strict test-specific rule.
- Keep TestNG as the test framework. Use a TestNG macro strategy similar to
  `/Users/peter/Code/stocksoftware/replicant/third_party/java/rules.bzl`.
- Use coarse module-level Bazel targets by default, matching the existing Buildr modules. Prefer finer-grained targets
  only when a specific module genuinely needs them.
- Place Bazel test targets in packages with the corresponding test sources. Prefer one TestNG target per test source
  package/module unless a specific test class needs its own target.
- Mirror the dependency versions in `build.yaml` for Bazel's `third_party/java/dependencies.yml` unless Bazel, Java 17,
  or Error Prone requires a specific version exception.
- `third_party/java/BUILD.bazel` may contain a small hand-written section above the depgen-generated block for stable
  aliases and aggregate targets. Keep depgen-owned content generated and avoid hand-editing inside generated blocks.
- Model processor test fixtures as Bazel `data` and pass `sting.fixture_dir` through a runfiles-aware path. Do not copy
  fixtures into the source tree or rely on Buildr's processor-test working-directory layout.
- Bazel targets must not compile existing `*/generated/**` directories. Treat those directories as local Buildr output
  artifacts and let Bazel produce generated sources under Bazel outputs.
- Use Bazel `9.1.1` with Bzlmod, `rules_java 9.6.1`, Java/tool Java 17, strict Java deps, explicit Java test deps,
  disabled Java header compilation, and a peer-directory symlink prefix adapted for Sting.
- Model Sting annotation processing explicitly with a `java_plugin` target. Bazel targets that need generated Sting code
  should list the processor plugin directly rather than relying on service discovery from runtime dependencies.
- Add a Phase 1 coverage gate for processor and server code only, using only the processor and server unit-test
  targets. Establish the initial line/branch thresholds from the first passing Bazel coverage report, rounding down
  slightly to create a stable baseline; do not add tests merely to raise the initial gate.
- Travis CI is disabled legacy infrastructure. Do not add Travis configuration or Travis-specific build behavior; use
  GitHub Actions for new CI work.
- The Phase 1 GitHub Actions workflow should run the Bazel verification script only (`tools/check.sh`). Do not fold
  Buildr site/deploy/release behavior into this workflow.
- `tools/check.sh` should run `tools/update_java_deps.sh` first and fail if depgen-generated Bazel files are stale.
- For Phase 1, the Bazel annotation processor target only needs to support build/test workflows. Do not recreate
  Buildr's shaded release `processor` jar unless release packaging becomes part of the requested scope.
- Keep Phase 1 Bazel documentation internal/contributor-facing. Do not add end-user Bazel setup docs until Bazel
  becomes part of published consumer setup or release packaging.
- Java source formatting may be introduced as part of the Bazel tooling, but the repository-wide formatting changes
  must be committed separately before committing the formatter tooling/check.
- Java formatting should cover maintained Java source/test code plus `processor/src/test/fixtures/bad_input/**` and
  `processor/src/test/fixtures/input/**`. Do not format expected-output fixture trees unless a fixture update is
  deliberately required and verified by processor tests.
- For Phase 1 Bazel, include `core/src/main/java/sting/Sting.gwt.xml` as a `core` resource, but do not reproduce
  Buildr's GWT compile/enhance behavior or GWT classifier packaging.
- Use `/Users/peter/Code/realityforge/jdbt` as the primary Bazel example for Bzlmod, `tools/` scripts, depgen-managed
  third-party dependencies, CI workflow shape, buildifier, Java formatting, and coverage gating.

## Coding Style & Naming Conventions

- Language: Java 17; compilation uses `-Xlint:all` and `-Werror` (warnings must be fixed).
- Indentation: 2 spaces; braces on a new line for types/methods; keep imports ordered and minimal.
- Annotations: prefer `@Nonnull`/`@Nullable`; use `final` where practical.
- Naming: packages lowercase (`sting.*`), classes `PascalCase`, methods/fields `camelCase`, constants `UPPER_SNAKE_CASE`.
- Public API must include Javadoc; keep package-level docs in `package-info.java`.

## Testing Guidelines

- Framework: TestNG across modules.
- Location: place tests under `*/src/test/java`; integration tests live in `integration-tests/src/test/java`.
- Naming: suffix unit tests with `Test` and integration tests with `IntegrationTest`.
- Run all tests with `bundle exec buildr test` before submitting.

## Commit & Pull Request Guidelines

- Follow `CONTRIBUTING.md` and the Code of Conduct.
- Commits: small, focused, imperative subject; reference issues where relevant; update `CHANGELOG.md` for user-visible changes.
- PRs: include a clear description, linked issues, tests for behavior, and docs updates if APIs change. Add screenshots or generated artifacts when helpful.

## Security & Configuration Tips (Optional)

- Never commit secrets. CI uses encrypted `etc/secrets`; maintainers handle deployment keys.
- Release-related env vars: `PRODUCT_VERSION`, `PREVIOUS_PRODUCT_VERSION`; for quicker local cycles set `J2CL=no`.

## Processor Integration Notes

- Meta-annotation detection by simple name is intentional. The processor checks for annotations whose meta-annotations have the simple names `StingProvider`, `ActAsStingConsumer`, `ActAsStingProvider`, `ActAsStingComponent`, and `InterceptorBinding` (without requiring FQNs). This enables third-party frameworks to define their own integration annotations (often package-private) without adding a direct dependency on Sting.
- `StingProvider` is for include resolution and auto-discovery. `InterceptorBinding` is for interceptor binding discovery. `ActAsStingConsumer`, `ActAsStingProvider`, and `ActAsStingComponent` are validation-only hooks.
- Do not “fix” this to FQN/type checks; it is a deliberate extension point. If adding new integration hooks, document their simple names here and keep the behavior consistent.
