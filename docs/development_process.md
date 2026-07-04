---
title: Development Process
---
<nav class="page-toc">

<!-- toc -->

- [Bazel Phase 1](#bazel-phase-1)
- [Publishing](#publishing)
  * [Publishing the Website](#publishing-the-website)

<!-- tocstop -->

</nav>

## Bazel Phase 1

The Bazel build runs in parallel with Buildr and currently covers the main Java modules:
`core`, `doc-examples`, `integration-tests`, `processor`, `server`, and `server-integration-tests`.

Use the Bazel CI-equivalent check before submitting Bazel changes:

    $ tools/check.sh

The check regenerates bazel-depgen outputs, runs buildifier and Java format checks, builds the phase-1 modules,
runs the TestNG targets, and enforces the processor/server unit coverage gate.

When changing Java dependencies, edit `third_party/java/dependencies.yml` or
`tools/java-format/dependencies.yml`, then run:

    $ tools/update_java_deps.sh

When changing Java source in the Bazel formatting scope, use:

    $ tools/java_format.sh write

The coverage gate intentionally uses only the processor and server unit-test targets. Integration tests still run in
`tools/check.sh`, but they are not part of the coverage threshold.

## Publishing

### Publishing the Website

The website is published by running `bundle exec buildr site:deploy` with credentials that can push to
the `sting-ioc.github.io` repository.

Firstly you create the key via the following command.

    $ ssh-keygen -t rsa -b 4096 -C "peter@realityforge.org" -f ../deploy -P ""

This is a private key and should NOT be checked into source code repository.

Finally you add the public part of the deploy key to the repository at
[https://github.com/sting-ioc/sting-ioc.github.io/settings/keys](https://github.com/sting-ioc/sting-ioc.github.io/settings/keys) and
make sure you give the key write access.
