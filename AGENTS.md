# Repository Guidelines

This guide helps contributors work effectively on the Sting codebase.

## Project Structure & Module Organization

- Java modules: `core/` (runtime/annotations), `processor/` (annotation processor), `server/` (server integrations).
- Integration tests: `integration-tests/`, `server-integration-tests/`.
- Source code used in documentation: `doc-examples/`.
- Docs and site: `docs/`, `website/` (Docusaurus v1), `assets/`.
- Build configuration: `MODULE.bazel`, directory-owned `BUILD.bazel` files, `third_party/java/`, and `tools/`.
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

Prerequisites: Bazel 9.3.0, JDK 17+, Node 24 and npm for the docs site.

- Bootstrap site dependencies: `npm ci`.
- Build Java modules: `bazel build //core //processor //server //doc-examples`.
- Run tests: `bazel test //:all_tests`.
- CI-equivalent locally: `tools/check.sh`.
- Regenerate one processor golden family: `tools/update_goldens.sh <target>`.
- Docs site: `tools/site.sh serve` (dev), `tools/site.sh build` (static output under `reports/site`).

## Bazel Build Invariants

- Bazel targets must list source files explicitly; do not use `glob()`.
- Each source directory owns its own `BUILD.bazel`; targets must not list source files from child, sibling, or parent directories.

## Coding Style & Naming Conventions

- Annotations: processor internals use JSpecify (`@NullMarked` packages and `@Nullable` only where needed);
  generated code, public examples, and annotation-processor input fixtures may still use `javax.annotation`.
  Use `final` where practical.
- Naming: packages lowercase (`sting.*`), classes `PascalCase`, methods/fields `camelCase`, constants `UPPER_SNAKE_CASE`.
- Public API must include Javadoc; keep package-level docs in `package-info.java`.

## Testing Guidelines

- Framework: TestNG across modules.
- Location: place tests under `*/src/test/java`; integration tests live in `integration-tests/src/test/java`.
- Naming: suffix unit tests with `Test` and integration tests with `IntegrationTest`.
- Run `tools/check.sh` before submitting. For a quick edit cycle, run the affected Bazel test target first.

## Commit & Pull Request Guidelines

- Follow `CONTRIBUTING.md` and the Code of Conduct.
- Commits: small, focused, imperative subject; reference issues where relevant; update `CHANGELOG.md` for user-visible changes.
- PRs: include a clear description, linked issues, tests for behavior, and docs updates if APIs change. Add screenshots or generated artifacts when helpful.

## Security & Configuration Tips (Optional)

- Never commit secrets. Maintainers provide signing and publishing credentials when running a release.
- Releases use an explicit version and the scripts under `tools/release/`; see `docs/development_process.md`.

## Processor Integration Notes

- Meta-annotation detection by simple name is intentional. The processor checks for annotations whose meta-annotations have the simple names `StingProvider`, `ActAsStingConsumer`, `ActAsStingProvider`, `ActAsStingComponent`, and `InterceptorBinding` (without requiring FQNs). This enables third-party frameworks to define their own integration annotations (often package-private) without adding a direct dependency on Sting.
- `StingProvider` is for include resolution and auto-discovery. `InterceptorBinding` is for interceptor binding discovery. `ActAsStingConsumer`, `ActAsStingProvider`, and `ActAsStingComponent` are validation-only hooks.
- Do not “fix” this to FQN/type checks; it is a deliberate extension point. If adding new integration hooks, document their simple names here and keep the behavior consistent.
