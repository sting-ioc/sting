# Sting

[![Build Status](https://secure.travis-ci.org/sting-ioc/sting.svg?branch=master)](http://travis-ci.org/sting-ioc/sting)
[![codecov](https://codecov.io/gh/sting-ioc/sting/branch/master/graph/badge.svg)](https://codecov.io/gh/sting-ioc/sting)

## What is Sting?

Sting is a fast, easy to use, compile-time dependency injection toolkit. The toolkit accepts a set of annotated
java classes and generates source code to instantiate and wire together the components.

# Credit

* [Dagger](https://github.com/google/dagger) proved that this technique was possible and provided the ideas
  that kick-started development.

* [javax.inject](https://github.com/javax-inject/javax-inject) or the JSR-330 Dependency Injection standard for
  Java defines the standard injection API. This is not used by Sting but directly influenced much of the API.
