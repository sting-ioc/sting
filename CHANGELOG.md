# Change Log

### Unreleased

* Improve the javadocs organization.

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
