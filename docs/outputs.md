---
title: Accessing Services managed by the Injector
sidebar_label: Injector Service Outputs
---

The injector is responsible for constructing and linking components together. In many scenarios,
it is desirable for the injector to expose services so that they can be accessed by the host
application. This is done by adding an getter method on the injector. The annotation processor
will treat these getter methods as dependencies that must be resolved. These methods can also
return qualified services if they are annotated with the {@link: sting.Named @Named} annotation
as described in the [naming](naming.md) document.

A simple example to illustrate how services can be accessed from an injector follows.

The injector:

{@file_content: file=sting/doc/examples/outputs/LibraryApplication.java start_line=@Injector}

Using the injector from the host application:

{@file_content: file=sting/doc/examples/outputs/Main.java "start_line=  {" "end_line=  }"}
