# Build rules

- List source files explicitly in Bazel targets; `glob()` is prohibited.
- Each source directory owns its `BUILD.bazel`. Targets may list sources only from that directory, not from child, sibling, or parent directories.
- Keep generated binaries and build artifacts out of version control; edit them only when troubleshooting a local build.
