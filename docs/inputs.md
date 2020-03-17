---
title: Injecting Services into Injector
sidebar_label: Injector Service Inputs
---

When creating creating an injector it is common to want to pass services and values into the injector
that are managed externally. You can pass in environment-specific components such as a `ServletContext`
when running in a servlet container or configuration settings in other contexts.

To implement this in Sting, the developer must specify the required services using the
{@link: sting.Injector#inputs() @Injector.inputs} parameter. The {@link: sting.Injector#inputs() @Injector.inputs}
parameter accepts an array of annotations that define the service interface using a [type](typing.md),
[qualifier](naming.md) and a flag indicating whether the service is optional.

Sting will generate an injector with a constructor that accepts the input services as parameters in
the order specified and using the types specified. Optional service inputs will also be annotated
with the `@Nullable` annotation.

For example, the injector defined by:

{@file_content: file=sting/doc/examples/inputs/MyInjector.java start_line=@Injector}

produces an injector implementation with a constructor with the signature:

{@file_content: path=generated/processors/main/java file=sting/doc/examples/inputs/Sting_MyInjector.java start_line=Sting_MyInjector\( end_line=\)}
