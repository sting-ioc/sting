---
title: Project Setup
---

A Sting project can be setup using any build system that supports configuration of annotation
processors. Library and generated contracts use JSpecify; see the
[nullness documentation](dependency_kinds.md#nullness) for annotations and supported dependency forms.

The following instructions describe how to set up the project using the
[Apache Maven](https://maven.apache.org) build tool as it is relatively well known within the Java ecosystem.

## Configure Maven

To configure Maven to support Sting you need to add a dependency on the annotations library as well as
configure the javac compiler to use the Sting annotation processor.

The Sting annotations are included in the `sting-core` artifact. To add this library to
your Maven project, simply add the following to your `pom.xml`:

```xml
<project>
  ...
  <dependencies>
    ...
    <dependency>
      <groupId>org.realityforge.sting</groupId>
      <artifactId>sting-core</artifactId>
      <version>0.40</version>
    </dependency>
    ...
  </dependencies>
</project>
```

To enable the Sting annotation processor, you need add the following snippet to configure the maven
compiler plugin from within the `pom.xml`:

```xml
<project>
  ...
  <plugins>
    ...
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.5.1</version>
        <configuration>
          <source>17</source>
          <target>17</target>
          <compilerArgs>
            <arg>-XDaddTypeAnnotationsToSymbol=true</arg>
          </compilerArgs>
          <useIncrementalCompilation>false</useIncrementalCompilation>
          <annotationProcessorPaths>
            <path>
              <groupId>org.realityforge.sting</groupId>
              <artifactId>sting-processor</artifactId>
              <version>0.40</version>
            </path>
          </annotationProcessorPaths>
        </configuration>
      </plugin>
      ...
    </plugins>
  </build>
</project>
```

### Compiler and JSpecify

The Sting processor requires Java 21 or newer. Use JDK 22+ or patched JDK 21.0.8+ when reading JSpecify
contracts from compiled classes. The symbol flag in the example makes type-use annotations available to Sting
on patched Java 21 compilers. Library and generated classes target Java 17. `sting-core` depends on
`org.jspecify:jspecify:1.0.0`, which supplies the annotations on the compilation classpath. The shaded
processor reads and generates canonical annotation names without bundling JSpecify on its isolated processorpath.

To check application and generated bodies, configure Error Prone and NullAway with JSpecify mode and marked
scopes as described in the [development process](development_process.md#nullness-checking).
NullAway trusts unmarked application implementations; mark and check them to verify their nullness contracts.

### Optional Generated Source Formatting

By default, the Sting processor formats generated Java source before writing it. To disable generated
source formatting, pass the literal annotation processor option `-Asting.format_generated_source=false`.

The formatter support is supplied by Proton and shaded inside the `sting-processor` artifact, but the
formatter uses JDK compiler internals. On JDK 16 and later, the JVM running `javac` and annotation
processing must receive these exports unless generated source formatting is disabled:

```text
--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED
```

For Maven, put the `--add-exports` flags in `.mvn/jvm.config` or `MAVEN_OPTS` so they reach the
Maven JVM that runs annotation processing. If the compiler plugin forks `javac`, pass the same
exports with `-J`, for example
`-J--add-exports=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED`, so they reach the forked
compiler JVM.

## Configure Bazel

The public targets `@sting//:core`, `@sting//:processor`, and `@sting//:server` import the binary and
source JARs used for Maven Central releases. Core depends on JSpecify; server depends on core and Jakarta
Transactions. The processor JAR bundles its runtime dependencies, including the formatter, and is used by
`@sting//:sting_processor_plugin`.

To use an unreleased checkout, copy Sting's `rules_java` patch into the consumer's root directory.
Bazel requires override patches to belong to the root module:

```sh
cp ../sting/third_party/bazel/patches/rules_java_exclude_build_data.patch .
```

Then add this to the consumer's `MODULE.bazel`:

```starlark
bazel_dep(name = "sting")
local_path_override(module_name = "sting", path = "../sting")

bazel_dep(name = "rules_java", version = "9.9.0")
single_version_override(
    module_name = "rules_java",
    patch_strip = 1,
    patches = ["//:rules_java_exclude_build_data.patch"],
    version = "9.9.0",
)

git_override(
    module_name = "proton_project",
    commit = "6d50db451021b7880097d0880907aa036a30a689",
    remote = "https://github.com/realityforge/proton.git",
)

archive_override(
    module_name = "rules_palantir_java_format",
    integrity = "sha256-4h/h0cVmPQ/7jkXnvW69rmABbfXOOzoXuBh3gMJDMk8=",
    strip_prefix = "rules_palantir_java_format-0.1.1",
    urls = ["https://github.com/realityforge/rules_palantir_java_format/releases/download/v0.1.1/rules_palantir_java_format-v0.1.1.tar.gz"],
)
```

Bazel only applies module overrides from the root module. These overrides select the pinned Proton
revision, the formatter rule module, and Sting's patch to omit build metadata from Java deploy JARs.

Use Bazel 9.3.0 and configure the consumer's `.bazelrc` with Sting's Java toolchains; dependency
repositories' `.bazelrc` files are not inherited:

```text
common --java_language_version=17
common --java_runtime_version=remotejdk_21
common --tool_java_language_version=25
common --tool_java_runtime_version=remotejdk_25
```

Then configure Java targets in `BUILD.bazel`:

```starlark
load("@rules_java//java:defs.bzl", "java_library")

java_library(
    name = "app",
    srcs = ["App.java"],
    plugins = ["@sting//:sting_processor_plugin"],
    deps = ["@sting//:core"],
)
```

Add `@sting//:server` for transaction integrations. If application source imports JSpecify or Jakarta
Transactions types directly, also add `@sting//third_party/java:jspecify` or
`@sting//third_party/java:jakarta_transaction_api` to `deps` to satisfy Bazel's strict dependency checks.

The source targets `//core`, `//processor`, `//processor:sting_processor_plugin`, and `//server` remain
available for development inside Sting.

## Configure your IDE

It is expected that most Sting applications are developed from within an IDE. The configuration of the IDE
can be done by importing the `pom.xml` into the IDE but further customizations may need to be done by
the user.

Now that the build environment is setup, you can move on to [getting started](getting_started.md) creating
the application code.
