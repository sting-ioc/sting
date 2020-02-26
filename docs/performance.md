---
title: Performance
---

## Build Time

Sting prioritizes decreasing build time of incremental injector rebuilds over rebuilding the entire
application. Sting is often used in the construction of web applications. In this context the application
is typically incrementally rebuilt when the web page is refreshed. Thus optimizing incremental rebuilds
makes the development experience much more pleasant.

When Sting builds a type annotated with with the {@api_url: @Injectable::Injectable} annotation
or the {@api_url: @Fragment::Fragment} annotation, the Sting annotation processor will generate a small, binary
descriptor describing the type. The descriptor includes all the information required by Sting to bind the
type to an injector. When the annotation processor attempts to process a type annotated by the
{@api_url: @Injector::Injector} annotation, the annotation processor will load the binary descriptor rather
than attempting to load and analyze the type.

This results in a small increase in time compiling types annotated with the {@api_url: @Injectable::Injectable}
annotation or the {@api_url: @Fragment::Fragment} annotation as the annotation processor needs to write
the binary descriptor file. As of Java 8, writing a non-java source file from an annotation processor is
relatively slow as it forces a synchronous write to the filesystem from within the compiler. However, the
advantage comes when processing types annotated with the {@api_url: @Injector::Injector} annotation, which is
significantly faster.

Most of the initial Sting applications were migrated from [Dagger2](https://github.com/google/dagger). Dagger2
is a more mature toolkit with significant performance optimizations enabled and as such it is a great target
to compare against. A [sample application](#sample-application-description) representative of real world
applications was developed to evaluate Sting. Sting performs reasonably well for it's intended use case without
any significant performance optimizations implemented.

The table below compares the ratio of the speed of dagger in various scenarios with the speed of Sting. A value
of `1` indicates that they are exactly the same speed while a value of `0.5` would indicate Sting takes twice as
long as Dagger and a value of `2.0` indicates Sting takes half as long as dagger. This performance evaluation was
last run for Sting version `0.5`

It is expected that most applications that use Sting are in the "Medium" category with around 250 objects managed
by the injector. Although it should be noted that when the Sting authors were converting their applications, almost
all of them where closer to the "Large" category.

| Scenario | Object Count | Full Compile | Incremental Recompile |
|----------|--------------|--------------|-----------------------|
| Tiny     | 10           | 1.017        | 1.099                 |
| Small    | 50           | 0.648        | 1.716                 |
| Medium   | 250          | 0.764        | 3.605                 |
| Large    | 500          | 0.710        | 4.745                 |
| Huge     | 1000         | 0.704        | 11.785                |

Stings architecture gives a nice little performance boost for incremental rebuilds in most circumstances with a
slight performance penalty for the initial compile or full rebuilds. As Sting matures, it is expected that the
performance penalty for full rebuilds will decrease slightly but will always exist. There are many further
optimizations possible in incremental recompiles that will likely lead to even faster recompiles in the future.

## Code Size

{@include: CodeSizeTable.html}

## Sample Application Description

The sample application used during performance testing is procedurally generated from a number of input
parameters. The generator first generates an directed graph in memory. (Dagger and Sting only support directed
graphs with circular dependencies replaced with either `javax.inject.Provider` style dependencies in Dagger
or `java.util.function.Supplier` style dependencies in Sting.) The graph consists of a number of layers of nodes
where nodes in one layer can only depend upon nodes in the previous layer.

Initial performance evaluation attempted to assess the performance impact of the number of dependencies for each
node but no statistically significant variation was found in either framework and thus this was eliminated from
the trials. The average and maximum length of dependency chain had an and thus does form part of the testing regime.

Most web applications have a certain percentage of nodes that are expected to be instantiated on application
startup. In web applications, these eager components often perform actions like subscribing to an application
global event broker, adding listeners for browser events, initializing graphics contexts etc.

Sting has built-in support for {@api_url: @Eager::Eager} components that are instantiated when the
injector is constructed while Dagger-based applications often simulate this by adding an accessor to the dagger
component that is invoked early in the application lifecycle. Code-size is impacted by this feature so different
trials were run with different proportions of the object-graph treated as eager.

The object graph in the sample application is intended to be reasonably representative of the type of object
graphs that appear in real-life applications without being unduly biased towards Sting or Dagger. However, the
tests do focus on incremental build time and code-size which are Sting strengths. This is primarily because this
is what Sting is focused upon and intends to improve upon in the future.

It should be noted that the example application only includes `@javax.inject.Inject` annotated types for Dagger
and {@api_url: @Injectable::Injectable} annotated types for Sting and not components provided by `@dagger.Module`
annotated types for Dagger or {@api_url: @Fragment::Fragment} annotated types for Sting. This is primarily because
it was too easy to introduce bias against Dagger in this scenario.
