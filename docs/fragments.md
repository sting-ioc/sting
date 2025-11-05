---
title: Fragments
---

A {@link: sting.Fragment @Fragment} annotated interface is used to define a subset of the components that
can contribute to a component graph modelled by an injector. The fragment does this through two mechanisms:

* The {@link: sting.Fragment#includes() @Fragment.includes} parameter allows the user to specify additional
  types that will contribute to the component graph.
* The interface may include zero or more default access methods that are invoked to programmatically create
  components. This technique is used when third-party objects can't be annotated, when a different framework
  is responsible for creating the object (i.e `GWT.create(MyGwtRpcService.class)` or when it is awkward to
  create the type (i.e. when publishing a component that is provided by calling a method on another
  component). The documentation often terms these "provider" methods.

It is not uncommon to define a fragment that includes several other types but does not declare any provider
methods. This makes it possible to include several components in an injector by just including a single
fragment. This may be desirable if the application has multiple different injectors or uses different
injectors when writing tests or if the components can be included in multiple independent applications.

For example:

{@file_content: file=sting/doc/examples/fragments/SecurityFragment.java start_line=@Fragment}

At other times, fragments will just include provider methods such as:

{@file_content: file=sting/doc/examples/fragments/RemoteServicesFragment.java start_line=@Fragment}

It is also perfectly acceptable, for a fragment to include both includes and provider methods:

{@file_content: file=sting/doc/examples/fragments/CompilerFragment.java start_line=@Fragment}

## Include Cycles

Sting de-duplicates contributions from `includes`, so adding the same fragment (directly or transitively) multiple times has no runtime effect. However, cyclical includes can make graphs harder to reason about and can hide redundant declarations.

If the annotation processor detects that a fragment includes another fragment that (transitively) includes the origin, a warning is emitted:

- Key: `Sting:FragmentIncludeCycle`
- Example scenario: Fragment `A` includes fragment `B`, and `B` (directly or via other fragments) includes `A`.

To suppress this warning for a fragment, annotate the fragment with:

```java
@SuppressWarnings("Sting:FragmentIncludeCycle")
@Fragment( /* ... */ )
public interface MyFragment { }
```

This warning is informational only; it does not change the generated code or runtime behaviour.
