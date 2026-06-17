# Sting Interceptors v1 Requirements

Status: accepted

## Mission

Add compile-time interception for Sting-published service interfaces using generated interface proxies. The feature must support cross-cutting concerns such as logging, auditing, timing, authorization checks, and transaction-style before/after/exception handling without reflection, dynamic proxies, bytecode generation, or runtime annotation lookup.

## Scope Boundaries

- In scope: type-level interceptor bindings on service interfaces, injectable implementation classes, and fragment provider methods.
- In scope: multiple published service interfaces per binding.
- In scope: generated cached proxy per binding plus published service interface.
- In scope: lifecycle interception through `@Before`, `@After`, and `@AfterException`.
- In scope: processor-path fast-path plugins discovered by `ServiceLoader`.
- In scope: required, non-null `@Injectable` and `@Fragment` provider bindings.
- In scope: factory-generated injectables published as service interfaces, intercepted through bindings on the factory service interface.
- Out of scope for v1: method-level interceptor bindings on service methods or implementation methods.
- Out of scope for v1: interception of injector inputs and nullable/optional provider bindings.
- Out of scope for v1: `@Around`, retry/token-refresh proceed semantics, argument rewriting, async completion observation, subclass proxies, runtime reflection, and generic `InvocationContext`.

## Non-Negotiables

- No Java reflection, `java.lang.reflect.Proxy`, runtime annotation lookup, bytecode generation, or runtime classpath scanning.
- Generated code must be compatible with Sting's existing GWT/J2CL-oriented style.
- Interception applies at service-interface boundaries, not self-invocation inside implementation classes.
- Requests for an intercepted service coordinate must receive the proxy across every `ServiceRequest.Kind`: instance, optional, supplier, supplier optional, collection, supplier collection, and supplier optional collection.
- Equal effective interceptor priorities for one service interface are compile errors.
- Duplicate effective binding annotation types for one service interface are compile errors.
- Plugin-only bindings must be claimed by exactly one plugin or compilation fails.
- Intercepted services must be represented in the component graph so interceptor dependencies participate in ordering, eager propagation, cycle checks, JSON reports, and dot reports.

## Public API Surface

Add new public interceptor annotations under `core/src/main/java/sting/interceptors/` in package `sting.interceptors`:

```java
@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.ANNOTATION_TYPE)
public @interface InterceptorBinding {
  @Nonnull
  String implementedBy() default "";

  int priority();
}
```

Lifecycle annotations:

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Before {}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface After {}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AfterException {}
```

Parameter marker annotations:

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ServiceType {}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface MethodName {}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface BindingValue {
  @Nonnull String value();
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Arguments {}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Result {}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Thrown {}
```

Notes:

- `InterceptorBinding` must be `CLASS` retention; marker annotations can be runtime-retained for processor visibility consistency with existing Sting annotations, but generated runtime code must not perform annotation lookup.
- All interceptor-related public annotations in this section must live in `sting.interceptors`; no new interceptor annotation belongs in the root `sting` package.
- Binding annotations that use `@InterceptorBinding` should use `@Retention(CLASS)` and `@Target({ TYPE, METHOD })`; Sting validates that method usage only appears on fragment provider methods in v1.
- Binding annotations that use `@InterceptorBinding` must not use `@Retention(SOURCE)`. Missing `@Retention`, `@Retention(CLASS)`, and `@Retention(RUNTIME)` are valid for processor visibility.
- The processor detects `InterceptorBinding` meta-annotations by simple name, matching Sting's existing integration-hook policy. Third-party meta-annotations named `InterceptorBinding` must expose a compatible `priority` member and optional `implementedBy` member or fail with a focused diagnostic.

## Behavior Expectations

- A binding may publish multiple service interfaces. Each intercepted published service interface receives its own cached proxy instance.
- Proxy identity may differ from the target identity and may differ across service interfaces from the same binding.
- Generated proxies are specific to `binding + published service interface`, not globally reusable per service type.
- Service identity includes the full Sting coordinate: qualifier plus service type. Proxy descriptors, proxy cache names, duplicate detection, and graph/report output must include the qualifier when present. The same service interface under two different qualifiers receives distinct proxy descriptors and caches.
- Proxies should live in the service interface package where required to implement package-access service interfaces.
- Proxy target fields should be typed as the service interface and delegate via service-interface methods.
- For package-access service interfaces or cross-package construction, generate a public service-package factory method that accepts inaccessible values as `Object` or raw wrapper types and casts inside the service package before constructing the proxy.
- Processor state must deduplicate proxy source emission across injectors and rounds. A proxy descriptor keyed by `binding id + service coordinate` must be emitted at most once.
- The component graph must include synthetic proxy nodes keyed by `binding id + service coordinate`. A proxy node depends on the raw target node and every generic interceptor component node. Published intercepted service coordinates resolve to proxy nodes, while the raw target node remains internal.
- Graph nodes must become kind-aware. Binding nodes retain existing binding ids and binding semantics; proxy nodes have stable ids derived from `proxy + binding id + full service coordinate`, expose proxy-specific metadata instead of a `Binding`, and participate in dependency traversal wherever existing graph logic expects a provider node.
- Graph JSON for proxy nodes must include `kind: "PROXY"`, the stable proxy node id, the full service coordinate, the raw target node id, and generic interceptor dependency node ids. Dependency `supportedBy` arrays must contain provider node ids, whether the provider is a binding node or a proxy node.
- Dot reports must render proxy nodes with distinguishable labels and edges to the raw target and interceptor dependencies. Include-root unused validation must continue to account for the raw binding and auto-added interceptor bindings without treating synthetic proxy nodes as user include roots.
- Eager intercepted bindings eagerly construct the raw target, required generic interceptor dependencies, and cached proxy nodes for every intercepted published service interface. An eager graph path reaching a proxy marks the proxy, raw target, and generic interceptor dependencies as eager unless the path crosses an existing supplier boundary.
- Proxy methods cover abstract, default, and inherited service-interface instance methods. Static/private interface methods are ignored. `Object` methods are ignored unless explicitly declared by the service interface.
- Proxy methods must support overloaded service methods, varargs service methods, and explicitly redeclared `Object` methods.
- V1 rejects intercepted generic service interfaces and service methods with their own type parameters. This keeps generated proxy signatures and raw `@Typed` service publication predictable.
- Binding annotations from service interface type and binding source are combined. Binding source means injectable implementation class for `@Injectable`, or fragment provider method for `@Fragment`.
- If the same binding annotation type appears from both service interface and binding source for the same service, fail compilation.
- `@Arguments Object[]` is metadata only. Generated code calls the target with original local variables. The array must be documented as read-only.
- `@Result Object` is passed only to `@After`; primitive returns are boxed only when required; void methods pass `null`.
- `@Thrown Throwable` is passed only to `@AfterException`.
- Lifecycle methods must be public instance methods. Private, protected, package-access, and static lifecycle methods are invalid in v1.
- Lifecycle methods must return `void`, must not have type parameters, and must not be annotated with more than one lifecycle annotation.
- Lifecycle methods must be declared directly on the interceptor implementation class in v1. Inherited lifecycle annotations are unsupported. A subclass may override a base method and redeclare the lifecycle annotation directly; an unannotated override of an inherited lifecycle method is invalid so inherited lifecycle behavior is not silently dropped.
- Interceptor implementation classes named by `implementedBy` must be effectively public `@Injectable` classes.
- `implementedBy` values must be canonical dotted qualified Java names. Nested types use dotted canonical names such as `com.acme.Outer.Inner`; binary names containing `$` are invalid.
- Interceptor classes may omit phases but must declare at least one lifecycle method and at most one method per phase.
- Lifecycle methods must not declare checked exceptions.
- Injector input bindings are not intercepted in v1. If an input service coordinate would otherwise be intercepted through a service-interface binding, fail compilation with an explicit input-interception unsupported diagnostic.
- Nullable provider bindings are not intercepted in v1. An intercepted `@Fragment` provider method must be non-null, and an intercepted binding must be required.
- If lifecycle methods throw `RuntimeException` or `Error`, normal nested Java semantics apply and the thrown failure replaces the current outcome.
- Async-returning methods are intercepted at the synchronous call boundary only; v1 does not observe async completion.

## Ordering Semantics

- `priority` is required on `@InterceptorBinding`.
- Lower priority means outermost interceptor.
- `@Before` runs outer-to-inner.
- `@After` and `@AfterException` run inner-to-outer.
- If an inner lifecycle method fails, outer interceptors observe the failure through `@AfterException`.

## Generated Lifecycle Template

For two interceptors where `A` is outer and `B` is inner, generated code must behave like this template:

```java
ResultType result;
A.before();
try {
  B.before();
  try {
    result = target.method(args);
  } catch (Throwable t) {
    B.afterException(t);
    throw t;
  }
  B.after(result);
} catch (Throwable t) {
  A.afterException(t);
  throw t;
}
A.after(result);
return result;
```

Normative implications:

- An interceptor's own `@AfterException` does not observe failure from its own `@Before`.
- An interceptor's own `@AfterException` does not observe failure from its own `@After`.
- Outer interceptors observe failures from inner `@Before`, `@After`, `@AfterException`, and target calls.
- If `@AfterException` throws, that failure replaces the current failure and is observed by outer interceptors.
- Actual generated code must preserve void returns, non-void returns, declared checked exceptions, runtime exceptions, and errors.

## Generic Lifecycle Parameter Rules

Allowed markers:

- `@ServiceType String`: fully qualified service interface name.
- `@MethodName String`: service method simple name.
- `@BindingValue("name")`: compile-time value from the current binding annotation member.
- `@Arguments Object[]`: lazily created and shared for the proxy method invocation only when requested.
- `@Result Object`: successful return value for `@After` only.
- `@Thrown Throwable`: thrown failure for `@AfterException` only.

Lifecycle parameter validation matrix:

| marker | allowed phases | required Java parameter type |
| --- | --- | --- |
| `@ServiceType` | `@Before`, `@After`, `@AfterException` | `String` |
| `@MethodName` | `@Before`, `@After`, `@AfterException` | `String` |
| `@BindingValue` | `@Before`, `@After`, `@AfterException` | member-compatible type from the conversion rules below |
| `@Arguments` | `@Before`, `@After`, `@AfterException` | `Object[]` |
| `@Result` | `@After` only | `Object` |
| `@Thrown` | `@AfterException` only | `Throwable` |

Every lifecycle method parameter must have exactly one marker annotation from `sting.interceptors`. Unmarked parameters, multiple marker annotations on one parameter, marker annotations on unsupported parameter types, and marker annotations used in unsupported phases are compile errors.

`@BindingValue` conversion rules:

- `String` annotation members bind to `String` parameters.
- Primitive annotation members bind to the same primitive type or corresponding boxed type.
- Enum annotation members bind to `String` parameters containing the enum constant name.
- Class-valued annotation members bind to `String` parameters containing the fully qualified class name extracted at compile time.
- Default annotation member values are used when a binding annotation omits the member.
- Array-valued members and nested annotation-valued members are not supported in v1 and must fail validation if requested via `@BindingValue`.

Rejected in v1:

- Parameter-name based binding.
- Type-only implicit binding.
- Individual typed method argument injection.
- Runtime binding annotation instances.
- `Class<?>`, `Method`, or reflection metadata parameters.

## Plugin Requirements

- Add a processor SPI, likely in `processor/src/main/java/sting/processor/`, discovered with `ServiceLoader`.
- A plugin may claim an interceptor binding and emit direct lifecycle code.
- Plugin claim identity is per effective interceptor binding descriptor, keyed by `binding id + full service coordinate + binding annotation type + normalized binding annotation value map`.
- Exactly zero or one plugin may claim one effective descriptor. Multiple claimants for the same descriptor fail compilation.
- Claim state transitions are `unclaimed -> claimed(plugin id)` or `unclaimed -> conflict(multiple plugin ids)`. Claim state is independent for the same annotation on different service coordinates.
- Plugin id is the plugin implementation class canonical name, falling back to the binary class name only when the canonical name is unavailable. Diagnostics and tests must use this stable id, not `toString()` or service-file ordering.
- If one plugin claims an effective descriptor, `implementedBy` may be empty and no generic interceptor component is required for that descriptor.
- If no plugin claims an effective descriptor, `implementedBy` must name an effectively public `@Injectable` interceptor class.
- Effective interceptor binding metadata extraction happens before plugin claim resolution. Plugin discovery and claim resolution must happen before claim-dependent validation and generic interceptor implementation resolution.
- The v1 plugin SPI must not expose shaded JavaPoet or Proton types in public signatures.
- The v1 plugin SPI must not add graph/runtime dependencies. Plugins emit dependency-free direct lifecycle snippets through Sting-owned model/emitter interfaces. Runtime collaborators require a normal `implementedBy` interceptor and generic lifecycle path.
- The v1 plugin SPI emits raw Java statements through a Sting-owned emitter. Public plugin APIs must not expose imports, fields, methods, constructor parameters, graph dependencies, or runtime service requests as extension points.
- The emitter must provide expression helpers for service type, method name, binding values, individual arguments, the shared arguments array, result, and thrown failure. Result and thrown expressions are legal only in their matching lifecycle phases; invalid metadata requests fail with plugin diagnostics.
- Public SPI model interfaces must expose enough Sting-owned metadata for external plugins to claim a binding and emit method-specific lifecycle code:
  - binding annotation type name
  - priority
  - full service type name
  - qualifier key, or an empty value when unqualified
  - binding member names and scalar member values
  - method name
  - return type name
  - parameter type names
  - declared thrown type names
  - default-method and varargs flags

## Quality Gates

Targeted checks during implementation:

- Processor fixture tests for each new validation and generation shape.
- Integration tests covering generated proxy behavior and runtime call ordering.
- Focused documentation examples compile as part of existing doc example build.

Required full gate before implementation completion:

```bash
bundle exec buildr ci J2CL=no
```

Optional compatibility gate when the local environment supports it:

```bash
bundle exec buildr ci
```

## Known Intentional Divergences

- Existing non-intercepted `@Typed` behavior remains unchanged.
- Intercepted bindings may no longer expose concrete implementation types or `Object.class`; v1 interception supports multiple service interfaces only.
- Identity compatibility is intentionally relaxed for intercepted multi-service bindings.
- Async completion semantics are intentionally not modeled in v1.

## Post-Implementation Issue Requirements

### PI-01: Proxy whitelisted annotation preservation

Mission:

- Align interceptor proxy method generation with existing Sting generated variants that preserve whitelisted source annotations.

Evidence:

- `InterceptorsIntegrationTest.MyService.ok(@Nonnull String)` declares `@Nonnull` on the return value and parameter.
- The generated `Sting_sting_integration_InterceptorsIntegrationTest_MyServiceImpl_MyService_InterceptorProxy.ok(String)` omits both annotations.
- Existing generators such as `FactoryGenerator` and `FragmentGenerator` explicitly call `GeneratorUtil.copyWhitelistedAnnotations(...)`; `InterceptorProxyGenerator` currently relies on `MethodSpec.overriding(...)`, and JavaPoet does not copy overridden method or parameter annotations.

Scope boundaries:

- In scope: copied whitelisted annotations on generated interceptor proxy service methods.
- In scope: copied whitelisted annotations on generated interceptor proxy service method parameters.
- In scope: `@Nonnull`, `@Nullable`, and `@Deprecated`, matching `GeneratorUtil.ANNOTATION_WHITELIST`.
- In scope: generated-source fixture updates and assertions proving copied annotations appear in interceptor proxy output.
- Out of scope: copying arbitrary annotations, interceptor binding annotations, lifecycle annotations, or user-defined annotations outside the whitelist.
- Out of scope: copying source `@SuppressWarnings`; Sting continues to synthesize `@SuppressWarnings` only when generated code requires rawtype/deprecation/unchecked suppression.
- Out of scope: changing interceptor runtime behavior, lifecycle ordering, proxy identity, request-kind routing, or graph semantics.

Behavior expectations:

- Interceptor proxy overrides must preserve whitelisted method annotations from the service interface declaration.
- Interceptor proxy override parameters must preserve whitelisted parameter annotations from the service interface declaration.
- Type resolution must remain correct for inherited methods and methods viewed through the proxied service `DeclaredType`.
- Generated proxy methods must continue to include `@Override`, resolved return/parameter/throws types, varargs shape, and declared checked exception signatures.
- Existing generated proxy fixtures may legitimately gain `final` on parameters if the implementation switches to the existing `GeneratorUtil.overrideMethod(...)` helper.

Quality gates:

- Targeted gate: `bundle exec buildr sting:processor:test`
- Targeted gate: `bundle exec buildr sting:integration-tests:test`
- Required full gate before issue resolution: `bundle exec buildr ci J2CL=no`

## Acceptance Criteria

- New public annotations compile and are documented.
- Processor discovers and validates interceptor binding annotations from service interface types, implementation classes, and fragment provider methods.
- Processor detects interceptor binding meta-annotations by simple name and includes fixtures for a non-Sting integration annotation named `InterceptorBinding`.
- Processor rejects v1-invalid usages with focused diagnostics.
- Processor rejects input interception, nullable intercepted providers, unsupported `@BindingValue` conversions, non-void lifecycle methods, and multi-phase lifecycle methods.
- Processor rejects binding annotations that combine `@InterceptorBinding` with `@Retention(SOURCE)`.
- Processor accepts `implementedBy` canonical dotted names, including nested dotted names, and rejects binary names containing `$`.
- Processor rejects intercepted generic service interfaces and generic service methods.
- Processor handles qualified intercepted services as distinct service coordinates.
- Factory-generated injectable services can be intercepted when their published factory interface has service-interface interceptor bindings.
- Interceptor implementation classes are auto-added to the graph when needed.
- Synthetic proxy nodes appear in graph-derived generation/reporting so interceptor dependencies are ordered and cycle-checked.
- Synthetic proxy node ids, graph JSON fields, dot labels, `supportedBy` references, and include-root validation behavior are covered by fixtures.
- Cycle tests include an interceptor dependency cycle through the intercepted service proxy, and supplier-boundary behavior follows existing Sting cycle rules while still delivering the proxy when requested.
- Eager intercepted services construct raw targets, generic interceptor dependencies, and cached proxy nodes according to eager graph reachability rules.
- Proxy source emission is deduplicated across multiple injectors that include the same intercepted binding.
- Generated proxies wrap service calls with correct lifecycle ordering and exception behavior.
- Generated interceptor proxy methods and parameters preserve whitelisted source annotations (`@Nonnull`, `@Nullable`, `@Deprecated`) from service interface methods, matching existing generated variants.
- Generated proxies avoid argument/result allocation unless requested by interceptor method signatures.
- All request kinds receive proxies for intercepted service coordinates.
- Explicit request-kind coverage includes instance, optional, supplier, supplier optional, collection, supplier collection, and supplier optional collection.
- Plugin-only and generic interceptor paths are both covered by tests.
- Public plugin SPI models expose enough metadata for external plugins to claim specific bindings and emit method-specific lifecycle code without processor-internal types.
- Plugin claim tests cover the same binding annotation on two service coordinates, multiple claimants for one effective descriptor, and an unclaimed plugin-only descriptor on one coordinate.
- Plugin conflict diagnostics identify plugins by stable implementation class canonical name.
- Public docs and examples import interceptor annotations from `sting.interceptors`.
- Docs and changelog describe v1 scope and explicit v1 exclusions.

## Review Clarifications

These clarifications were added after iterative plan review round 1:

- R-01: Use graph-visible synthetic proxy nodes keyed by `binding id + service coordinate`; proxy nodes depend on target and generic interceptor nodes.
- R-02: Discover and claim plugins before resolving generic `implementedBy` interceptors.
- R-03: Generate service-package bridge factories accepting `Object`/raw values for package-access service interfaces.
- R-04: Deduplicate proxy source emission with processor-level proxy descriptors/generated flags.
- R-05: Keep the v1 plugin SPI free of shaded JavaPoet/Proton types and plugin-requested graph dependencies.
- R-06: Define exact `@BindingValue` member-to-parameter conversions.
- R-07: Require lifecycle methods to be void and single-phase.
- R-08: Reject input interception and nullable intercepted provider bindings in v1.
- R-09: Treat qualifiers as part of intercepted service identity and test qualified proxy descriptors/caches.
- R-10: Include factory-generated injectable services in v1 when bindings are on the factory interface.
- R-11: Support overloaded, varargs, and explicitly redeclared `Object` service methods.
- R-12: Reject intercepted generic service interfaces and generic service methods in v1.
- R-13: Define plugin emission through a Sting-owned lifecycle emitter with raw Java statements and no import/field/method/constructor/graph extension points.
- R-14: Specify canonical nested try/catch lifecycle semantics, including which failures an interceptor's own `@AfterException` observes.
- R-15: Reject `@Retention(SOURCE)` on binding annotations while accepting missing, `CLASS`, and `RUNTIME` retention.
- R-16: Define `implementedBy` as a canonical dotted qualified name and reject binary `$` names.
- R-17: Define eager intercepted service construction and propagation through proxy, target, and generic interceptor nodes.
- R-18: Split interceptor binding metadata extraction from claim-dependent validation so plugin claim resolution has binding models to inspect.
- R-19: Specify minimal public SPI model metadata getters for external plugin claiming and method-specific emission.
- R-20: Detect `InterceptorBinding` meta-annotations by simple name, consistent with existing Sting integration hooks.
- R-21: Enumerate all seven `ServiceRequest.Kind` values and require proxy coverage for optional supplier forms.
- R-22: Place all public interceptor annotations in package `sting.interceptors`, not the root `sting` package.
- R-23: Define kind-aware synthetic proxy graph-node ids, serialization/reporting fields, provider references, and include-root behavior.
- R-24: Define plugin claim identity per effective descriptor and claim state transitions.
- R-25: Add a complete lifecycle parameter marker validation matrix.
- R-26: Use plugin implementation class canonical name as the stable plugin id for claim diagnostics.
- R-27: Restrict lifecycle methods to methods declared directly on the interceptor implementation class in v1.
- R-28: Require cycle fixtures for `proxy -> interceptor -> intercepted service -> proxy` and supplier-boundary behavior.

## Open Questions Register

All design questions from the grill-me session are resolved. `artifacts_updated` refers to this requirements file, `10-implementation-plan.md`, and `20-task-board.yaml`.

| id | status | question | context | options | tradeoffs | recommended_default | user_decision | artifacts_updated |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Q-01 | resolved | Must intercepted bindings publish exactly one service interface? | Existing Sting supports multi-type `@Typed`; interception must avoid bypass ambiguity. | Single service only; multiple service interfaces; arbitrary multiple types. | Single service is simpler but too restrictive; arbitrary types cannot be decorated by interface proxy. | Support multiple service interfaces only. | Multiple service interfaces only; concrete/Object publication not required for intercepted bindings. | 00, 10, 20 |
| Q-02 | resolved | Should v1 generate separate proxy/cache per published service interface? | Same target may publish several service interfaces. | One composite proxy; per-service proxy. | Composite proxy has signature conflict risks; per-service proxy preserves interface-specific metadata. | Per-service proxy. | Accepted. | 00, 10, 20 |
| Q-03 | resolved | Are implementation-side bindings allowed? | Implementation annotations can express binding-wide defaults. | Service interface only; service plus implementation. | Implementation support adds merge rules but is useful. | Allow binding-wide implementation annotations. | Implementation bindings should be valid and apply to all typed service interfaces. | 00, 10, 20 |
| Q-04 | resolved | Are method-level bindings in v1 allowed? | Method binding semantics multiply merge complexity. | Type-level only; type plus method. | Method-level is expressive but complicates v1. | Type-level only. | Bindings limited to types, plus provider methods as binding sources. | 00, 10, 20 |
| Q-05 | resolved | Should service and binding-source bindings combine? | Interface and implementation/provider may both declare bindings. | Combine; override; source wins. | Override hides conflicts; combine is explicit. | Combine and reject duplicate binding annotation types. | Accepted. | 00, 10, 20 |
| Q-06 | resolved | Do provider-method bindings apply to all typed service interfaces? | Provider methods create/publish a binding. | Apply to all; service-specific unsupported. | All-services rule is simple and mirrors implementation class annotations. | Apply to every typed service interface. | Accepted. | 00, 10, 20 |
| Q-07 | resolved | Should binding annotation target include `METHOD`? | Java cannot target only fragment provider methods. | TYPE only; TYPE+METHOD with validation. | TYPE+METHOD permits Java compilation and Sting validation. | TYPE+METHOD. | Accepted. | 00, 10, 20 |
| Q-08 | resolved | Should `implementedBy` be a string? | Plugins and generated/intermediate integrations may not have runtime classes. | `Class<?>`; string; both. | String avoids class literal requirements and matches Sting integration style. | String class name only. | Accepted. | 00, 10, 20 |
| Q-09 | resolved | If `implementedBy=""`, allow no-op marker bindings? | Silent no-op would hide misconfiguration. | Marker no-op; require plugin. | Plugin requirement is stricter and safer. | Require plugin. | Accepted. | 00, 10, 20 |
| Q-10 | resolved | Include `@Around` in v1? | Around introduces proceed/retry/exception/async complexity. | Include now; defer. | Deferring keeps v1 focused. | Defer to v2. | Accepted. | 00, 10, 20 |
| Q-11 | resolved | Use lifecycle methods or generic SPI? | No around means generic invocation context adds overhead. | Lifecycle methods; MethodInterceptor context. | Lifecycle calls are direct and allocation-light. | Lifecycle methods. | Accepted. | 00, 10, 20 |
| Q-12 | resolved | Bind lifecycle parameters how? | Names and type-only matching are ambiguous. | Parameter name; type-only; explicit markers. | Explicit markers give validation and stable codegen. | Explicit parameter marker annotations. | Accepted. | 00, 10, 20 |
| Q-13 | resolved | Should v1 support service method arguments? | Auditing needs arguments. | No args; `Object[]`; typed args. | `Object[]` allocates only when needed; typed args are method-specific. | Lazy shared `@Arguments Object[]`. | Arguments are necessary; use lazy shared array. | 00, 10, 20 |
| Q-14 | resolved | Exact v1 argument model? | Multiple interceptors may request arguments. | Per-interceptor arrays; shared array; typed args. | Shared array minimizes allocations. | Shared `Object[]` per invocation when needed. | Accepted. | 00, 10, 20 |
| Q-15 | resolved | Support `@Result`? | Audit may need successful return values. | No result; `Object` result. | `Object` boxes primitives only when requested. | `@Result Object`, null for void. | Accepted. | 00, 10, 20 |
| Q-16 | resolved | Exception metadata and checked lifecycle throws? | Proxy must preserve interface throws signatures. | `Throwable`; declared-only; allow checked lifecycle throws. | `Throwable` is complete; checked lifecycle throws complicate signatures. | `@Thrown Throwable`; no checked lifecycle throws. | Accepted. | 00, 10, 20 |
| Q-17 | resolved | Auto-add interceptor implementations to graph? | Requiring manual includes leaks cross-cutting concern wiring. | Manual includes; auto include. | Auto include is less error-prone. | Auto-add when used. | Accepted. | 00, 10, 20 |
| Q-18 | resolved | What can `implementedBy` name in v1? | Fragment-provided interceptors would require coordinate lookup. | Injectable class only; arbitrary service/provider. | Injectable-only is implementable and clear. | Effectively public `@Injectable` class. | Accepted. | 00, 10, 20 |
| Q-19 | resolved | Lifecycle method visibility? | Generated code may cross packages. | Public only; public/package-access. | Public-only avoids adapter complexity. | Public only. | Selected public only. | 00, 10, 20 |
| Q-20 | resolved | Multiple lifecycle methods per phase? | Ordering inside one interceptor would be undefined. | Allow many; max one. | Max one keeps semantics clear. | Max one per phase. | Accepted. | 00, 10, 20 |
| Q-21 | resolved | Can interceptors omit phases? | Some concerns are before-only or after-only. | Require all; allow omissions. | Omissions are useful; empty class should fail. | Omit phases but require at least one. | Accepted. | 00, 10, 20 |
| Q-22 | resolved | Must interceptor classes be final? | Generated code does not subclass interceptors. | Require final; no extra rule. | Final is style-only. | No final requirement. | Accepted. | 00, 10, 20 |
| Q-23 | resolved | Where is priority declared? | Plugin-only bindings may have no class. | Binding annotation; implementation class. | Binding-level priority works for plugins and class-backed bindings. | Binding annotation. | Accepted. | 00, 10, 20 |
| Q-24 | resolved | Equal priorities? | Order affects behavior. | Tie-break; fail. | Compile failure makes order explicit. | Fail compilation. | Accepted. | 00, 10, 20 |
| Q-25 | resolved | Lifecycle ordering? | Lower priority is outermost. | Same order all phases; nested semantics. | Nested semantics match interceptor mental model. | Before outer-to-inner; after/exception inner-to-outer. | Accepted. | 00, 10, 20 |
| Q-26 | resolved | Do outer interceptors observe lifecycle failures? | Nested semantics imply outer observations. | Observe; do not observe. | Observe matches nested try/catch. | Yes, outer `@AfterException` observes inner failures. | Accepted. | 00, 10, 20 |
| Q-27 | resolved | Metadata representation? | J2CL/no-reflection constraint. | Strings; Class/Method objects. | Strings avoid runtime metadata dependencies. | String metadata. | Accepted. | 00, 10, 20 |
| Q-28 | resolved | Binding annotation values? | Passing annotation instances requires generated objects or reflection. | Annotation instance; `@BindingValue`. | Individual constants are allocation-free and explicit. | `@BindingValue("member")`. | Accepted. | 00, 10, 20 |
| Q-29 | resolved | Required priority shape? | Separate priority annotation is redundant if required. | Separate annotation; required member. | Inline required member makes omission a Java compile error. | Required `priority()` in `@InterceptorBinding`. | Accepted. | 00, 10, 20 |
| Q-30 | resolved | Cache proxy instances? | Recreating proxies changes identity and allocates. | No cache; cache per service. | Cache gives stable service identity. | Cache per binding-service interface. | Accepted. | 00, 10, 20 |
| Q-31 | resolved | Collections/suppliers receive proxy? | Collection paths can bypass otherwise. | Raw target; proxy. | Proxy preserves interception for all request kinds. | Proxy for all intercepted service coordinates. | Accepted. | 00, 10, 20 |
| Q-32 | resolved | Must interceptor classes be effectively public? | Public methods on non-public class are inaccessible cross-package. | Allow adapters; require public. | Public requirement avoids v1 adapter subsystem. | Effectively public required. | Accepted. | 00, 10, 20 |
| Q-33 | resolved | Proxy placement and target type? | Package-access services exist. | Implementation package/target type; service package/service type. | Service package supports service visibility and boundary semantics. | Service package, target typed as service interface. | Accepted. | 00, 10, 20 |
| Q-34 | resolved | Which service methods are proxied? | Type-level binding should cover interface surface. | Abstract only; include defaults/inherited. | Include defaults/inherited avoids gaps. | All callable instance service methods except static/private/Object. | Accepted. | 00, 10, 20 |
| Q-35 | resolved | Lifecycle failure replacement? | Java nested semantics need clear outcome. | Preserve original with suppressed; replace normally. | Normal replacement is simpler. | Normal nested Java semantics, no suppressed handling. | Accepted. | 00, 10, 20 |
| Q-36 | resolved | Async semantics? | No single Sting async abstraction. | Observe async completion; synchronous boundary only. | Boundary-only is framework-neutral. | Synchronous call boundary only. | Accepted. | 00, 10, 20 |
| Q-37 | resolved | Plugin discovery? | No existing plugin system in processor. | Manual registration; `ServiceLoader`. | `ServiceLoader` is standard processor-path extension. | `ServiceLoader`. | Accepted. | 00, 10, 20 |
| Q-38 | resolved | Proxy class reuse granularity? | Binding-source values differ per target binding. | One proxy per service; proxy per binding-service. | Binding-service proxy can bake constants. | Proxy per binding plus service interface. | Accepted. | 00, 10, 20 |
| Q-39 | resolved | Can `@Arguments` rewrite target args? | Mutating arrays could be surprising. | Metadata-only; mutation rewrites. | Metadata-only preserves direct-call model. | Metadata-only/read-only. | Accepted. | 00, 10, 20 |
| Q-40 | resolved | Validation scope? | Custom binding annotations may appear outside Sting graph. | Global validation; graph-reachable validation. | Reachable validation avoids unrelated diagnostics. | Validate only Sting-reachable binding usages. | Accepted. | 00, 10, 20 |
