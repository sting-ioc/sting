---
title: Getting Started
---

Sting models an application as a set of components. So lets create our first component. The
component will be called `WelcomePage` and it is responsible for generating html to welcome
the user to our application. The simples way to create a component is to create a class and
annotate it with the {@link: sting.Injectable @Injectable} annotation.

The component may look like:

{@file_content: file=sting/doc/examples/getting_started/step1/WelcomePage.java start_line=@Injectable}

At a later stage we may want to create the user using their username rather than the generic
term "user". The currently authenticated user is available from another service named
`AuthenticationService`. To make this component available to the `WelcomePage` component we
pass the component in via a constructor parameter. Sting is responsible for creating the
`WelcomePage` component and will ensure that the `AuthenticationService` service is available
and passed to the `WelcomePage` component when it is created.

After this change, the component may look like:

{@file_content: file=sting/doc/examples/getting_started/step2/WelcomePage.java start_line=@Injectable}

The next step is to get Sting creating the injector responsible for constructing the components. An
injector is a java interface that is annotated by the {@link: sting.Injector @Injector} annotation.

To tell the framework which components are available managed by the injector the user may specify
the types of the components in the {@link: sting.Injector#includes() @Injector.includes} parameter.
It should be noted that this only specifies that component may potentially be part of the component
graph managed by the injector and not whether the component is actually part of the component graph.

The Sting annotation processor will analyze the injector interface and identify the root set of
components required by looking for accessors on the injector and will add these to the component
graph. Then it will recursively include all required dependencies of the root components.

To ensure the injector includes the `WelcomePage` component we add an getter for the component to
the injector and our injector may look like:

{@file_content: file=sting/doc/examples/getting_started/step2/WebApplication.java start_line=@Injector}

If we compile this class with the Sting annotation processor present it will generate an injector
implementation named `Sting_WebApplication`. The generated implementation is package access and most
Sting applications add a static method on the injector interface to create the implementation.
For example:

{@file_content: file=sting/doc/examples/getting_started/step3/WebApplication.java start_line=@Injector}

An example where the injector implementation is used by the host application may be as simple as:

{@file_content: file=sting/doc/examples/getting_started/step3/Main.java "start_line=  {" "end_line=  }" include_start_line=false include_end_line=false}

## Next Steps

The section above gives you enough to get started and get your feet wet using Sting but Sting includes
so much more. It is recommended that you wander through the rest of the documentation to learn the other
features available in Sting. If there is anything further you need, feel free to reach out via the
[issue tracker](https://github.com/sting-ioc/sting/issues) and voice your requests, comment and/or concerns.

Here are some links to other documents that you may want to look at next.

* [Fragments](fragments.md): Fragments are used to create components when third-party objects can not be
  annotated or when other toolkits are responsible for instantiating the object etc. Fragments can also be
  define a partial component graph that can be reused across injectors.

* [Component Autodiscovery](autodiscovery.md): It is not always required to add components to the
  {@link: sting.Injector#includes() @Injector.includes} parameter as the framework has builtin mechanism
  that make autodiscovery of components possible.

* [Eager Components](eager.md): Eager components are constructed when the injector is constructed and also
  tell the injector to treat the component as a root even if it is not exposed by an accessor method on the
  injector.

* [Typing Components](typing.md): A component can be exposed to other components in the injector using
  specific types or not be accessible as a dependency for other components at all.

* [Naming Components](naming.md): A component can be exposed to other components using a specific
  qualifier/label/name which helps distinguish the component from other components of the same type.

* [Injector Inputs](inputs.md): A host application can pass in services when constructing the injector and
  these services can be made available to other components within the injector.
