---
title: Component Auto-Discovery
---

Rather than explicitly listing a component in the {@link: sting.Injector#includes() @Injector.includes} parameter,
Sting can auto-discover a component. Auto-discovery makes construction of injectors easier. Auto-discovery also
eliminates one place where code often conflicts merging features from different branches.

Sting supports two auto-discovery modes:

* Direct auto-discovery for {@link: sting.Injectable @Injectable} types.
* Provider-backed auto-discovery for types annotated with an annotation annotated by
  {@link: sting.StingProvider @StingProvider}.

For direct {@link: sting.Injectable @Injectable} auto-discovery, the component must comply with the following
constraints:

* The component is annotated with the {@link: sting.Injectable @Injectable} annotation.
* The component is not annotated with the {@link: sting.Named @Named} annotation or is annotated and specifies
  the default qualifier value `""`.
* The component is not annotated with the {@link: sting.Typed @Typed} annotation or is annotated and specifies
  the default value for services published. (i.e. A single published service with the same type as the component type).

For provider-backed auto-discovery:

* The unresolved dependency must request the default qualifier value `""`.
* The requested type must be annotated with an annotation annotated by
  {@link: sting.StingProvider @StingProvider}.
* The resolved provider type must be annotated with either {@link: sting.Fragment @Fragment} or
  {@link: sting.Injectable @Injectable}.
* The resolved provider type must publish the requested type with the default qualifier value `""`.

Using auto-discovered components makes specifications less laborious
