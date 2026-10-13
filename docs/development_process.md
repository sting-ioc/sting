---
title: Development Process
---
## Build and test

Bazel is the build for the Java modules and documentation site. Install Bazel 9.3.0 and JDK 17 or newer. Run the complete local check before submitting changes:

    $ tools/check.sh

This checks Starlark and Java formatting and the Ahab hermeticity baseline, builds the modules and release artifacts, runs all TestNG targets, checks processor and server coverage, and builds and validates the documentation site.

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

Ahab analyzes action declarations and tool specifications; it does not execute tools or prove byte-for-byte reproducibility. The site renderer runs as a Bazel action with declared Markdown, templates, navigation, assets, example source jars, and Javadoc inputs. It reads neither the clock nor generated workspace directories and never rewrites source documents. Release signing and publishing use host tools, credentials, and network access intentionally; these shell workflows remain outside the check.

## Website

The site uses a small Java generator under `tools/site/`, commonmark-java for Markdown and tables, and HTML templates under `website/`. It needs no Node, npm, or host Python. Build it directly with Bazel or use the convenience commands:

    $ bazel build //website:site
    $ tools/site.sh build
    $ tools/site.sh check
    $ tools/site.sh serve

The hermetic output is `bazel-bin/website/site`. `tools/site.sh build` copies the complete site to `reports/site`, including Bazel-generated Javadocs at `/api`. Both the build action and `tools/site.sh check` verify local file links and HTML fragments and reject external browser assets. External page links are not fetched.

Preview with `tools/site.sh serve` at `http://localhost:3000`. After editing, run `tools/site.sh build` again and refresh the browser. There is one complete build; unchanged examples and Javadocs are cached by Bazel.

Documents retain their `title` and optional `sidebar_label` frontmatter, `{@link: ...}` API links, and `{@file_content: ...}` snippets. Snippets come from compiled example source jars, including processor-generated Java. `website/navigation.txt` lists ordered document IDs under `[Category]` headings. `layout.html` provides the shared header/footer; `home.html` and `document.html` are the two page templates. Their named `{{slots}}` are filled once by the generator. When adding a document or asset, list it explicitly in its directory's `BUILD.bazel` and in `website/BUILD.bazel` as needed.

CSS, Java/XML syntax highlighting, logos, and favicons are served locally. The checked-in Highlight.js assets and license live in `website/static/vendor/highlightjs`; its README records the version, upstream source, and checksums. System fonts and a footer without a year keep pages independent of external font services and the build time.

## Release

A release requires a clean default-branch checkout, an explicit version, GPG signing access, Maven Central Portal credentials, `gh` access to the repository, and push access to the website repository. It is run by a maintainer after review, outside CI.

1. Run `python3 tools/release/prepare.py 0.41` with the intended version. Review the moved changelog notes and the version in `docs/project_setup.md`, then commit those changes.
2. Run `tools/check.sh`, tag the reviewed commit `v0.41`, and push the commit and tag to origin.
3. Run `tools/release/package.sh 0.41`. This builds the three main, source, and Javadoc jars and POMs, signs every artifact, writes checksums, and creates `dist/sting-0.41.zip`. Inspect that bundle. `GPG_USER` selects a key; `GPG_PASS` can provide its passphrase through standard input. Use `--unsigned` only for a local preview.
4. Set `MAVEN_CENTRAL_USERNAME` and `MAVEN_CENTRAL_PASSWORD`, then run `tools/release/upload_maven_central.sh 0.41`. It uploads the bundle to Central Portal and waits for publication.
5. Save the reviewed changelog entry to `/tmp/sting-0.41-notes.md`, then create the GitHub release with `gh release create v0.41 dist/sting-0.41.zip --title 'Sting 0.41' --notes-file /tmp/sting-0.41-notes.md`.
6. Run `tools/site.sh deploy 0.41` to build, check, and publish the website from the release tag. It pushes a normal commit to `sting-ioc.github.io`.

If a later step fails, resume from that step after correcting the problem. Do not reupload an already published Central bundle.
