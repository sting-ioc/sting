# Java conventions

- Use JSpecify in owned code and positive fixtures: `@NullMarked` packages or types, with type-use `@Nullable` where needed. Generated bodies must pass NullAway JSpecify mode and the enabled Error Prone checks.
- Prefer `final` for variables and parameters that are not reassigned.
- Document public APIs with Javadoc and package-level documentation in `package-info.java`.
