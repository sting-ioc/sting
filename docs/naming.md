---
title: Customizing Service Qualifiers
---

Sometimes an injector contains multiple instances of a component that are published using the same type.
(For details on how to customize service types see the ["Customizing Service Types"](typing.md) document.)
In this scenario it is useful to distinguish different services of the same type bu different semantics
using a "qualifier". Within Sting, a qualifier is an opaque, user-supplied string that is specified using
the {@link: sting.Named @Named} annotation. The {@link: sting.Named @Named} annotation can be added to to
where a service is published or where a service is consumed.

The publishing locations include on a type annotated by the {@link: sting.Injectable @Injectable}
annotation or a provider method enclosed within a type annotated by the {@link: sting.Fragment @Fragment}
annotation. A service is consumed by being passed as; constructor parameters in injectable components or
as method parameters passed to provider methods. These parameter can also be annotated with the
{@link: sting.Named @Named} annotation. When sting compiles the injector, it ensures that the consumer service
dependency can only be satisfied by producers if they have the same qualifier.

## Qualified Components

Consider an application that consists of many components that all publish the same service interface
`SimulationSystem` but need to be connected in specific topologies. This is possible to implement using
the {@link: sting.Named @Named} annotation.

The `DynamicLightingSystem` publishes a service with the `SimulationSystem` type and the qualifier
`"system:lighting"`.

{@file_content: file=sting/doc/examples/naming/DynamicLightingSystem.java start_line=@Injectable}

This could be consumed by another component defined in a provider method:

{@file_content: file=sting/doc/examples/naming/SimulationFragment.java start_line=@Fragment}

The component could also be consumed by other injectable components:

{@file_content: file=sting/doc/examples/naming/GeometryProcessor.java start_line=@Injectable}

## Qualified Values

While qualified components occasionally occur in applications, it is far more common to
see the {@link: sting.Named @Named} annotation used to when configuring the application with
multiple instances of primitive or immutable values. For instance a component could accept
multiple parameters of type string for different configuration settings. For example:

{@file_content: file=sting/doc/examples/naming/HttpClient.java start_line=@Injectable}

## Name Format

The actual value of the string is relatively arbitrary and you should use whatever makes sense
within your application. Some projects use reverse DNS naming to guarantee uniqueness, others use
short prefixes for namespacing while other projects use short local names.
