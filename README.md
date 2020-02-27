<p align="center"><img src="/assets/icons/logo.png" alt="Sting" width="120"></p>

# Sting

[![Build Status](https://secure.travis-ci.org/sting-ioc/sting.svg?branch=master)](http://travis-ci.org/sting-ioc/sting)
[<img src="https://img.shields.io/maven-central/v/org.realityforge.sting/sting-core.svg?label=latest%20release"/>](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.realityforge.sting%22)
[![codecov](https://codecov.io/gh/sting-ioc/sting/branch/master/graph/badge.svg)](https://codecov.io/gh/sting-ioc/sting)
![GWT3/J2CL compatible](https://img.shields.io/badge/GWT3/J2CL-compatible-brightgreen.svg)

Sting is a fast, easy to use, compile-time dependency injection toolkit. The toolkit accepts a set of annotated
java classes and generates source code to instantiate and wire together the components.

Sting is under heavy development, and sometimes the documentation does not keep up to date. However the goal of
the toolkit is to be easy to use, and this includes clear and concise documentation. If something is unclear
please [report it as a bug](https://github.com/sting-ioc/sting/issues) because it *is* a bug. If a new user
has a hard time, then we need to fix the problem.

For more information about Sting, please see the [Website](https://sting-ioc.github.io/). For the source code
and project support, please visit the [GitHub project](https://github.com/sting-ioc/sting).

# Contributing

The project was released as open source so others could benefit from the project. We are thankful for any
contributions from the community. A [Code of Conduct](CODE_OF_CONDUCT.md) has been put in place and
a [Contributing](CONTRIBUTING.md) document is under development.

# License

Sting is licensed under [Apache License, Version 2.0](LICENSE).

# Credit

* [Stock Software](http://www.stocksoftware.com.au/) for some support maintaining the library.

* [Dagger](https://github.com/google/dagger) proved that this technique was possible and provided the ideas
  that kick-started development.

* [javax.inject](https://github.com/javax-inject/javax-inject) or the JSR-330 Dependency Injection standard for
  Java defines the standard injection API. This is not used by Sting but directly influenced much of the API.
