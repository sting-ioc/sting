# Sting: A simple, compile-time dependency injection toolkit

[![Build Status](https://secure.travis-ci.org/realityforge/sting.svg?branch=master)](http://travis-ci.org/realityforge/sting)
[![codecov](https://codecov.io/gh/realityforge/sting/branch/master/graph/badge.svg)](https://codecov.io/gh/realityforge/sting)

## What is Sting?

Sting is a super simple, compile time dependency injection toolkit. The toolkit accepts a set of annotated
java classes and generates source code to instantiate and wire together the components.

# Credit

* [Dagger](https://github.com/google/dagger) proved that this technique was possible and provided the ideas
  that kick-started development.

* [javax.inject](https://github.com/javax-inject/javax-inject) or the JSR-330 Dependency Injection standard for
  Java defines the standard injection API. This is not used by Sting but directly influenced much of the API.
