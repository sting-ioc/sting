# Sting Interceptors v1 Test Strategy

Status: accepted

## Required Full Gate

```bash
bundle exec buildr ci J2CL=no
```

Run this before marking implementation complete.

## Optional Compatibility Gate

```bash
bundle exec buildr ci
```

Run when the local environment supports J2CL/GWT variants.

## Minimum Coverage Budget

The interceptor suite must be at least as comprehensive as existing Sting feature suites and should err on the side of more fixture coverage. The minimum planned coverage is:

| category | minimum top-level checks |
| --- | ---: |
| Processor good fixtures | 36 |
| Processor bad fixtures | 43 |
| Cycle-boundary fixtures | 2 |
| Integration runtime scenarios | 26 |
| Generated-source assertions | 14 |
| Documentation validation examples | 7 |
| Manual generated-source review checks | 5 |
| Total | 133 |

These are minimum top-level checks, not maximums. When a row below names multiple subcases, such as every `ServiceRequest.Kind`, every lifecycle marker type, or every `@BindingValue` conversion shape, each named subcase must receive concrete fixture coverage.

## Processor Fixture Coverage

Add good fixtures for:

- Public interceptor annotations imported from `sting.interceptors`.
- Type-level binding on service interface only.
- Third-party integration annotation named `InterceptorBinding` is detected by simple name and exposes compatible `priority`/`implementedBy` members.
- Interceptor binding annotations are extracted into descriptors before plugin claim resolution.
- Binding on injectable implementation class applying to all typed service interfaces.
- Binding on fragment provider method applying to all typed service interfaces.
- Combined service-interface plus binding-source annotations.
- Multiple service interfaces with separate generated proxies.
- Same service interface type published under two different `@Named` qualifiers with distinct proxies/caches.
- Factory-generated injectable service interface intercepted through a binding on the factory interface.
- Generic interceptor with before only.
- Generic interceptor with after only.
- Generic interceptor with afterException only.
- Generic interceptor with all three phases.
- `@ServiceType String` and `@MethodName String`.
- `@ServiceType`, `@MethodName`, and `@Arguments` with legal parameter types in every lifecycle phase.
- `@BindingValue` for `String`, primitive/boxed primitive, enum constant name as `String`, class-valued member as fully qualified class-name `String`, and default member values.
- `@Arguments Object[]` generated only when requested.
- `@Result Object` with reference, primitive, and void service methods.
- `@Thrown Throwable` for declared checked exception and runtime exception.
- Default interface methods and inherited interface methods.
- Supplier, optional, supplier optional, collection, supplier collection, and supplier optional collection requests.
- Plugin-only binding claimed by a test plugin.
- Plugin wins when both plugin and `implementedBy` exist.
- External-style test plugin compiles against the public SPI without shaded JavaPoet/Proton or processor-internal dependencies.
- External-style test plugin claims a binding using `InterceptorBindingModel` getters and emits method-specific code using `InterceptedMethodModel` getters.
- Two injectors include the same intercepted binding and only one proxy source is emitted.
- Cross-package package-access service interface proxy is constructed through a generated bridge factory.
- Graph JSON/dot reports include synthetic proxy nodes with stable proxy ids, `kind: "PROXY"`, service coordinates, target node ids, interceptor node ids, and provider-node `supportedBy` references.
- Include-root unused validation treats raw target and interceptor bindings reachable only through proxy nodes as used, while proxy nodes are not include roots.
- Overloaded service methods.
- Varargs service methods.
- Explicitly redeclared `Object` methods on a service interface.
- Same binding annotation on two service coordinates has independent plugin claim state.
- Multiple plugin claim diagnostic includes plugin implementation class canonical names.
- Plugin-only binding where one effective descriptor is claimed and another descriptor is unclaimed reports only the unclaimed descriptor.

Add bad fixtures for:

- Binding annotation on service method.
- Binding annotation on implementation method.
- Binding annotation on non-fragment method.
- Intercepted binding publishing concrete implementation class.
- Intercepted binding publishing `Object.class`.
- Intercepted injector input service.
- Intercepted nullable provider binding.
- Intercepted generic service interface.
- Intercepted service method with type parameters.
- Duplicate effective binding annotation type.
- Duplicate effective priority.
- Binding annotation combines `@InterceptorBinding` with `@Retention(SOURCE)`.
- Third-party simple-name `InterceptorBinding` hook lacks compatible `priority` or `implementedBy` members.
- Empty `implementedBy` without plugin.
- Multiple plugins claiming the same binding.
- `implementedBy` class missing.
- `implementedBy` uses a binary nested-class name containing `$`.
- `implementedBy` names non-`@Injectable` class.
- `implementedBy` names non-public class.
- Interceptor lifecycle method is private, protected, package-access, or static.
- Interceptor lifecycle annotation appears only on an inherited method.
- Interceptor overrides an inherited lifecycle method without redeclaring the lifecycle annotation.
- Interceptor inherits one lifecycle method and declares another lifecycle method for the same phase.
- Interceptor lifecycle method returns non-void.
- Interceptor lifecycle method has type parameters.
- Interceptor lifecycle method has multiple lifecycle annotations.
- Interceptor lifecycle method declares checked exception.
- Interceptor class has duplicate lifecycle method for same phase.
- Interceptor class has no lifecycle methods.
- Unsupported lifecycle parameter marker.
- Lifecycle parameter has no marker annotation.
- Lifecycle parameter has multiple marker annotations.
- `@ServiceType` parameter is not `String`.
- `@MethodName` parameter is not `String`.
- `@Arguments` parameter is not `Object[]`.
- `@Result` parameter is not `Object`.
- `@Thrown` parameter is not `Throwable`.
- `@Result` used outside `@After`.
- `@Thrown` used outside `@AfterException`.
- `@BindingValue` names an unknown annotation member.
- `@BindingValue` parameter type incompatible with annotation member value.
- `@BindingValue` requests array-valued or nested annotation-valued member.
- Interceptor constructor dependency requests the intercepted service, producing `proxy -> interceptor -> intercepted service -> proxy` cycle.

Add cycle-boundary fixtures for:

- Interceptor dependency on `Supplier<intercepted service>` follows existing supplier cycle semantics.
- The supplied intercepted service is still the proxy when the supplier is invoked.

## Integration Runtime Coverage

Add integration tests under `integration-tests/src/test/java/sting/integration/`.

Required scenarios:

- `before` is called before target.
- `after` is called after successful target return.
- `afterException` is called when target throws a declared checked exception.
- `afterException` is called when target throws runtime exception.
- Ordering for priorities `100`, `200`, `300` is before outer-to-inner and after/exception inner-to-outer.
- If inner `before` throws, outer `afterException` observes it and target is not called.
- If inner `after` throws, outer `afterException` observes it.
- If inner `afterException` throws, outer `afterException` observes replacement failure.
- If an interceptor's own `before` throws, that interceptor's own `afterException` is not called.
- If an interceptor's own `after` throws, that interceptor's own `afterException` is not called and outer `afterException` observes the failure.
- Subclass override that redeclares a lifecycle annotation directly is accepted.
- Multi-service binding returns stable cached proxy per service interface, and different service interfaces may have different identity.
- Constructor dependency requesting intercepted service receives proxy.
- Fragment provider method dependency requesting intercepted service receives proxy.
- Injector output requesting intercepted service receives proxy.
- `Supplier<T>` returns proxy.
- `Supplier<Optional<T>>` returns `Optional.of(proxy)` for present required bindings.
- `Optional<T>` contains proxy.
- `Collection<T>` contains proxies.
- `Collection<Supplier<T>>` suppliers return proxies.
- `Collection<Supplier<Optional<T>>>` suppliers return `Optional.of(proxy)` for present required bindings.
- Same service type under different `@Named` qualifiers receives distinct proxies.
- Factory-generated service interface is intercepted.
- Eager intercepted binding constructs the raw target, cached proxy, and generic interceptor dependencies when the injector is created.
- `@Arguments` mutation does not rewrite target call arguments.
- Async-shaped method returning a handle is intercepted only at synchronous call boundary.

## Generated Source Assertions

Expected generated-source fixtures should verify:

- No `Object[]` allocation when no interceptor requests arguments.
- Exactly one `Object[]` allocation per proxy method invocation when requested.
- Primitive result boxing appears only when `@Result` is requested.
- Void result passes `null` only when `@Result` is requested.
- Proxy constructor and fields use service interface target type.
- Package-access proxy factory accepts inaccessible target/service values as `Object` or raw wrapper types and casts inside the service package.
- Proxy class name is deterministic per binding plus service interface.
- Proxy/cache names and graph descriptors include qualifier information for qualified intercepted services.
- Graph JSON uses provider node ids rather than assuming every dependency target has a binding id.
- Dot reports render proxy nodes with distinguishable labels and edges to target/interceptor nodes.
- Proxy class is generated in a package that can implement package-access service interfaces.
- Lifecycle nesting matches the canonical template, including own-before and own-after failure behavior.
- Checked exception proxy method signature matches service interface.
- Catch blocks rethrow declared checked exceptions, runtime exceptions, and errors without wrapping.

## Documentation Validation

Documentation updates should include examples that compile under the normal build:

- Simple timing interceptor.
- Auditing interceptor using `@BindingValue` and `@Arguments`.
- Provider-method binding example.
- Multiple service interface publication example.
- Plugin-only binding note.
- Imports from `sting.interceptors` for every public interceptor annotation.
- `InterceptorBinding` simple-name integration-hook note in processor integration documentation.

## Residual Manual Review

Before finalizing implementation, manually inspect at least one generated proxy fixture for:

- Lifecycle nesting readability.
- No debug logging.
- No unused imports.
- No avoidable allocations.
- Clear generated names for proxy fields and accessors.
