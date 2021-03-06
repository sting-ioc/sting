---
title: Customizing Service Types
---

When a component is included in an injector, it is published with zero or more "services". A service consists
of a java type and an optional qualifier. By default, Sting publishes a single service with the java type set
to the type that is annotated by the {@link: sting.Injectable @Injectable} annotation or the return type
of the provider method. Sting makes it possible to customize the service types published by using the
{@link: sting.Typed @Typed} annotation.

The {@link: sting.Typed @Typed} annotation can be applied to either the injectable type or the provider method.
Zero or more types can be specified and thus a single component can publish multiple services or no services.

It should be noted that if a component does not publish any services then it must be annotated with
{@link: sting.Eager @Eager}. If a component publishes zero services that it will never be a dependency of any
other component and if the component is not annotated with the {@link: sting.Eager @Eager} annotation, it would
would never be instantiated.

The easiest way to illustrate how this would work is to present some basic examples.

<nav class="page-toc">

<!-- toc -->

* <a>Examples</a>
  - [@Typed on @Injectable types](#typed-on-injectable-types)
  - [@Typed on provider methods](#typed-on-provider-methods)
  - [Combining @Typed and @Named](#combining-typed-and-named)

<!-- tocstop -->

</nav>

## @Typed on @Injectable types

Consider a scenario where you have a single component that provides multiple services. The following
example demonstrates the `MessageService` that publishes two types. The `MessageSender` type is published
to enable some components in the application to send messages while the `MessageBroker` type is published
so that other components can receive messages.

{@file_content: file=sting/doc/examples/typing/MessageService.java start_line=@Injectable}

It is easy to imagine that we would have another component `LoginService` that performs the asynchronous
login action and sends events as it progresses through each step of the process. The `LoginService` component
would depend upon the `MessageSender` type. For example:

{@file_content: file=sting/doc/examples/typing/LoginService.java start_line=@Injectable}

It also easy to imagine that there are multiple components within the application that will add listeners without
ever needing to send messages. For example:

{@file_content: file=sting/doc/examples/typing/UserHeaderItem.java start_line=@Injectable}

## @Typed on provider methods

The {@link: sting.Typed @Typed} annotation can be applied to provider methods with the same impacts as when
it is applied to an injectable type. We could re-implement the above example but instead of using types
annotated by the {@link: sting.Injectable @Injectable} annotation we could use use provider methods. Such an
example would look like:

{@file_content: file=sting/doc/examples/typing/ApplicationFragment.java start_line=@Fragment}

## Combining @Typed and @Named

The {@link: sting.Typed @Typed} annotation can be combined with the {@link: sting.Named @Named} annotation.
The qualifier specified by the {@link: sting.Named @Named} annotation is applied to all the types published
by the component.

The example below publishes the component of type `MessageService` with two service interfaces; a service
of type `MessageBroker` with the qualifier `system` and a service of type `MessageSender` with the qualifier
`system`.

{@file_content: file=sting/doc/examples/typing/NamedApplicationFragment.java start_line=@Fragment}
