---
title: Accessing Services managed by the Injector
sidebar_label: Injector Service Outputs
---

The injector is responsible for constructing and linking components together. In many scenarios,
it is desirable for the injector to expose services so that they can be accessed by the host
application. This is done by adding an "output" method on the injector. The methods must be an
abstract instance method that returns the desired service. The annotation processor
will treat these methods as dependencies that must be resolved. These methods can also
return qualified services if they are annotated with the {@link: sting.Named @Named} annotation
as described in the [naming](naming.md) document. These output methods can also be the different
types of dependency described in the [dependency kinds](dependency_kinds.md) document.

When resolving an output, Sting treats primitive and boxed equivalents as the same service. A
request for `Integer` can be satisfied by an `int` provider and a request for `int` can be
satisfied by a non-null `Integer` provider. However, a nullable boxed provider still does not
satisfy a required primitive or required boxed request, and if both primitive and boxed providers
exist for the same qualifier then singular outputs remain ambiguous while collection outputs
contain both bindings.

A simple example to illustrate how services can be accessed from an injector follows.

The injector:

{@file_content: file=sting/doc/examples/outputs/LibraryApplication.java start_line=@Injector}

Using the injector from the host application:

{@file_content: file=sting/doc/examples/outputs/Main.java "start_line=  {" "end_line=  }"}
