# Sting Interceptors v1 Implementation Plan

Status: accepted

## Delivery Approach

Implement v1 in narrow, testable slices. Preserve all existing non-intercepted behavior first, then add interception metadata, validation, graph wiring, proxy generation, and documentation. Keep generated code direct and allocation-conscious.

Required full gate:

```bash
bundle exec buildr ci J2CL=no
```

Optional compatibility gate:

```bash
bundle exec buildr ci
```

## Phase Sequence

### Phase 1: Public API Annotations

Files:

- `core/src/main/java/sting/interceptors/InterceptorBinding.java`
- `core/src/main/java/sting/interceptors/Before.java`
- `core/src/main/java/sting/interceptors/After.java`
- `core/src/main/java/sting/interceptors/AfterException.java`
- `core/src/main/java/sting/interceptors/ServiceType.java`
- `core/src/main/java/sting/interceptors/MethodName.java`
- `core/src/main/java/sting/interceptors/BindingValue.java`
- `core/src/main/java/sting/interceptors/Arguments.java`
- `core/src/main/java/sting/interceptors/Result.java`
- `core/src/main/java/sting/interceptors/Thrown.java`
- `core/src/main/java/sting/interceptors/package-info.java`

Implementation notes:

- Add Javadocs explaining no runtime annotation lookup and v1 exclusions.
- Put every public interceptor annotation in package `sting.interceptors`; do not add root-package aliases or duplicate root `sting` annotations in v1.
- Use `@Retention(CLASS)` for `InterceptorBinding`.
- Use parameter/method annotation targets precisely.
- Add fully qualified and simple-name constants to `processor/src/main/java/sting/processor/Constants.java`, following the existing simple-name integration-hook pattern.

Validation:

- Compile annotation API.
- Add bad-input fixture if annotation targets need processor diagnostics beyond Java target errors.

### Phase 2: Interceptor Metadata Model

Files:

- `processor/src/main/java/sting/processor/InterceptorBindingDescriptor.java`
- `processor/src/main/java/sting/processor/InterceptorDescriptor.java`
- `processor/src/main/java/sting/processor/InterceptorMethodDescriptor.java`
- `processor/src/main/java/sting/processor/InterceptedServiceDescriptor.java`
- `processor/src/main/java/sting/processor/InterceptorProxyDescriptor.java`
- `processor/src/main/java/sting/processor/ProxyNode.java` or an equivalent synthetic-node representation
- `processor/src/main/java/sting/processor/Binding.java`
- `processor/src/main/java/sting/processor/ServiceSpec.java`
- `processor/src/main/java/sting/processor/Registry.java`
- `processor/src/main/java/sting/processor/Node.java`
- `processor/src/main/java/sting/processor/ComponentGraph.java`
- `processor/src/main/java/sting/processor/InjectorDotReportGenerator.java`

Implementation notes:

- Model effective bindings per `Binding + ServiceSpec`.
- Detect annotations whose annotation type has a meta-annotation with simple name `InterceptorBinding`, while building binding metadata. This intentionally follows the existing `StingProvider` and `ActAsSting*` integration-hook policy rather than requiring the `sting.interceptors.InterceptorBinding` FQN. Phase 2 extracts descriptors only; claim-dependent validation and generic interceptor resolution happen in later phases.
- Treat `ServiceSpec.getCoordinate()` as the full service identity, including qualifier. Every proxy descriptor key, generated proxy/cache name, duplicate check, graph edge, JSON descriptor, and dot report entry must preserve the qualifier.
- Record service-interface annotation bindings and binding-source annotation bindings separately, then merge.
- Store priority, binding annotation type, binding annotation mirror/value map, optional `implementedBy`, and plugin claim state.
- Represent binding annotation member values through a Sting-owned value model. Scalar values are available to plugins and generic lifecycle validation; array-valued and nested annotation-valued members are recorded as unsupported for v1 metadata use.
- Do not store runtime annotation instances.
- Add a proxy descriptor keyed by `binding id + service coordinate`, with generated-state tracking so repeated injectors do not emit duplicate proxy source files.
- Add a graph-visible synthetic proxy node model keyed by `binding id + service coordinate`. The proxy node references the raw target node and dependencies for each generic interceptor implementation. Intercepted service coordinates must resolve to proxy nodes; raw target nodes remain available only for target/proxy construction.
- Refactor graph provider nodes to be kind-aware:
  - injector root node remains a root-only node
  - binding nodes wrap a real `Binding` and keep existing binding id semantics
  - proxy nodes wrap an `InterceptorProxyDescriptor` and must not require `getBinding()`
- Replace binding-only graph assumptions with provider-node accessors where needed. Current hotspots include JSON serialization `supportedBy` ids, published service maps, dot labels, graph ordering, eager propagation, and include-root validation.
- Use stable proxy node ids derived from `proxy + binding id + full service coordinate`. The exact string must be deterministic, file-system/source safe where reused, and independent of processing order.
- JSON serialization for proxy nodes must write `kind: "PROXY"`, `id`, full service coordinate, raw target node id, generic interceptor node ids, eager flag when applicable, and dependency edges whose `supportedBy` arrays contain provider node ids rather than only binding ids.
- Dot reports must label proxy nodes distinctly, include service coordinate qualifier text, and render dependency edges to raw target and interceptor nodes.
- Include-root unused validation must consider raw target and interceptor bindings as used when reachable only through proxy nodes, while synthetic proxy nodes themselves are not include roots and cannot satisfy user include-root accounting directly.
- Ensure proxy nodes participate in node naming, eager propagation, circular dependency checks, JSON object graph descriptors, and dot reports.
- Preserve existing graph registration for non-intercepted services.

Validation:

- Unit fixtures for extracting service-interface plus implementation/provider binding descriptors.
- Fixture proving duplicate binding candidates remain visible to Phase 4 validation.
- JSON and dot expected-output fixtures for one proxy node, including stable id, `kind: "PROXY"`, service coordinate, target node id, interceptor node ids, and provider-node `supportedBy` references.
- Fixture proving include-root unused validation treats proxy-reachable raw target and interceptor bindings as used without counting proxy nodes as include roots.

### Phase 3: Plugin SPI And Claim Resolution

Files:

- `processor/src/main/java/sting/processor/InterceptorCodeGenerator.java`
- `processor/src/main/java/sting/processor/InterceptorBindingModel.java`
- `processor/src/main/java/sting/processor/InterceptedMethodModel.java`
- `processor/src/main/java/sting/processor/LifecycleCodeEmitter.java` or equivalent
- `processor/src/main/java/sting/processor/StingProcessor.java`
- test service registration resources under test fixtures as needed

Implementation notes:

- Discover plugins via `ServiceLoader<InterceptorCodeGenerator>` during processor initialization.
- Let plugins claim effective interceptor binding descriptors before any generic `implementedBy` resolution.
- Claim key is `binding id + full service coordinate + binding annotation type + normalized binding annotation value map`.
- Fail if multiple plugins claim the same effective descriptor.
- If a plugin claims an effective descriptor, skip generic interceptor resolution for that descriptor.
- If no plugin claims an effective descriptor and `implementedBy` is empty, fail for that descriptor.
- Claim state is stored per effective descriptor as unclaimed, claimed by plugin id, or conflict with plugin ids. The same binding annotation on different service coordinates has independent claim state.
- Plugin id is `plugin.getClass().getCanonicalName()`, falling back to `plugin.getClass().getName()` only when the canonical name is unavailable. Diagnostics and tests must not use plugin `toString()` or ServiceLoader file order as identity.
- Keep v1 plugin API lifecycle-only: before/after/afterException code emission, no proceed.
- Public SPI signatures must not expose shaded `com.palantir.javapoet` or `org.realityforge.proton` types because the processor jar relocates those packages.
- V1 plugins must not add graph/runtime dependencies. They emit dependency-free direct lifecycle snippets through Sting-owned model/emitter interfaces. Runtime collaborators require the generic `implementedBy` interceptor path.
- Define the public SPI before implementing generic interceptor resolution. Minimum contract:

  ```java
  public interface InterceptorCodeGenerator {
    boolean supports(InterceptorBindingModel binding);

    void emitBefore(InterceptedMethodModel method,
                    InterceptorBindingModel binding,
                    LifecycleCodeEmitter emitter);

    void emitAfter(InterceptedMethodModel method,
                   InterceptorBindingModel binding,
                   LifecycleCodeEmitter emitter);

    void emitAfterException(InterceptedMethodModel method,
                            InterceptorBindingModel binding,
                            LifecycleCodeEmitter emitter);
  }

  public interface LifecycleCodeEmitter {
    String serviceType();

    String methodName();

    String bindingValue(String name);

    String argument(int index);

    String argumentsArray();

    String result();

    String thrown();

    void emitStatement(String javaStatement);
  }

  public interface InterceptorBindingModel {
    String annotationTypeName();

    int priority();

    String serviceTypeName();

    String qualifierKey();

    java.util.Set<String> valueNames();

    BindingValueModel value(String name);
  }

  public interface BindingValueModel {
    String name();

    BindingValueKind kind();

    Object scalarValue();

    String className();

    String enumTypeName();

    String enumConstantName();

    String javaLiteral();
  }

  public enum BindingValueKind {
    STRING,
    BOOLEAN,
    BYTE,
    SHORT,
    INT,
    LONG,
    FLOAT,
    DOUBLE,
    CHAR,
    ENUM,
    CLASS,
    UNSUPPORTED
  }

  public interface InterceptedMethodModel {
    String methodName();

    String returnTypeName();

    java.util.List<String> parameterTypeNames();

    java.util.List<String> thrownTypeNames();

    boolean defaultMethod();

    boolean varArgs();
  }
  ```

- `emitStatement` accepts a complete Java statement and requires fully qualified type names where a type reference is needed. Plugins cannot add imports.
- `argumentsArray()`, `result()`, and `thrown()` are legal only in lifecycle phases where the corresponding metadata is available; invalid calls fail with plugin diagnostics.
- `qualifierKey()` returns an empty string for unqualified services. `scalarValue()` returns boxed primitives or `String`; class and enum metadata use the dedicated name/constant accessors. `javaLiteral()` returns the source literal Sting would emit for supported scalar values and fails for `UNSUPPORTED`.
- The SPI must not expose extension points for imports, fields, helper methods, constructor parameters, graph dependencies, or runtime service requests.
- Add processor-path ServiceLoader test setup that proves plugin discovery works from test fixtures.

Validation:

- Test plugin-only binding success.
- Test multiple plugin claim failure.
- Test empty `implementedBy` without plugin failure.
- Test plugin and `implementedBy` coexistence where plugin wins for code generation.
- Test same binding annotation on two service coordinates where plugin claim state remains independent.
- Test multiple plugins claiming one effective descriptor while another descriptor for the same annotation is unaffected.
- Assert multiple-claim diagnostics include the stable plugin implementation class names.
- Test plugin-only binding where one effective service coordinate is claimed and another is unclaimed, producing a diagnostic only for the unclaimed descriptor.
- Test that plugin API examples do not require shaded JavaPoet/Proton imports.
- Compile an external-style test plugin against only the public SPI. The plugin must claim a specific binding through `InterceptorBindingModel` getters and emit method-specific code through `InterceptedMethodModel` getters.

### Phase 4: Binding Usage Validation

Files:

- `processor/src/main/java/sting/processor/StingProcessor.java`
- `processor/src/main/java/sting/processor/Constants.java`
- processor bad-input fixtures under `processor/src/test/fixtures/bad_input/...`
- processor expected error assertions in `processor/src/test/java/sting/processor/StingProcessorTest.java`

Implementation notes:

- Consume effective interceptor binding descriptors extracted in Phase 2.
- Validate only Sting-reachable binding usages.
- Run this validation after Phase 3 plugin claim resolution so plugin-only bindings can be distinguished from missing generic implementations.
- Reject binding annotation types that combine `@InterceptorBinding` with `@Retention(SOURCE)`. Accept missing `@Retention`, `@Retention(CLASS)`, and `@Retention(RUNTIME)`.
- For simple-name `InterceptorBinding` hooks, require a compatible `int priority()` member and optional `String implementedBy()` member. The core `sting.interceptors.InterceptorBinding` annotation gets Java-level enforcement; third-party simple-name hooks get processor diagnostics.
- Validate `implementedBy` string syntax before type resolution. Accept canonical dotted qualified names, including nested dotted names such as `com.acme.Outer.Inner`; reject binary names containing `$`.
- Valid binding usage locations:
  - service interface type published by `@Typed`
  - `@Injectable` implementation class
  - `@Fragment` provider method
- Invalid v1 usage locations:
  - service interface methods
  - implementation methods
  - arbitrary method not enclosed by `@Fragment`
  - non-service published types on intercepted bindings
  - injector input services
  - nullable provider bindings
- For intercepted bindings, require all nonzero `@Typed` values to be interfaces.
- Reject `Object.class`, concrete classes, primitives, arrays, parameterized service types, and zero-type eager-only interception.
- Reject intercepted service interfaces that declare type parameters.
- Reject intercepted service methods that declare type parameters.
- Reject intercepted `Binding.Kind.INPUT` services. Inputs can still use service interfaces that have interceptor annotations elsewhere only when that input coordinate is not selected for interception; if the input coordinate's effective service-interface bindings would require interception, fail with an input-interception unsupported diagnostic.
- Reject intercepted optional/nullable bindings. Optional requests for a required intercepted binding still receive `Optional.of(proxy)`; absent optional requests remain absent.
- Support factory-generated injectable bindings published as factory service interfaces. Bindings on the factory interface are service-interface bindings and are effective when the generated factory implementation publishes that interface.
- Reject duplicate effective binding annotation types for one service interface.
- Reject equal priorities for one service interface.
- Reject plugin-only bindings with empty `implementedBy` when no plugin claimed the binding in Phase 3.
- Reject binding annotations missing required Java-level `priority()` by relying on Java compile errors.

Validation:

- Bad fixtures for method-level binding, duplicate binding type, duplicate priority, concrete/Object published type, missing plugin for empty `implementedBy`, invalid provider method usage, input interception, nullable intercepted provider binding, generic service interface, generic service method, `@Retention(SOURCE)` binding annotation, binary-name `implementedBy`, and simple-name `InterceptorBinding` hook with incompatible members.
- Good fixtures for qualified intercepted services, factory-generated injectable services, and third-party simple-name `InterceptorBinding` integration hooks.
- Good fixture for a nested interceptor implementation referenced by canonical dotted `implementedBy`.

### Phase 5: Interceptor Implementation Resolution

Files:

- `processor/src/main/java/sting/processor/StingProcessor.java`
- `processor/src/main/java/sting/processor/Registry.java`
- `processor/src/main/java/sting/processor/ComponentGraph.java`
- `processor/src/main/java/sting/processor/Binding.java`

Implementation notes:

- For each generic binding, resolve `implementedBy` as a `TypeElement`.
- Require effectively public `@Injectable` class.
- Derive/register the interceptor injectable if not already registered.
- Validate interceptor class lifecycle methods:
  - lifecycle annotations are considered only on methods declared directly by the interceptor implementation class
  - inherited lifecycle annotations are unsupported in v1
  - an override of a lifecycle-annotated base method must redeclare a lifecycle annotation directly if it is intended to be a lifecycle method
  - public instance method only
  - `void` return type
  - no type parameters
  - no checked thrown types
  - exactly one lifecycle annotation per lifecycle method
  - at most one `@Before`, one `@After`, one `@AfterException`
  - at least one lifecycle method
  - supported parameter markers and marker placement
- Validate every lifecycle method parameter against this marker matrix:

  | marker | phases | type |
  | --- | --- | --- |
  | `@ServiceType` | before, after, afterException | `String` |
  | `@MethodName` | before, after, afterException | `String` |
  | `@BindingValue` | before, after, afterException | conversion-compatible member type |
  | `@Arguments` | before, after, afterException | `Object[]` |
  | `@Result` | after only | `Object` |
  | `@Thrown` | afterException only | `Throwable` |

- Reject unannotated lifecycle parameters, more than one marker annotation on one parameter, wrong marker parameter types, and marker annotations used outside their allowed phase.
- Validate `@BindingValue` mappings:
  - `String` member to `String`
  - primitive member to same primitive or boxed type
  - enum member to `String` constant name
  - class-valued member to fully qualified class name `String`
  - default member values included
  - array-valued and nested annotation-valued members rejected
- Attach generic interceptor implementation dependencies to synthetic proxy nodes so graph ordering, eager propagation, and circular dependency checks remain coherent.
- Detect and report circular dependencies involving interceptors through existing graph cycle checks. A required dependency path `proxy -> interceptor -> intercepted service coordinate -> proxy` must fail as a cycle. Supplier-boundary paths follow the existing graph cycle semantics and must still provide the proxy when invoked.

Validation:

- Bad fixtures for non-public interceptor class, non-public lifecycle method, static/private/protected lifecycle method, inherited lifecycle annotation, unannotated override of an inherited lifecycle annotation, inherited lifecycle plus declared duplicate phase, non-void lifecycle method, lifecycle method with type parameter, method with multiple lifecycle annotations, checked lifecycle exception, unsupported marker, unannotated lifecycle parameter, multiple marker annotations on one parameter, wrong marker parameter types for every marker, marker used outside its legal phase, invalid `@BindingValue` conversion, duplicate lifecycle phase, and empty interceptor.
- Good fixtures for auto-included interceptor with constructor dependency and subclass override that redeclares the lifecycle annotation directly.
- Bad fixture for `proxy -> interceptor -> intercepted service -> proxy` cycle.
- Supplier-boundary fixture for interceptor dependency on `Supplier<intercepted service>` that follows existing cycle semantics and returns a proxy.

### Phase 6: Proxy Generation

Files:

- `processor/src/main/java/sting/processor/InterceptorProxyGenerator.java`
- `processor/src/main/java/sting/processor/InjectorGenerator.java`
- `processor/src/main/java/sting/processor/StingGeneratorUtil.java`
- `processor/src/main/java/sting/processor/ComponentGraph.java`
- `processor/src/main/java/sting/processor/Node.java`

Implementation notes:

- Generate one proxy class per intercepted `Binding + ServiceSpec`.
- Place proxy in service interface package or otherwise preserve access to package-access service interfaces.
- Name proxies deterministically using binding owner plus service interface simple/flat name.
- Register each proxy descriptor as generated before emitting source so subsequent injectors/rounds do not attempt duplicate source creation.
- Proxy constructor takes:
  - service interface target
  - required interceptor component instances for generic interceptors
- For inaccessible service types, expose a public static factory in the service package that accepts the target as `Object` and any inaccessible wrappers as raw types, casts inside the service package, and returns `Object` when the service type is not publicly nameable from the caller.
- V1 plugin fast paths cannot add constructor dependencies.
- Proxy methods:
  - implement all callable service-interface instance methods, including inherited and default methods
  - delegate to `target.method(...)`
  - exclude static/private interface methods and implicit `Object` methods
  - support overloaded methods, varargs methods, and explicitly redeclared `Object` methods
- Generate nested try/catch lifecycle structure so outer interceptors observe inner failures. The generated code must match the requirements template: an interceptor's own `@AfterException` does not catch its own `@Before` or `@After` failure; outer interceptors catch inner lifecycle and target failures.
- Preserve declared service method checked exceptions.
- Run `@Before` outer-to-inner.
- Run `@After` and `@AfterException` inner-to-outer.
- Box primitives for `@Result` only when some effective interceptor requests result.
- Create `Object[] arguments` only when some effective interceptor requests `@Arguments`.
- Reuse one arguments array across lifecycle calls for one method invocation.
- Pass `null` result for void methods only when `@Result` is requested.
- Do not emit suppressed-exception handling.

Validation:

- Expected generated-source fixtures for simple before, before/after, exception, result, arguments, primitive result, void result, checked exception, default method, inherited method, and lifecycle failure nesting.
- Expected generated-source fixtures for own-`@Before` failure and own-`@After` failure, proving that the same interceptor's `@AfterException` is not called while outer interceptors still observe those failures.
- Expected generated-source fixtures for overloaded methods, varargs methods, and explicitly redeclared `Object` methods.
- Expected generated-source fixtures for cross-package package-access service interface proxy construction through the bridge factory.
- Fixture with two injectors including the same intercepted binding proves proxy source emission is deduplicated.

### Phase 7: Injector Integration And Request Substitution

Files:

- `processor/src/main/java/sting/processor/InjectorGenerator.java`
- `processor/src/main/java/sting/processor/ComponentGraph.java`
- `processor/src/main/java/sting/processor/Edge.java`
- `processor/src/main/java/sting/processor/Node.java`

Implementation notes:

- Add cached proxy fields in injector implementation, one per intercepted service coordinate per binding.
- Access cached proxies through synthetic proxy nodes rather than overloading raw target nodes.
- For eager intercepted target nodes, initialize raw target, cached proxy nodes for every intercepted published service interface, and generic interceptor dependencies in the injector constructor.
- If an eager graph path reaches a synthetic proxy node, mark the proxy node, raw target node, and generic interceptor dependency nodes eager unless the path crosses an existing supplier boundary.
- Substitute proxy accessors for intercepted service requests in:
  - instance
  - optional
  - supplier
  - supplier optional
  - collection
  - supplier collection
  - supplier optional collection
- Preserve collection caching behavior.
- Keep raw target node available internally for target construction and service proxy construction.
- Do not expose raw target for intercepted service coordinates.
- Ensure graph JSON and dot report output make proxy nodes distinguishable from raw target nodes and show interceptor dependencies.

Validation:

- Integration tests for every `ServiceRequest.Kind`: direct output, optional, supplier, supplier optional, collection, supplier collection, and supplier optional collection.
- Integration tests for constructor dependency and fragment provider dependency request paths.
- Integration test proving service interface identities may differ while both delegate to same target.
- Integration tests for two qualified services with the same service type but different `@Named` qualifiers.
- Integration test for an intercepted factory-generated service interface.
- Integration test proving eager intercepted services construct target, proxy, and generic interceptor dependencies during injector creation.
- Graph descriptor tests proving proxy nodes include target and interceptor dependencies.

### Phase 8: Documentation And Compatibility Notes

Files:

- `README.md`
- `docs/`
- `CHANGELOG.md`
- `AGENTS.md`
- optional doc examples under `doc-examples/`

Implementation notes:

- Document v1 service-interface-only model.
- Document binding locations and duplicate/priority rules.
- Document lifecycle method signature rules and parameter markers.
- Document no `@Around`, no async completion, no method-level bindings, no argument rewriting.
- Document plugin SPI at a high level if public.
- Update examples to use type-level service and provider bindings.
- Ensure all docs and examples import interceptor annotations from `sting.interceptors`.
- Update processor integration notes in `AGENTS.md` to include the `InterceptorBinding` simple-name meta-annotation hook.

Validation:

- Documentation examples compile as part of full gate.

### Phase 9: Full Validation And Cleanup

Files:

- Test fixtures and snapshots across processor/integration/doc examples.

Implementation notes:

- Run targeted processor tests while iterating.
- Run required full gate.
- Inspect generated source snapshots for debug artifacts or inconsistent formatting.
- Ensure no temporary test files or debug logging remain.

Validation:

```bash
bundle exec buildr ci J2CL=no
```

## High-Risk Areas And Mitigations

- Risk: proxy package placement breaks package-access service interfaces.
  - Mitigation: add package-access service fixture and expected generated proxy in service package.
- Risk: lifecycle try/catch generation mishandles checked exceptions.
  - Mitigation: fixture with declared checked exception and runtime/error paths.
- Risk: collection/supplier paths bypass proxies.
  - Mitigation: integration tests for every `ServiceRequest.Kind`.
- Risk: plugin API exposes mutable processor internals too early.
  - Mitigation: define small immutable model interfaces for v1 and exclude shaded JavaPoet/Proton types from public SPI.
- Risk: adding interceptor nodes disturbs include-root unused validation.
  - Mitigation: represent intercepted services as synthetic proxy nodes and register interceptor bindings as graph-required, not user include roots.
- Risk: synthetic proxy nodes break binding-centric graph/report assumptions.
  - Mitigation: refactor graph nodes to expose provider-node ids and kind-specific metadata, and add JSON/dot/include-root fixtures.
- Risk: plugin claim state is accidentally global by annotation type.
  - Mitigation: key claims by effective descriptor and add multi-service/qualified claim-state fixtures.
- Risk: plugin claim diagnostics are unstable.
  - Mitigation: use plugin implementation class canonical name as the stable plugin id and assert it in conflict diagnostics.
- Risk: lifecycle marker validation accepts invalid signatures.
  - Mitigation: encode the marker matrix in validation and add bad fixtures for unmarked, multi-marked, wrong-type, and wrong-phase parameters.
- Risk: inherited lifecycle annotations are interpreted differently by implementers.
  - Mitigation: v1 uses declared-only lifecycle methods and has inherited/override fixtures.
- Risk: interceptor dependencies create cycles through the proxy.
  - Mitigation: add a required-cycle fixture and supplier-boundary fixture that mirrors existing graph cycle semantics.
- Risk: package-access service proxies are not callable from cross-package injectors.
  - Mitigation: generate service-package bridge factories accepting `Object`/raw values and returning `Object` for inaccessible service types.
- Risk: duplicate proxy source generation across injectors.
  - Mitigation: add proxy descriptors with generated flags and a two-injector fixture.
- Risk: qualifier-insensitive proxy keys collapse distinct services.
  - Mitigation: key proxy descriptors by full coordinate and add same-type/different-qualifier fixtures.
- Risk: factory-generated injectables bypass service-interface interception.
  - Mitigation: include factory-generated service fixtures and inspect generated factory bindings.
- Risk: proxy method surface misses legal Java interface methods.
  - Mitigation: support overloads/varargs/redeclared `Object` methods and reject generic service interfaces/methods in v1.
- Risk: plugin SPI leaks internal generator concerns or relocated dependencies.
  - Mitigation: expose only Sting-owned model/emitter interfaces, raw statement emission, and an external-style plugin compile test.
- Risk: lifecycle failure semantics drift from nested interceptor expectations.
  - Mitigation: use the canonical lifecycle template and fixtures for own-before/own-after failure behavior.
- Risk: eager interception initializes only the raw target, leaving proxies or interceptors lazy.
  - Mitigation: model eager reachability through synthetic proxy nodes and add injector-construction integration coverage.
- Risk: generated code allocates metadata unconditionally.
  - Mitigation: generation tests assert no `Object[]` or boxing when not requested.

## Decision Log

| question | outcome | concrete plan impact |
| --- | --- | --- |
| Q-01 | Multiple service interfaces only. | Phase 4 rejects intercepted concrete/Object published types. |
| Q-02 | Proxy per service interface. | Phase 6/7 generate/cache service-specific proxies. |
| Q-03 | Implementation bindings allowed. | Phase 2 merges implementation class bindings before Phase 3 claim resolution. |
| Q-04 | Type-level only, provider method allowed as binding source. | Phase 4 validates binding locations. |
| Q-05 | Combine and reject duplicates. | Phase 2 effective binding merge logic and Phase 4 duplicate validation. |
| Q-06 | Provider bindings apply to every typed service. | Phase 2 provider binding-source model. |
| Q-07 | Binding annotations target TYPE+METHOD. | Phase 1 API docs and Phase 4 method validation. |
| Q-08 | `implementedBy` string only. | Phase 1 API, Phase 4 string validation, and Phase 5 resolution. |
| Q-09 | Empty `implementedBy` requires plugin. | Phase 4 validation. |
| Q-10 | No `@Around` in v1. | Phase 1 docs and Phase 3 lifecycle-only plugin API. |
| Q-11 | Lifecycle methods. | Phase 1/5/6 implement before/after/exception. |
| Q-12 | Explicit parameter markers. | Phase 1/5 parameter validation. |
| Q-13 | Arguments needed. | Phase 6 lazy `Object[]` generation. |
| Q-14 | Shared `@Arguments Object[]`. | Phase 6 reuse per invocation. |
| Q-15 | `@Result Object`. | Phase 6 result generation and boxing rules. |
| Q-16 | `@Thrown Throwable`, no checked lifecycle throws. | Phase 5 validation and Phase 6 catch generation. |
| Q-17 | Auto-add interceptors. | Phase 5 graph registration. |
| Q-18 | `implementedBy` names injectable class only. | Phase 5 validation. |
| Q-19 | Public lifecycle methods only. | Phase 5 validation. |
| Q-20 | Max one lifecycle method per phase. | Phase 5 validation. |
| Q-21 | Omit phases but require at least one. | Phase 5 validation. |
| Q-22 | No final requirement. | Phase 5 avoids extra class final validation. |
| Q-23 | Priority on binding annotation. | Phase 1 inline `priority`. |
| Q-24 | Equal priorities fail. | Phase 4 validation. |
| Q-25 | Nested lifecycle order. | Phase 6 code generation. |
| Q-26 | Outer interceptors observe inner failures. | Phase 6 nested try/catch shape. |
| Q-27 | String metadata. | Phase 1 marker docs and Phase 6 constants. |
| Q-28 | `@BindingValue`. | Phase 1/5/6 parameter support. |
| Q-29 | Required `priority()` in `@InterceptorBinding`. | Phase 1 API. |
| Q-30 | Cache proxy instances. | Phase 7 fields/accessors. |
| Q-31 | All request kinds receive proxy. | Phase 7 substitution. |
| Q-32 | Interceptor class effectively public. | Phase 5 validation. |
| Q-33 | Proxy in service package, target as service interface. | Phase 6 generator. |
| Q-34 | Proxy inherited/default methods. | Phase 6 method collection. |
| Q-35 | Normal failure replacement semantics. | Phase 6 no suppressed handling. |
| Q-36 | Sync boundary only for async returns. | Phase 6 no async wrapping, Phase 8 docs. |
| Q-37 | ServiceLoader plugins. | Phase 3 SPI discovery. |
| Q-38 | Proxy per binding-service. | Phase 6 naming and constants. |
| Q-39 | Arguments metadata-only. | Phase 6 target call uses original locals, Phase 8 docs. |
| Q-40 | Validate reachable uses only. | Phase 4 graph-reachable validation scope. |
| Package | Public interceptor annotations live in `sting.interceptors`. | Phase 1 file paths/package declarations, constants, docs, and examples. |

## Review Loop Changes

| round | change |
| --- | --- |
| 1 | Added synthetic proxy graph nodes, plugin claim ordering, package-access bridge factories, proxy emission dedupe, stable plugin SPI constraints, exact `@BindingValue` mappings, void/single-phase lifecycle validation, and v1 rejection of input/nullable target interception. |
| 2 | Added qualifier-aware service coordinates, factory-generated service interception, overloaded/varargs/redeclared `Object` method support, and v1 rejection of generic service interfaces/methods. |
| 3 | Specified lifecycle emitter SPI operations, canonical nested lifecycle failure semantics, binding annotation retention validation, `implementedBy` canonical-name validation, and eager intercepted proxy construction. |
| 4 | Split binding metadata extraction before plugin claim resolution and specified minimal public SPI model getters for binding and method metadata. |
| 5 | Aligned `InterceptorBinding` discovery with Sting's simple-name integration-hook policy and made all seven `ServiceRequest.Kind` proxy paths explicit. |
| package-update | Moved planned public interceptor annotations from root `sting` package to `sting.interceptors`. |
| 6 | Added kind-aware synthetic proxy graph-node contract, descriptor-scoped plugin claim identity, and complete lifecycle marker validation matrix. |
| 7 | Added stable plugin identity, declared-only lifecycle inheritance rules, and interceptor/proxy cycle fixtures. |

## Review Gate

This plan is accepted. `PLAN-APPROVAL` in `20-task-board.yaml` records user approval; implementation tasks may proceed in dependency order.
