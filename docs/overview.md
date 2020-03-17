---
title: Overview
---

Sting models the application as a set of components. An injector is responsible for constructing
and linking components together. Components are linked to other components by services. A service
is just a java type with an optional string qualifier. A component can consume services published
by other components and/or publish services for other components to consume.

Sting performs builds a directed graph of components at compile time, linking components via services.
Unless a service is marked as optional then there must exist a component that publishes a service
if another component requires the service. The compiler analyzes the graph to ensure that is well
formed and contains no cycles. If the analysis is successful then Sting will generate java source
code to implement the injector.

Sting can create components directly if they are annotated with the {@link: sting.Injectable @Injectable} annotation
or it can call out to user code to provide the component (i.e. methods in {@link: sting.Fragment @Fragment} annotated
types.) The second form is used when third-party objects can't be annotated, when a different framework is
responsible for creating the object (i.e `GWT.create(MyGwtRpcService.class)` or when it is awkward to
create the type (i.e. when publishing a component that is constructed by invoking methods on a builder object).
Sting can also pass in components when creating the injector.

Sting generates the code injector with no reference to sting-specific implementation classes other than
the Sting annotations. The code generated is intended to be easy and very similar to the code a human
would write by hand if given this task.

Sting is an opinionated framework but the opinions are designed to make efficient code generation
possible and/or to guide users away from practices that can lead to problems. Some of these "opinions"
are effectively "code style" issues and can be suppressed but others are intrinsic to the way Sting
is built.

Sting attempts to give user-friendly error messages when things go wrong and helpful guides to get
get started. A primary goal of Sting is to be easy to use, and this includes clear and concise documentation.
If something is unclear please [report it as a bug](https://github.com/sting-ioc/sting/issues) because it *is*
a bug. If a new user has a hard time, then we need to fix the problem.
