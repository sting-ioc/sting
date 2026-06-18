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

The `implementedBy` value is the canonical dotted name of an {@link: sting.Injectable @Injectable} interceptor
implementation. Lower `priority` values run outermost. Equal effective priorities for one intercepted service are
compile errors.

Bindings may be placed on:

- service interface types,
- injectable implementation classes, and
- fragment provider methods.

Method-level bindings on service methods or implementation methods are not supported.

## Lifecycle Methods

Generic interceptor implementations use public instance methods annotated with
{@link: sting.interceptors.Before @Before}, {@link: sting.interceptors.After @After}, or
{@link: sting.interceptors.AfterException @AfterException}.

{@file_content: file=sting/doc/examples/interceptors/TimingInterceptor.java start_line=@Injectable}

The lifecycle order is:

- `@Before`: outer-to-inner.
- `@After`: inner-to-outer after a successful target call.
- `@AfterException`: inner-to-outer when the target or an inner interceptor fails.

An interceptor's own `@AfterException` method does not observe failures from that same interceptor's `@Before` or
`@After` method. Outer interceptors still observe failures from inner interceptors.

## Metadata Parameters

Every lifecycle method parameter must have exactly one marker annotation from `sting.interceptors`.

Supported metadata:

- {@link: sting.interceptors.ServiceType @ServiceType} `String`: the intercepted service interface name.
- {@link: sting.interceptors.MethodName @MethodName} `String`: the service method name.
- {@link: sting.interceptors.BindingValue @BindingValue} scalar values from the binding annotation.
- {@link: sting.interceptors.Arguments @Arguments} `Object[]`: the original target arguments.
- {@link: sting.interceptors.Result @Result} `Object`: successful return value for `@After`.
- {@link: sting.interceptors.Thrown @Thrown} `Throwable`: thrown failure for `@AfterException`.

{@file_content: file=sting/doc/examples/interceptors/AuditInterceptor.java start_line=@Injectable}

`@Arguments` is metadata only. Mutating the array does not rewrite the arguments passed to the target method.

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

## Plugin Bindings

Processor-path plugins may claim a binding and emit dependency-free lifecycle code directly. A binding intended only
for plugins leaves `implementedBy` empty and must be claimed by exactly one plugin for every effective intercepted
service coordinate.

{@file_content: file=sting/doc/examples/interceptors/PluginOnly.java start_line=@InterceptorBinding}

Plugin emission is intentionally lifecycle-only. Plugins cannot add graph dependencies, fields, helper methods,
constructor parameters, imports, or runtime service requests.

Plugin implementations compile against `org.realityforge.sting:sting-processor-spi` and import SPI types from
`sting.processor.spi.*`. Register plugin implementations in
`META-INF/services/sting.processor.spi.InterceptorCodeGenerator`.

The `sting-processor` artifact contains the SPI classes in the same `sting.processor.spi` package so annotation
processor users can keep only `sting-processor` and plugin jars on the processor path. The old
`sting.processor.*` SPI imports and `META-INF/services/sting.processor.InterceptorCodeGenerator` descriptor are not
supported.
