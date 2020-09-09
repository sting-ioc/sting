# Change Log

### Unreleased

* Upgrade the `org.realityforge.org.jetbrains.annotations` artifact to version `1.6.0`.
* Upgrade the `javax` artifact to version `8.0`.
* Upgrade the `com.squareup` artifact to version `1.13.0`.

### [v0.16](https://github.com/sting-ioc/sting/tree/v0.16) (2020-06-07) · [Full Changelog](https://github.com/sting-ioc/sting/compare/v0.15...v0.16)

Changes in this release:

* Upgrade the `org.realityforge.proton` artifacts to version `0.51`.
* Upgrade the `org.realityforge.braincheck` artifact to version `1.29.0`.
* Improve code generation when a `@Fragment` annotated type or an `@Injector` annotated type is effectively deprecated by virtual of being enclosed in a deprecated type so that the generated code suppresses deprecation warnings.

### [v0.15](https://github.com/sting-ioc/sting/tree/v0.15) (2020-04-23) · [Full Changelog](https://github.com/sting-ioc/sting/compare/v0.14...v0.15) · [API Differences](https://sting-ioc.github.io/api-diff?key=sting&old=0.14&new=0.15)

The release includes 1 non breaking API change.

Changes in this release:

* Upgrade the `org.realityforge.proton` artifacts to version `0.46`.
* Generate an error if a `@Fragment` annotated type includes itself.
* Generate an error if an `@Injector` annotated type includes itself.
* Generate an error if an `@Injector` annotated type includes a `@Fragment` annotated type, an `@Injectable` annotated type or `@StingProvider` type that is not used within the graph.
* Introduce the `@sting.InjectorFragment` annotation that marks an interface that is allowed to have `@Named` annotations present as it is expected to be inherited by an injector.
* Generate an error if a type annotated with `@Injector` or any super types, contains a method with the `default` qualifier.

### [v0.14](https://github.com/sting-ioc/sting/tree/v0.14) (2020-04-06) · [Full Changelog](https://github.com/sting-ioc/sting/compare/v0.13...v0.14) · [API Differences](https://sting-ioc.github.io/api-diff?key=sting&old=0.13&new=0.14)

The release includes 1 non breaking API change.

Changes in this release:

* Fix the grammar in some annotation processor error messages.
* Introduce the `@ActAsStingComponent` annotation that makes it possible to use `@Named` and `@ContributeTo` annotations in types that are not annotated with either `@Injectable` or `@StingProvider`. The `@ActAsStingComponent` is used by third party frameworks to indicate that the third party framework will manage the `@Named` and `@ContributeTo` annotations.

### [v0.13](https://github.com/sting-ioc/sting/tree/v0.13) (2020-03-30) · [Full Changelog](https://github.com/sting-ioc/sting/compare/v0.12...v0.13)

Changes in this release:

* Fix the implementation of auto-discovery so that it behaves as documented. Prior to this fix auto-discovering would work if the qualifiers matched and at least one of the published types matched. This behaviour meant that another pass would be required to validate that auto-discovered types did not validate previously processed components dependency resolution.

### [v0.12](https://github.com/sting-ioc/sting/tree/v0.12) (2020-03-30) · [Full Changelog](https://github.com/sting-ioc/sting/compare/v0.11...v0.12)

Changes in this release:

* Generate an error if a primitive type is added to `includes` parameter of either the `@Fragment` annotation or the `@Injector` annotation.
* Enable `-Werror` when compiling the javac project to ensure that all javac warnings are fixed immediately.
* Fix a potentially infinite loop in `StingProcessor` resulting from java class being resolved but the annotation processor failing to process the type to produce a descriptor due to code warnings.
* Generate a suppressable warning if an auto-discoverable `@Injectable` annotated type is annotated with `@ContributeTo` or is added to the `includes` parameter of either the `@Fragment` annotation or the `@Injector` annotation.
* Add some basic documentation on the following topics:
  - The algorithm for creating the Component Graph
  - Auto-Discovery of Components

### [v0.11](https://github.com/sting-ioc/sting/tree/v0.11) (2020-03-25) · [Full Changelog](https://github.com/sting-ioc/sting/compare/v0.10...v0.11)

Changes in this release:

* Add some additional debug logging in the annotation processor to make debugging the processor in downstream applications easier.
* Injectors and fragments that are unresolved within a single annotation processing round due to missing descriptors could be due to the class being compiled but the annotation processor not processing the class yet. The annotation processor has been changed to iterate over the injectors and fragments within a round multiple times until an iteration fails to resolve an element.

### [v0.10](https://github.com/sting-ioc/sting/tree/v0.10) (2020-03-25) · [Full Changelog](https://github.com/sting-ioc/sting/compare/v0.09...v0.10)

Changes in this release:

* Generate an error if an `@Injector` annotated type encloses a type that is not annotated with either `@Injectable` or `@Fragment`.
* Eliminate a crash when a primitive type dependency was missing and the processor attempted to auto-discover the type.
* Generate an error if an auto-included type is explicitly included in the includes of an `@Injector`. This means that any `@Injectable` or `@Fragment` directly enclosed within an injector type will generate an error.
* Generate an error if an `includes` parameter of either the `@Injector` annotation or the `@Fragment` annotation contains duplicate values.
* Generate an error if a `@Fragment` annotated type encloses any types.
* Rename the field that contains the dependency kind from "type" to "kind" in the json output.

### [v0.09](https://github.com/sting-ioc/sting/tree/v0.09) (2020-03-19) · [Full Changelog](https://github.com/sting-ioc/sting/compare/v0.08...v0.09) · [API Differences](https://sting-ioc.github.io/api-diff?key=sting&old=0.08&new=0.09)

The release includes 2 non breaking API changes.

Changes in this release:

* Upgrade the `org.realityforge.proton` artifacts to version `0.44`.
* Add some basic documentation on the following topics:
  - Getting Started
  - Fragments
  - Kinds of Dependencies
* Cleanup the organization of the documentation by introducing the "Essential" and "Advanced" sections.
* Significantly improve the language and grammar in existing documentation.
* Add some additional debug logging in the annotation processor to make debugging the processor in downstream applications easier.
* Generate an error if a dependency of a component is attempts to recompile fails without causing the consumer component to recompile and the injector attempts to build a component graph using the component. Prior to this fix, Sting would generate a null pointer exception. After this fix, Sting produces a more reasonable error message.
* Fix a bug where `@Eager` annotated components that included in an injector transitively via `@Fragment` annotated types.
* Add initial implementation of `@AutoFragment` annotated types to simplify generation of fragments by gathering candidate components from the classpath. This is considered, an advanced, experimental feature that may change in the future. Look at the documentation for further details.

### [v0.08](https://github.com/sting-ioc/sting/tree/v0.08) (2020-03-13) · [Full Changelog](https://github.com/sting-ioc/sting/compare/v0.07...v0.08)

Changes in this release:

* Add some basic documentation on the following topics:
  - Accessing services managed by the injector
* Enhance the error message on unresolved injectors to suggest using the `sting.debug` annotation processor option.
* Add significantly more debug logging to the annotation processor to simplify tracking down unresolved injector errors.
* Ensure that the processor will correct defer an injector to a later round if a transitive include of an `@Injectable` type has not been generated yet but is expected to be generated by another annotation processor.

### [v0.07](https://github.com/sting-ioc/sting/tree/v0.07) (2020-03-12) · [Full Changelog](https://github.com/sting-ioc/sting/compare/v0.06...v0.07)

Changes in this release:

* Generate an error if a type annotated with `@Injectable` or a method in a `@Fragment` annotated type is annotated with `@Named` and specifies zero types with a `@Typed` annotation. There is no reason to specify a qualifier if the binding has no published types.
* Move all of the TODOs out of the project and into the Github issue tracker. Setup some milestones so the issues can be categorized according to expected releases.
* Dramatically improve the usability of the documentation infrastructure to make writing documentation easier. This involved using java-style api links, making api docs available when running docusaurus locally, removing historical cruft from earlier projects that used the current docusaurus infrastructure.
* Start to add some basic documentation on the following topics:
  - Typing of published services
  - Qualifying of consumed and published services
  - Passing services into the injector
  - Including one injector in another injector
  - Annotation processor options
* Fix a bug in the annotation processor which would stop Sting loading descriptor data from the platform or boot classpath. In an ideal world, there should be no sting annotated classes loaded from the platform classpath but some tools (i.e. the IDEA javac process invoked to compile test dependencies) will add dependencies to the platform classpath. As a result Sting has been updated to support this usecase.
* Improve the grammar of the error messages when the Sting annotation processor can find a .class file but can not find the expected descriptor file.
* Add additional debug messages to the Sting annotation processor that describe why a type is not yet resolved. This can help track down errors relating to unresolved injectors.
* Add some additional nullability annotations in the generated fragment that supports injecting injectors in other injectors.

### [v0.06](https://github.com/sting-ioc/sting/tree/v0.06) (2020-03-06) · [Full Changelog](https://github.com/sting-ioc/sting/compare/v0.05...v0.06)

Changes in this release:

* If an output method on a `@Injector` annotated type returns a collection then it will now return the same collection if invoked multiple times.
* Improve the overview documentation.
* Add some basic documentation comparing the features of Sting relative to Dagger.
* Cache whether a fragment has been resolved across processor rounds. This avoids recalculating the "resolved" state of a fragment when it has already calculated as resolved within the current round or in a prior round.
* Upgrade the `org.realityforge.proton` artifacts to version `0.41`.
* Fix a bug where a fragment has not been "resolved" and thus processed before the annotation processor attempts to generate the injector that uses the fragment. This scenario can occur when another annotation processor is responsible for creating the fragment and the other annotation processor creates the fragment in a later round.
* Add the option of generating a [.dot](https://en.wikipedia.org/wiki/DOT_(graph_description_language)) report that represents the component graph. The report is generated by the annotation processor when the injector implementation is generated and the annotation parameter `sting.emit_dot_reports` is set to `true`. This is typically done by passing the `-Asting.emit_dot_reports=true` parameter to the javac compiler.
* Fix the ordering of node creation in an injector to ensure dependencies are always created first. Prior to this the ordering was based on maximum depth of the node in any dependency chain which could result in attempts to create nodes with dependencies that had not been created yet.

### [v0.05](https://github.com/sting-ioc/sting/tree/v0.05) (2020-02-27) · [Full Changelog](https://github.com/sting-ioc/sting/compare/v0.04...v0.05) · [API Differences](https://sting-ioc.github.io/api-diff?key=sting&old=0.04&new=0.05)

The release includes 3 non breaking API changes.

Changes in this release:

* Add the `@Injector.injectable` parameter that that controls whether the annotation processor will generate a provider so that the injector can be included in other injectors.
* Add some nullability annotations to the parameters of annotations in the `core` package.
* Add the `@Injector.gwt` parameter that that controls whether the annotation processor will customize injector implementation to work within the context of GWT. This primarily involves the addition of the `@DoNotInline` annotation to lazy component accessors within the injector implementation. This avoids the scenario where the GWT compiler could inline a component accessor and all transitive lazy component accessors, significantly increasing code-size, compilation time and run time.
* Add `synchronized` keyword to node accessors in the generated injector to avoid problems resulting arising from concurrent attempts to access nodes in a jre context. When transpiled to javascript, concurrent access is impossible and synchronized keyword is ignored. In a JRE context, this serializes node construction but this is not problematic for the current set of supported usecases.
* Add a `performance-tests` module to track performance of Sting over time. The tests will initially measure build times and code when compiled to javascript. The results of the performance testing is written up in `performance.md` document to make it easily consumable by other parties. It is expected that over time that these performance tests will expand but they currently focus on the primary performance goals of Sting.

### [v0.04](https://github.com/sting-ioc/sting/tree/v0.04) (2020-02-19) · [Full Changelog](https://github.com/sting-ioc/sting/compare/v0.03...v0.04)

Changes in this release:

* Improve the javadocs organization.
* Build and measure code size of downstream sample projects to help identify track code size changes as the library evolves.
* Generate a suppressable warning when an `@Injectable` annotated type is also annotated with an annotation that is annotated by `@javax.inject.Scope` that is used in other injection frameworks like CDI, Dagger, Guice, GIN etc. This generates a warning as it is unusual that an application uses multiple injection frameworks and the presence of a scoped annotation is more likely the result of a misunderstanding or incomplete conversion from a prior injection framework.
* Generate an error if a `@Fragment` annotated type or a provider method enclosed by the fragment has a jsr330 `@javax.inject.Scope` annotated annotation present.
* Generate an error if an `@Injector` annotated type or an output method enclosed by the injector has a jsr330 `@javax.inject.Scope` annotated annotation present.

### [v0.03](https://github.com/sting-ioc/sting/tree/v0.03) (2020-02-18) · [Full Changelog](https://github.com/sting-ioc/sting/compare/v0.02...v0.03)

Changes in this release:

* Improve the POM generated for both the `sting-core` and `sting-processor` modules.

### [v0.02](https://github.com/sting-ioc/sting/tree/v0.02) (2020-02-17) · [Full Changelog](https://github.com/sting-ioc/sting/compare/v0.01...v0.02)

Changes in this release:

* Upgrade the `org.realityforge.proton` artifacts to version `0.40`.
* Stop generating errors if types are annotated with `@Named`, `@Typed` or `@Eager` and they have a provider annotation.
* Correct bug where a sting provider annotated type was constructing the classname of the target provider relative to the package of the injector rather the package of the declaring type.
* Change the return type of the factory method used to provide `@Injectable` values to return a `java.lang.Object` rather than a package-access type. This is to simplify integration with some downstream tools that assume public methods work with public types and to align with the pattern used for passing package-access dependencies into the factory method.
* Fix broken code that was generated when a binding is not public but the services it provides are public. The code was previously omitting some required casts.

### [v0.01](https://github.com/sting-ioc/sting/tree/v0.01) (2020-02-13) · [Full Changelog](https://github.com/sting-ioc/sting/compare/9e796d0e5c44bee98107f3e65bd394d41bbe07c7...v0.01)

Changes in this release:

 ‎🎉	Initial super-alpha release ‎🎉.
