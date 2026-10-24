---
title: Development Process
---
## Build and test

Use the Bazel version pinned in `.bazelversion` and JDK 21 or newer. Run the affected test target while editing, and run `tools/check.sh` before submitting changes:

    $ bazel test //processor/src/test/java/sting/processor:factory_goldens
    $ bazel test //:all_tests
    $ tools/check.sh

The full check also runs `tools/check_bazel_consumer.py`. It verifies the public labels and rejected internal
access from a separate module, then executes a generated injector and the interceptor runtime on the JVM
and as optimized JavaScript. A copied checkout with invalid nullness verifies that J2CL requires the
checked native compilation. Node.js must be available on `PATH` for the JavaScript execution.

Format Java with `tools/java_format.sh write`, or run `tools/java_format_watch.sh` while editing. Format Bazel and Starlark files with `bazel run //tools/buildifier:buildifier`; use `bazel run //tools/buildifier:buildifier_check` to check formatting and lint.

After deliberately changing generated output, regenerate the affected golden family:

    $ tools/update_goldens.sh factory_goldens

Review the changes in both `expected` and `expectedFormatted`, then rerun the test target.

## Dependencies

To update Java dependencies, edit `third_party/java/dependencies.yml` and run `tools/update_java_deps.sh`.

To update Proton, change its `git_override` commit in `MODULE.bazel`. For local Proton development, add this line to the ignored `.bazelrc.local`, using your checkout's absolute path:

    common --override_module=proton_project=/absolute/path/to/proton

Remove the local override when checking the pinned revision used by CI.

When changing packaging or shading, build and check the release artifacts:

    $ bazel build //tools/release:maven_artifacts
    $ python3 tools/release/check_artifacts.py

## Nullness checking

- Mark owned packages or types with `@NullMarked` and use JSpecify's type-use `@Nullable` where needed.
- Use the Java rule wrappers in `third_party/java/rules.bzl` to enable NullAway and Error Prone on owned source targets.
- When generating types from `TypeMirror`, use Proton's `GeneratorUtil.getTypeName` to retain type-use annotations. Plain JavaPoet `TypeName.get` drops them.
- Keep library and generated sources compatible with Java 21. Bazel supplies the compiler; for other builds, follow the [compiler requirements](project_setup.md#compiler-and-jspecify).
- After changing nullness handling or generation, run `bazel test //processor/src/test/java/sting/processor:nullability` and the affected golden targets. Keep deliberately invalid processor fixtures in the plain-javac diagnostics target.

For NullAway builds outside these wrappers, add these compiler options:

    -Xep:NullAway:ERROR
    -XepOpt:NullAway:JSpecifyMode=true
    -XepOpt:NullAway:OnlyNullMarked=true
    -XDaddTypeAnnotationsToSymbol=true

See [NullAway's JSpecify support](https://github.com/uber/NullAway/wiki/JSpecify-Support) for compiler requirements when reading compiled type-use annotations.

## Hermeticity

Declare each action's inputs and tools, and avoid network access during build actions. Run Ahab after changing build rules or tools:

    $ bazel run //tools/ahab:hermeticity
    $ bazel run //tools/ahab:hermeticity.check
    $ bazel run //tools/ahab:hermeticity.explain

Investigate new findings before updating `tools/ahab/baseline.json` with `bazel run //tools/ahab:hermeticity.update`, then review the diff. Record custom tool reproducibility specifications in `tools/ahab/reproducibility.json`.

## Website

Build, check, and preview the site:

    $ tools/site.sh build
    $ tools/site.sh check
    $ tools/site.sh serve

Open `http://localhost:3000`. After editing, rebuild and refresh the browser. The built copy is in `reports/site`, including Javadoc at `/api`; use `bazel build //website:site` for the Bazel output alone. The link checker checks local files and HTML fragments and rejects external browser assets; it does not fetch external page links.

When adding documentation or assets:

- Use `title` and optional `sidebar_label` frontmatter in documents.
- Use `{@link: ...}` for API links and `{@file_content: ...}` for snippets from compiled example source jars, including generated Java.
- List each new document or asset in its directory's `BUILD.bazel`.
- Add documents to `website/navigation.txt` under a `[Category]` heading, in display order.
- Map assets to their output paths in `website/BUILD.bazel` and serve browser assets locally.

Edit the templates in `website/` to change the page layout. Use `layout.html` for the header and footer, and `home.html` or `document.html` for page content.

## Release

Release from a clean default-branch checkout after review, outside CI. You need GPG signing access, Maven Central Portal credentials, `gh` access to the repository, and push access to the website repository. Replace `0.41` below with the release version.

1. Run `python3 tools/release/prepare.py 0.41`. Review the changelog notes and the version in `docs/project_setup.md`, then commit them.
2. Run `tools/check.sh`, tag the reviewed commit `v0.41`, and push the commit and tag to origin.
3. Run `tools/release/package.sh 0.41` to build and sign the artifacts, write checksums, and create `dist/sting-0.41.zip`. Inspect the bundle. `GPG_USER` selects the signing key; `GPG_PASS` can supply its passphrase through standard input. Use `--unsigned` only for a local preview.
4. Set `MAVEN_CENTRAL_USERNAME` and `MAVEN_CENTRAL_PASSWORD`, then run `tools/release/upload_maven_central.sh 0.41`. It uploads the bundle to Central Portal and waits for publication.
5. Save the reviewed changelog entry to `/tmp/sting-0.41-notes.md` and create the GitHub release with `gh release create v0.41 dist/sting-0.41.zip --title 'Sting 0.41' --notes-file /tmp/sting-0.41-notes.md`.
6. Run `tools/site.sh deploy 0.41` from the release tag to build, check, and publish the website. It pushes a normal commit to `sting-ioc.github.io`.

If a step fails, fix the problem and resume there. Do not upload a Central bundle that is already published.
