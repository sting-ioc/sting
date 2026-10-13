# Testing

- Use TestNG across modules. Suffix unit test classes with `Test` and integration test classes with `IntegrationTest`.
- Run the affected Bazel test target first during an edit cycle.
- Regenerate one processor golden family with `tools/update_goldens.sh <target>` after a deliberate generated-output change. Review the fixture diff; see [Build and test](../development_process.md#build-and-test) for the golden workflow.
