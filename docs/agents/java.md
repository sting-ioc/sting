# Java conventions

- Processor internals use JSpecify: `@NullMarked` packages and `@Nullable` where needed. Generated code, public examples, and annotation-processor input fixtures may still use `javax.annotation`.
- Prefer `final` for variables and parameters that are not reassigned.
- Document public APIs with Javadoc and package-level documentation in `package-info.java`.
