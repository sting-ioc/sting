---
title: Interceptors
---

Interceptors add compile-time lifecycle hooks around calls made through Sting-published service interfaces. Sting
generates direct service-interface proxies, so interception does not use reflection, dynamic proxies, bytecode
generation, runtime annotation lookup, or runtime classpath scanning.

Interception applies at the service-interface boundary. A component that calls one of its own methods directly is not
intercepted; consumers that request the published service interface receive the generated proxy.

## Binding Annotations

An interceptor binding is a normal annotation annotated with
{@link: sting.interceptors.InterceptorBinding @InterceptorBinding}. All public interceptor annotations live in
the `sting.interceptors` package.

{@file_content: file=sting/doc/examples/interceptors/Timed.java start_line=@InterceptorBinding}

The required `implementedBy` value is the canonical dotted name of an {@link: sting.Injectable @Injectable} interceptor
implementation. Lower `priority` values run outermost. Equal effective priorities for one intercepted service are
compile errors.

`implementedBy` may also be an enum-backed template. A template contains `{memberName}` placeholders that refer to
members on the interceptor binding annotation. Placeholders are supported only for scalar enum members; `String`,
primitive, `Class`, annotation-valued, and array-valued members are rejected with compile-time diagnostics.

```java
@InterceptorBinding( implementedBy = "com.example.{value}TraceInterceptor", priority = 100 )
@interface Traced
{
  TraceMode value() default TraceMode.DEFAULT;
}
```

Sting resolves the template from the effective annotation value on each reachable binding usage. It does not enumerate
or validate unused enum constants up front. The selected enum constant name is split on underscores and converted to
PascalCase using locale-independent case conversion, so `DEFAULT` becomes `Default` and `REQUIRES_NEW` becomes
`RequiresNew`. Enum constants with leading, trailing, or repeated underscores are rejected when that value is selected.
After substitution, the resolved name is validated as a canonical dotted Java name and then resolved through the normal
interceptor implementation checks.

Bindings may be placed on:

- service interface types,
- injectable implementation classes, and
- fragment provider methods.

Method-level bindings on service methods or implementation methods are not supported.

## Lifecycle Methods

Generic interceptor implementations use public instance methods annotated with
{@link: sting.interceptors.Before @Before}, {@link: sting.interceptors.Around @Around},
{@link: sting.interceptors.After @After}, or {@link: sting.interceptors.AfterException @AfterException}.

{@file_content: file=sting/doc/examples/interceptors/TimingInterceptor.java start_line=@Injectable}

An `@Around` method returns `Object`, may throw `Throwable`, and must have exactly one
{@link: sting.interceptors.Proceed @Proceed} {@link: sting.interceptors.Invocation Invocation}
parameter.

{@file_content: file=sting/doc/examples/interceptors/ValidationInterceptor.java start_line=@Injectable}

The lifecycle order is:

- `@Before`: outer-to-inner.
- `@Around`: wraps the inner interceptor chain or target call at the interceptor's priority position.
- `@After`: inner-to-outer after a successful target call.
- `@AfterException`: inner-to-outer when the target or an inner interceptor fails.

An interceptor's own `@AfterException` method does not observe failures from that same interceptor's `@Before` or
`@After` method. It does observe failures from that same interceptor's `@Around` method, including failures before or
after calling `proceed()`. Outer interceptors still observe failures from inner interceptors.

An around method may call `proceed()` to continue with the active arguments, call `proceed(Object[])` to replace
arguments for inner interceptors and the target, or return without proceeding to short-circuit the invocation.
Invocations may be called multiple times. Each call re-enters the remaining interceptor chain and may invoke
the target service method again.

`proceed(Object[])` requires a non-null array whose length matches the service method's formal parameter count. For
varargs methods, the final formal parameter is the varargs array. Null arrays and wrong counts are checked by generated
assertions when assertions are enabled; wrong element types and null primitive arguments fail naturally when generated
code casts or unboxes the replacement values.

Around return values become the service method result. Void service methods ignore the value. Primitive service
methods unbox it, so null or the wrong wrapper type fails naturally. Runtime exceptions, errors, and checked
exceptions declared by the service method are rethrown unchanged. Other checked throwables are wrapped at the proxy
boundary in `java.lang.reflect.UndeclaredThrowableException`; `@AfterException` receives the original throwable before
that boundary wrapping.

## Metadata Parameters

Every lifecycle method parameter must have exactly one marker annotation from `sting.interceptors`.

Supported metadata:

- {@link: sting.interceptors.ServiceType @ServiceType} `String`: the intercepted service interface name.
- {@link: sting.interceptors.MethodName @MethodName} `String`: the service method name.
- {@link: sting.interceptors.BindingValue @BindingValue} values from the binding annotation.
- {@link: sting.interceptors.Arguments @Arguments} `Object[]`: the active arguments at this interceptor boundary.
- {@link: sting.interceptors.Proceed @Proceed} `Invocation`: the next step for `@Around`.
- {@link: sting.interceptors.Result @Result} `Object`: successful return value for `@After`.
- {@link: sting.interceptors.Thrown @Thrown} `Throwable`: thrown failure for `@AfterException`.

{@file_content: file=sting/doc/examples/interceptors/AuditInterceptor.java start_line=@Injectable}

`@BindingValue` parameters read compile-time annotation member values from the active interceptor binding. Supported
scalar members are `String`, primitive types, `char`, `Class`, and enums. `String` members map to `String`, primitive
and `char` members map to the matching primitive or boxed parameter type, and `Class` or enum members map to `String`
class names or enum constant names.

Array binding members are also supported when their component type is one of the supported scalar member types.
`String[]` and primitive arrays map to the same array type. `Class[]`, `Class<?>[]`, and enum arrays map to `String[]`.
Empty array defaults are supported. Annotation-valued members and annotation-array-valued members are not supported.

`@Arguments` is metadata only. Mutating the array does not rewrite the arguments passed to the target method.
`@Result` is valid only on `@After`, and `@Thrown` is valid only on `@AfterException`; around methods observe results
and failures by surrounding their proceed call.

## Binding Locations

A binding on a service interface applies to every binding that publishes that service interface.

{@file_content: file=sting/doc/examples/interceptors/AccountService.java start_line=@Audited}

A binding on a fragment provider method applies to the service interfaces published by that provider method.

{@file_content: file=sting/doc/examples/interceptors/PaymentFragment.java start_line=@Fragment}

When one binding publishes multiple service interfaces, each intercepted service interface receives its own cached
proxy.

{@file_content: file=sting/doc/examples/interceptors/NotificationFragment.java start_line=@Fragment}

## Injector Requests

All request kinds for an intercepted service coordinate receive the proxy: direct instance requests, optional
requests, suppliers, supplier optionals, collections, supplier collections, and supplier optional collections. The raw
target remains internal to the generated injector and proxy.

{@file_content: file=sting/doc/examples/interceptors/InterceptorsInjector.java start_line=@Injector}
