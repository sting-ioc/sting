---
title: Development Process
---
## Build and test

Bazel is the build for the Java modules. Install Bazel 9.3.0 and JDK 17 or newer. Run the complete local check before submitting changes:

    $ tools/check.sh

This checks Starlark and Java formatting and the Ahab hermeticity baseline, builds the modules and release artifacts, runs all TestNG targets, checks processor and server coverage, and builds the documentation site. Install the site dependencies first with `npm ci` using Node 24.

For a shorter feedback loop, run the relevant Bazel target, for example:

    $ bazel test //processor/src/test/java/sting/processor:factory_goldens
    $ bazel test //:all_tests

The processor tests are split by feature. Each golden target owns only its input and expected fixture subset, so editing one fixture does not invalidate every processor test. The full suite still runs every processor invocation, including formatted and unformatted output checks. Regenerate a feature's goldens explicitly after reviewing a deliberate output change:

    $ tools/update_goldens.sh factory_goldens

Then inspect the fixture diff and rerun the target. The tests use an in-process Java compiler and complete quickly enough without a persistent Bazel worker.

When changing Java dependencies, edit `third_party/java/dependencies.yml` and run `tools/update_java_deps.sh`. The script verifies depgen's pinned SHA-256 before executing either a downloaded or cached JAR. Format Java sources with `tools/java_format.sh write`, or use `tools/java_format_watch.sh` while editing. Generated expected fixtures are managed by the processor tests.

## Hermeticity

Bazel sandboxed actions deny external network access by default; on macOS, Bazel still permits localhost and Unix sockets. Dependency fetching and commands launched by `bazel run` remain outside that action sandbox; tools that deliberately update dependencies can still download them.

Ahab 0.3.0 analyzes the default Bazel configuration across `//...`. Sting has no separate CI configuration. Run these targets with `bazel run`, because Ahab invokes Bazel's action query:

    $ bazel run //tools/ahab:hermeticity
    $ bazel run //tools/ahab:hermeticity.check
    $ bazel run //tools/ahab:hermeticity.explain

The report target exits nonzero when it finds violations. The check compares findings with `tools/ahab/baseline.json`; passing means the findings match, not that the build is hermetic. After investigating a change, run `bazel run //tools/ahab:hermeticity.update` and review the baseline diff. There are currently no exceptions. The formatter worker's reproducibility specification follows Rose's setup and was checked against its UTF-8 input handling and sorted marker output.

The baseline contains no findings. The `java_binary` and `java_test` wrappers set `exclude_build_data = True`; a local patch copied from Rose adds support for this attribute to rules_java 9.9.0. Depgen's `targetRuleLoadSymbols.java_test: false` setting keeps its generated test on Sting's hand-maintained `_java_test` wrapper load across regeneration. The release builders write every archive entry, including the manifest, with a fixed timestamp. Javadoc runs with `-notimestamp` to omit generation dates from HTML. All 10 main, source, and Javadoc release/site jars were reproduced byte-for-byte by repeating their actions after a time delay. `tools/release/check_artifacts.py` rejects archive timestamps and Javadoc generation dates that would regress these fixes.

The `--exclude_build_data` flag removes `build-data.properties` from deploy jars. This is separate from `--normalize`, which normalizes archive timestamps. The previous unstamped deploy jars used redacted build metadata, but omitting that metadata entirely also prevents it from becoming a changing input if stamping is enabled elsewhere.

Ahab analyzes action declarations and tool specifications; it does not execute tools or prove byte-for-byte reproducibility. Shell workflows outside Bazel are also outside this check: the documentation build uses host Node/npm and Python, and `website/siteConfig.js` derives the copyright year from the clock and rewrites the development document's table of contents. The repository's `.node-version` currently selects Node 17.5.0 despite the documented Node 24 prerequisite. Release signing and publishing use host tools, credentials, and network access intentionally. An unchanged Ahab baseline does not cover these workflows.

## Website

The Docusaurus v1 site uses Node 24 and npm. Its content and URLs remain under `docs/` and `website/`.

    $ npm ci
    $ tools/site.sh build
    $ tools/site.sh check
    $ tools/site.sh serve

The built site is in `reports/site`, including Bazel-generated Javadocs at `/api`. The check verifies local links in the built site. External links are not checked.

## Release

A release requires a clean default-branch checkout, an explicit version, GPG signing access, Maven Central Portal credentials, `gh` access to the repository, and push access to the website repository. It is run by a maintainer after review, outside CI.

1. Run `python3 tools/release/prepare.py 0.41` with the intended version. Review the moved changelog notes and the version in `docs/project_setup.md`, then commit those changes.
2. Run `npm ci && tools/check.sh`, tag the reviewed commit `v0.41`, and push the commit and tag to origin.
3. Run `tools/release/package.sh 0.41`. This builds the three main, source, and Javadoc jars and POMs, signs every artifact, writes checksums, and creates `dist/sting-0.41.zip`. Inspect that bundle. `GPG_USER` selects a key; `GPG_PASS` can provide its passphrase through standard input. Use `--unsigned` only for a local preview.
4. Set `MAVEN_CENTRAL_USERNAME` and `MAVEN_CENTRAL_PASSWORD`, then run `tools/release/upload_maven_central.sh 0.41`. It uploads the bundle to Central Portal and waits for publication.
5. Save the reviewed changelog entry to `/tmp/sting-0.41-notes.md`, then create the GitHub release with `gh release create v0.41 dist/sting-0.41.zip --title 'Sting 0.41' --notes-file /tmp/sting-0.41-notes.md`.
6. Run `tools/site.sh deploy 0.41` to build, check, and publish the website from the release tag. It pushes a normal commit to `sting-ioc.github.io`.

If a later step fails, resume from that step after correcting the problem. Do not reupload an already published Central bundle.
