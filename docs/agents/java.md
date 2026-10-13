# Java conventions

- Use JSpecify throughout owned code and positive fixtures: `@NullMarked` packages/types and type-use `@Nullable` where needed. Generated bodies must pass NullAway JSpecify mode. Keep legacy nullability annotation spellings only in migration diagnostics and deliberate rejection tests.
- Prefer `final` for variables and parameters that are not reassigned.
- Document public APIs with Javadoc and package-level documentation in `package-info.java`.
