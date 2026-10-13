# Sting

Sting is a compile-time dependency injection toolkit for Java.

Use the Bazel version pinned in [.bazelversion](.bazelversion) and JDK 17 or newer.

- Build Java modules: `bazel build //core //processor //server //doc-examples`.
- Run all tests: `bazel test //:all_tests`.
- Before submitting changes, run the full check: `tools/check.sh`.

Read the applicable guidance before working:

- Bazel targets or build artifacts: [build rules](docs/agents/build.md).
- Java source or public APIs: [Java conventions](docs/agents/java.md).
- Tests or processor goldens: [testing](docs/agents/testing.md).
- Annotation processor changes: [integration hooks](docs/agents/processor.md).
- New features, documentation, or website changes: [documentation and site](docs/agents/documentation.md).
- Commits, pull requests, user-visible changes, or releases: [contribution workflow](docs/agents/workflow.md).
