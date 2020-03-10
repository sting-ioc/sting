---
title: Performance
---

Sting is primarily used in java web applications compiled to javascript using either
[GWT](http://www.gwtproject.org/) or the more modern [J2CL](https://github.com/google/j2cl) and was
developed with specific performance goals in mind.

* Fast incremental build times.
* Small code size.
* Fast initialization time.

Fast incremental build times make developers happy and increase productivity. Fast refresh times is
essential for modern web development. Small code size and fast initialization times make the end users
of our websites happy.

The performance is measure relative to [Dagger](https://github.com/google/dagger) as most of the initial
Sting applications were migrated from Dagger. Dagger is a much more mature, established and reasonably
optimized library and thus is a great library to compare against.

A procedurally generated ["sample application"](#sample-application-description) was developed to be
representative of real world application usage of dependency injectors. This sample application helped
keep the focus on Stings the performance goals.

## Build Time

Sting optimizes incremental injector rebuilds over rebuilding an entire application. When Sting processes
a type annotated with either the {@link: sting.Injectable @Injectable} annotation or the
{@link: sting.Fragment @Fragment} annotation, the Sting annotation processor will generate a small, binary
descriptor describing the type. The descriptor includes all the information required by Sting to bind the
type to an injector. When the annotation processor attempts to process a type annotated by the
{@link: sting.Injector @Injector} annotation, the annotation processor will load the binary descriptors rather
than attempting to load and analyze the type.

This results in a small increase in time compiling types annotated with the {@link: sting.Injectable @Injectable}
annotation or the {@link: sting.Fragment @Fragment} annotation as the annotation processor needs to write
the binary descriptor file. As of Java 8, writing a non-java source file from an annotation processor is
relatively slow as it forces a synchronous write to the filesystem from within the compiler. However, reading
the binary descriptor rather than the java type when processing types annotated with the
{@link: sting.Injector @Injector} annotation is significantly faster.

The table below compares the ratio of the speed of dagger in various scenarios with the speed of Sting. A value
of `1` indicates that they are exactly the same speed while a value of `0.5` would indicate Sting takes twice as
long as Dagger and a value of `2.0` indicates Sting takes half as long as dagger.

{@include: BuildTimesTable.html}

Stings architecture gives a nice little performance boost for incremental rebuilds in most circumstances with a
slight performance penalty for the initial compile or full rebuilds. As Sting matures, it is expected that the
performance penalty for full rebuilds will decrease slightly but will always exist. There are many further
optimizations possible in incremental recompiles that will likely lead to even faster recompiles in the future.

## Code Size

Sting prioritizes smaller code size and the builtin support for {@link: sting.Eager @Eager} components
and the ease of optimizing when eager components are present is a significant contributor to Stings
relatively good performance in this area.

Most web applications contain "eager" components that are expected to be instantiated on application
startup to perform actions like subscribing to an event broker, adding listeners for browser events,
initializing graphics contexts etc. Dagger-based applications often implement eager components by
adding an accessor for the eager component onto the dagger injector and that accessor is invoked early
in the application lifecycle.

{@include: CodeSizeTable.html}

As expected, Sting has much smaller code sizes than Dagger when high proportions of the components are
"eager". Surprisingly, Sting also has smaller code sizes when all the components are lazy.

## Sample Application Description

The sample application used during performance testing is procedurally generated from a number of input
parameters. The generator first generates an directed graph in memory. (Dagger and Sting only support directed
graphs with circular dependencies replaced with either `javax.inject.Provider` style dependencies in Dagger
or `java.util.function.Supplier` style dependencies in Sting.) The graph consists of a number of layers of nodes
where nodes in one layer can only depend upon nodes in the previous layer.

Several different configurations or "variants" were used during performance evaluation. It was found that
the most important aspect was how big the component graph was and what proportion of the components were
eagerly created. So we developed "tiny", "small", "medium", "large" and "huge" variants that had 50% of the
components as eager. We also developed variants where 0% or 100% of the components are eager and these
variants had names prefixed with "lazy_" and "eager_" respectively.

It is expected that most applications that use Sting are in the "medium" or "eager_medium" category. Although
it should be noted that when the Sting authors were migrating applications to Sting, almost all of the
applications where closer to the "large" category.

The number of inputs into a component seemed to have no significant performance impact in either application
but the length of dependency chain does have an impact.

<table>
  <caption align="bottom">Variants Parameters</caption>
  <thead>
  <tr>
    <th>Variant</th>
    <th>Component Count</th>
    <th>Eager %</th>
    <th>Layer Count</th>
  </tr>
  </thead>
  <tbody>
  <tr>
    <td>Eager Tiny</td>
    <td>10</td>
    <td>100%</td>
    <td>2</td>
  </tr>
  <tr>
    <td>Tiny</td>
    <td>10</td>
    <td>50%</td>
    <td>2</td>
  </tr>
  <tr>
    <td>Lazy Tiny</td>
    <td>10</td>
    <td>0%</td>
    <td>2</td>
  </tr>
  <tr>
    <td>Eager Small</td>
    <td>50</td>
    <td>100%</td>
    <td>5</td>
  </tr>
  <tr>
    <td>Small</td>
    <td>50</td>
    <td>50%</td>
    <td>5</td>
  </tr>
  <tr>
    <td>Lazy Small</td>
    <td>50</td>
    <td>0%</td>
    <td>5</td>
  </tr>
  <tr>
    <td>Eager Medium</td>
    <td>250</td>
    <td>100%</td>
    <td>5</td>
  </tr>
  <tr>
    <td>Medium</td>
    <td>250</td>
    <td>50%</td>
    <td>5</td>
  </tr>
  <tr>
    <td>Lazy Medium</td>
    <td>250</td>
    <td>0%</td>
    <td>5</td>
  </tr>
  <tr>
    <td>Eager Large</td>
    <td>500</td>
    <td>100%</td>
    <td>5</td>
  </tr>
  <tr>
    <td>Large</td>
    <td>500</td>
    <td>50%</td>
    <td>5</td>
  </tr>
  <tr>
    <td>Lazy Large</td>
    <td>500</td>
    <td>0%</td>
    <td>5</td>
  </tr>
  <tr>
    <td>Eager Huge</td>
    <td>1000</td>
    <td>100%</td>
    <td>10</td>
  </tr>
  <tr>
    <td>Huge</td>
    <td>1000</td>
    <td>50%</td>
    <td>10</td>
  </tr>
  <tr>
    <td>Lazy Huge</td>
    <td>1000</td>
    <td>0%</td>
    <td>10</td>
  </tr>
  </tbody>
</table>

The object graph in the sample application is intended to be reasonably representative of the type of object
graphs that appear in real-life applications without being unduly biased towards Sting or Dagger. However, the
tests do focus on incremental build time, initialization time and code-size which are Sting strengths. This is
primarily because this is what Sting is focused upon and intends to improve upon in the future.

It should be noted that the example application only includes `@javax.inject.Inject` annotated types for Dagger
and {@link: sting.Injectable @Injectable} annotated types for Sting and not components provided by `@dagger.Module`
annotated types for Dagger or {@link: sting.Fragment @Fragment} annotated types for Sting. This is primarily because
it was too easy to introduce bias against Dagger in this scenario.
