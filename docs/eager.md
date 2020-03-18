---
title: Eager Components
---

Sting supports both lazily created components and eagerly created components. An component annotated with the
{@link: sting.Eager @Eager} annotation is considered eager as are all of the transitive non-supplier dependencies
of the component. Eager components are created when the injector is constructed. Lazy components on the other hand
are only created when the injector attempts to access the component.

A lazy component is useful when it is only accessed via a [supplier](dependency_kinds.md) dependency or
if the component is accessed as a result of invoking an [output](outputs.md) method on an injector. Lazy
components require additional book-keeping to ensure that they are not accessed before they are constructed.
As a result they can increase the code-size unnecessarily if used inappropriately. See the
[performance](performance.md) document for further details.

The easiest way to understand it is look at example where we have a single component annotated with the
{@link: sting.Eager @Eager} annotation. The code for this component looks like:

{@file_content: file=sting/doc/examples/eager/MyComponent3.java start_line=@Injectable}

We would expect that the component `MyComponent1` and `MyComponent3` would be constructed when the injector is
constructed as `MyComponent3` is explicitly annotated with the {@link: sting.Eager @Eager} annotation and
`MyComponent1` is a dependency of `MyComponent3`. `MyComponent2` is not constructed until the
first time that the `MyComponent3.performAction()` method is invoked as it invokes the `get()` method on the
supplier.
