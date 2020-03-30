---
title: Component Auto-Discovery
---

Rather than explicitly listing a component in the {@link: sting.Injector#includes() @Injector.includes} parameter,
Sting can auto-discover a component. Auto-discovery makes construction of injectors easier. Auto-discovery also
eliminates one place where code often conflicts merging features from different branches.

For a component to be auto-discoverable, it must comply with the following constraints:

* The component is annotated with the {@link: sting.Injectable @Injectable} annotation.
* The component is not annotated with the {@link: sting.Named @Named} annotation or is annotated and specifies
  the default qualifier value `""`.
* The component is not annotated with the {@link: sting.Typed @Typed} annotation or is annotated and specifies
  the default value for services published. (i.e. A single published service with the same type as the component type).

Using auto-discovered components makes specifications less laborious
