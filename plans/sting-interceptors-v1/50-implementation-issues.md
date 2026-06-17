# Sting Interceptors v1 Implementation Issues

Status: implemented

## Issue Register

### PI-01: Interceptor proxy methods omit whitelisted source annotations

Status: resolved

Context and evidence:

- `integration-tests/src/test/java/sting/integration/InterceptorsIntegrationTest.java` declares `MyService.ok(...)` with `@Nonnull` on the return value and parameter.
- The generated proxy method in `integration-tests/generated/processors/test/java/sting/integration/Sting_sting_integration_InterceptorsIntegrationTest_MyServiceImpl_MyService_InterceptorProxy.java` currently emits `public String ok(String name)` without those annotations.
- Existing generated variants explicitly copy whitelisted annotations with `GeneratorUtil.copyWhitelistedAnnotations(...)`, including in `FactoryGenerator` and `FragmentGenerator`.
- `InterceptorProxyGenerator` currently builds proxy override methods through `MethodSpec.overriding(...)`. The JavaPoet source for this version states that overridden method and parameter annotations must be added separately.

Why it matters:

- Generated interceptor proxies should preserve the same public contract annotations as other Sting generated variants.
- Static analysis, generated-source consumers, and IDE/nullness tooling can observe proxy method signatures directly.
- The omission is silent because Java override compatibility does not require repeating nullability annotations.

Implemented response:

- `InterceptorProxyGenerator` now builds interceptor proxy service methods through `GeneratorUtil.overrideMethod(processingEnv, serviceElement, method)`.
- The implementation therefore uses the existing Sting/Proton whitelisted annotation-copying path for method and parameter annotations.
- Copied annotations remain limited to `GeneratorUtil.ANNOTATION_WHITELIST`: `@Nonnull`, `@Nullable`, and `@Deprecated`.
- Source `@SuppressWarnings` is not copied; generated suppressions remain synthesized only when required.
- Processor generated-source assertions and expected proxy fixtures now cover the copied annotation output.
- Existing integration generated output was inspected directly as additional evidence.

Tracking tasks:

- `POST-ANN-APPROVAL`
- `POST-ANN-FIX`
- `POST-ANN-TEST`
- `POST-ANN-GATE`

Validation evidence:

```bash
bundle exec buildr sting:processor:test
```

Passed: 492 tests, 492 passes, 0 failures.

```bash
bundle exec buildr sting:integration-tests:test
```

Passed: 46 tests, 46 passes, 0 failures.

```bash
rg -n "@Nonnull|@Nullable|public String ok|final String name" \
  integration-tests/generated/processors/test/java/sting/integration/Sting_sting_integration_InterceptorsIntegrationTest_MyServiceImpl_MyService_InterceptorProxy.java
```

Confirmed the regenerated proxy contains `public String ok(@Nonnull final String name)` with the copied return annotation.

```bash
bundle exec buildr ci J2CL=no
```

Passed in 5m54.940s. The gate completed processor, integration, downstream, documentation, Javadoc, and site link-check validation. Downstream tests reported 4 passes, and the site link check reported 2913 links found, 2756 excluded, 0 broken.

```bash
git diff --check
```

Passed with no whitespace errors.

Residual risk:

- Switching helper paths may change generated formatting or add `final` to parameters. This is acceptable if confined to generated proxy service methods and backed by updated fixtures.

Approval status:

- User approved implementation with instruction: `implement`.
- Implementation is validated and the user requested commit creation.
