---
title: Component Graph Construction
---

As part of the compilation phase, injectors build a list of potential components. The potential components
(sometimes referred to as bindings) are collected using the follow algorithm.

1. Any {@link: sting.Injectable @Injectable}, {@link: sting.Fragment @Fragment} or {@link: sting.StingProvider @StingProvider}
   annotated types that declared in the {@link: sting.Injector#includes() @Injector.includes} parameter are added to
   the "work list".
2. Any {@link: sting.Injectable @Injectable} or {@link: sting.Fragment @Fragment} annotated types that are enclosed
   by the injector type are added to the "work list". i.e. The types annotated by the {@link: sting.Injectable @Injectable}
   or {@link: sting.Fragment @Fragment} annotations that are directly nested within the injector class are added.
3. Take one type of the "work list" and process this type until there are no types left on the work list.
   * A type that is annotated with the {@link: sting.Injectable @Injectable} annotation is directly added to
     the list of bindings.
   * A type annotated with the {@link: sting.Fragment @Fragment} annotation add all the components defined
     by the provider methods to the set of bindings and add all the types declared as part of the
     {@link: sting.Fragment#includes() @Fragment.includes} to the "work list".
   * Types annotated by the {@link: sting.StingProvider @StingProvider} annotation are processed according to
     the algorithm described in the [framework integration](framework_integration.md) document which may result
     in more types being added to the "work list".
4. From the potential components, identify the root components and add them to the set of actual components included
   in the injector. The "root" components include components that publish the [output](outputs.md) services as well
   as any potential component that is annotated with the {@link: sting.Eager @Eager} annotation. For every component
   added to the actual component set, resolve the dependencies of the components and add any component required to
   satisfy the dependencies.
5. If a dependency of a component can not be resolved by the components in to set of potential components then attempt
   to lookup the component using the [auto-discovery](autodiscovery.md) process. An error will be generated if a
   non-optional dependency remains unresolved at this stage.
6. All the components that are added to the injector are ordered so that dependencies are created before consumer
   components.
7. Any components that are annotated with the {@link: sting.Eager @Eager} annotation will have their eagerness
   propagated to all dependencies.

This component graph construction process ensures that the minimal set of components is created in an order that
ensures no dependency constraints are violated.
