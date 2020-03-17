---
title: Including an Injector
---

An injector defines a graph of components with service [inputs](inputs.md) and service [outputs](outputs.md).
So it would be possible to create a fragment with appropriate provider methods so that the injector can be
included in a different injector.

Imagine an injector defined by:

{@file_content: file=sting/doc/examples/include_injector/manual/LibraryApplication.java start_line=@Injector}

This injector could be exposed using a fragment such as:

{@file_content: file=sting/doc/examples/include_injector/manual/LibraryApplicationFragment.java start_line=@Fragment}

Rather than mechanically translating the injectors inputs and outputs into a fragment, Sting can generate
an equivalent infrastructure infrastructure for you if you set the {@link: sting.Injector#injectable() @Injector.injectable}
parameter to `true`. The generated code is of the form:

{@file_content: path=generated/processors/main/java file=sting/doc/examples/include_injector/auto/Sting_LibraryApplication_Provider.java start_line=@Fragment}

The generated fragment could be then included in the {@link: sting.Injector#includes() @Injector.includes}
and/or the {@link: sting.Fragment#includes() @Fragment.includes} parameters. However, the implementation also
uses the ["external" framework integration](framework_integration.md) techniques so it is possible to just
include the injector class directly in the includes. This avoids the developer having to remember the name of
the generated fragment.

For example, the following is perfectly acceptable:

{@file_content: file=sting/doc/examples/include_injector/auto/AcademyApplication.java start_line=@Injector}
