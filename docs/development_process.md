---
title: Development Process
---
## Build and test

Use Bazel 9.3.0 and JDK 21 or newer to build the Java modules and documentation site. Before submitting changes, run:

    $ tools/check.sh

This checks formatting and the hermeticity baseline, builds the modules and release artifacts, runs all TestNG targets, checks processor and server coverage, and builds and validates the site.

Format Bazel and Starlark files with `bazel run //tools/buildifier:buildifier`. Check formatting and lint without changing files with `bazel run //tools/buildifier:buildifier_check`.

During development, run the target for the code you changed, or the full test suite:

    $ bazel test //processor/src/test/java/sting/processor:factory_goldens
    $ bazel test //:all_tests

Processor golden tests are split by feature, so editing one fixture only invalidates its target. The full suite checks both formatted and unformatted output. When you deliberately change generated output, regenerate that feature's goldens:

    $ tools/update_goldens.sh factory_goldens

Review the fixture diff, then rerun the target. The tests compile Java in process; they do not use a persistent Bazel worker.

To update Java dependencies, edit `third_party/java/dependencies.yml` and run `tools/update_java_deps.sh`. The script checks depgen's pinned SHA-256 before running a downloaded or cached JAR. Format Java with `tools/java_format.sh write`, or use `tools/java_format_watch.sh` while editing. Regenerate expected fixtures through the golden tests.

## Nullness checking

Owned source targets automatically attach JSpecify and NullAway 0.14.2 at error severity. Positive processor
fixtures use an Error Prone 2.42.0 compiler in process, so their generated bodies are checked too. Deliberately
invalid processor fixtures use a separate plain-javac diagnostics target. The generated negative control in
`StingNullabilityTest` must fail with NullAway for both a nullable dereference and a nested generic mismatch.

Sting and generated sources target Java 17 bytecode. Annotation processing and the QA compiler run on Java 21
because Proton requires it; Bazel tools run on Java 25. The build enables
`NullAway:JSpecifyMode=true`, `NullAway:OnlyNullMarked=true` and `-XDaddTypeAnnotationsToSymbol=true`.
Use JDK 22+ or patched JDK 21.0.8+ with the symbol flag when reading compiled type-use contracts. Bazel resolves
the compiler and runtime; check the resolved versions before changing toolchains. The checked test/processor
runtime is Zulu 21.0.12, and the tools runtime is Zulu 25.0.4. See
[NullAway's JSpecify support](https://github.com/uber/NullAway/wiki/JSpecify-Support) for requirements.

The build uses released `proton-core` and `proton-qa` 0.76 from Maven Central. Dependencies are pinned in
`third_party/java/dependencies.yml` and fetched by Bazel. The processor release shades Proton's implementation
and bundled formatter; builds and CI do not require a local Proton checkout.

`tools/release/check_artifacts.py` checks the assembled artifacts: required JAR/source/Javadoc contents,
licenses, POM coordinates and API dependencies, normalized timestamps, processor relocation and service
registration, and Java 17 bytecode for owned and generated classes. It also compiles and runs a consumer with
only the shaded processor jar on the processorpath, preserving inherited JSpecify contracts from compiled
classes. This catches missing processor dependencies and shading errors that source tests cannot exercise.

## Hermeticity

Bazel's sandbox blocks external network access. On macOS, localhost and Unix sockets remain accessible. Dependency fetching and programs launched by `bazel run` are outside the sandbox, so dependency update tools can still download files.

Ahab checks action declarations and tool specifications across `//...`, using the same default configuration locally and in CI. It does not run the build tools or compare their output bytes. Run it with `bazel run` because it queries Bazel's actions:

    $ bazel run //tools/ahab:hermeticity
    $ bazel run //tools/ahab:hermeticity.check
    $ bazel run //tools/ahab:hermeticity.explain

The report fails when it finds violations. The check compares findings with `tools/ahab/baseline.json`; a pass means the findings match the baseline. The baseline and exception list are currently empty. Investigate new findings before running `bazel run //tools/ahab:hermeticity.update`, then review the diff. Custom tool reproducibility specifications live in `tools/ahab/reproducibility.json`.

Java binary and test wrappers omit `build-data.properties`. Deploy JARs use `--exclude_build_data` to remove this metadata and `--normalize` to fix archive timestamps. The release builders also fix every entry's timestamp, including the manifest, and Javadoc uses `-notimestamp` to omit generation dates. `tools/release/check_artifacts.py` checks release archive timestamps and Javadoc dates.

The site build declares its Markdown, templates, navigation, assets, example source jars, and Javadoc inputs. It does not read the clock or generated workspace directories and leaves source documents unchanged. Release signing and publishing use host tools, credentials, and network access outside Ahab's checks.

## Website

The Java generator in `tools/site/` renders Markdown and tables with commonmark-java and HTML templates in `website/`. Building the site needs no Node, npm, or host Python:

    $ bazel build //website:site
    $ tools/site.sh build
    $ tools/site.sh check
    $ tools/site.sh serve

Bazel writes the complete site to `bazel-bin/website/site`. `tools/site.sh build` copies it to `reports/site`, including Javadoc at `/api`. Every build checks local file links and HTML fragments and rejects external browser assets. `tools/site.sh check` checks the copy. External page links are not fetched.

Preview at `http://localhost:3000` with `tools/site.sh serve`. After editing, run `tools/site.sh build` and refresh the browser. Each build includes the whole site; Bazel reuses cached examples and Javadoc.

Documents use `title` and optional `sidebar_label` frontmatter, `{@link: ...}` API links, and `{@file_content: ...}` snippets. Snippets come from compiled example source jars, including generated Java. List each new document or asset in its directory's `BUILD.bazel`. Add documents to `website/navigation.txt` under a `[Category]` heading, in display order. Map assets to their output paths in `website/BUILD.bazel`.

The templates in `website/` use named `{{slots}}`, which the generator fills once. `layout.html` provides the header and footer; `home.html` and `document.html` provide the page content.

CSS, Java/XML highlighting, logos, and favicons are served locally. Highlight.js assets and their license are in `website/static/vendor/highlightjs`; its README records the version, source, and checksums. System fonts avoid external font services. The footer omits the copyright year so the output does not depend on the date.

## Release

A maintainer releases from a clean default-branch checkout after review, outside CI. You need GPG signing access, Maven Central Portal credentials, `gh` access to the repository, and push access to the website repository. Replace `0.41` below with the release version.

1. Run `python3 tools/release/prepare.py 0.41`. Review the changelog notes and the version in `docs/project_setup.md`, then commit them.
2. Run `tools/check.sh`, tag the reviewed commit `v0.41`, and push the commit and tag to origin.
3. Run `tools/release/package.sh 0.41` to build the main, source, and Javadoc JARs and POMs, sign them, write checksums, and create `dist/sting-0.41.zip`. Inspect the bundle. `GPG_USER` selects the signing key; `GPG_PASS` can supply its passphrase through standard input. Use `--unsigned` only for a local preview.
4. Set `MAVEN_CENTRAL_USERNAME` and `MAVEN_CENTRAL_PASSWORD`, then run `tools/release/upload_maven_central.sh 0.41`. It uploads the bundle to Central Portal and waits for publication.
5. Save the reviewed changelog entry to `/tmp/sting-0.41-notes.md` and create the GitHub release with `gh release create v0.41 dist/sting-0.41.zip --title 'Sting 0.41' --notes-file /tmp/sting-0.41-notes.md`.
6. Run `tools/site.sh deploy 0.41` from the release tag to build, check, and publish the website. It pushes a normal commit to `sting-ioc.github.io`.

If a step fails, fix the problem and resume there. Do not upload a Central bundle that is already published.
