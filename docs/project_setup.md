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
          <source>21</source>
          <target>21</target>
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
on patched Java 21 compilers. Library and generated classes target Java 21. `sting-core` depends on
`org.jspecify:jspecify:1.0.1`, which supplies the annotations on the compilation classpath. The shaded
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

The Java libraries `@sting//:core` and `@sting//:server` import the binary and source JARs used for Maven
Central releases. `@sting//:processor` is the annotation processor plugin, backed by the shaded Java library
`@sting//:processor_runtime`. Use the runtime library only when code instantiates the processor or tooling
needs its JAR.

Browser applications use `@sting//:core-j2cl`, which shares one J2CL compilation of the checked core source
archive, including the interceptor runtime. Depend on this target directly rather than recompiling Sting
sources or importing its Java bytecode as annotations. Generated Wasm/Kotlin companions, source files,
release artifacts and build macros are internal.

The curated dependency labels `@sting//third_party:error_prone_core`, `@sting//third_party:nullaway`,
`@sting//third_party:nullaway_plugin`, and `@sting//third_party:testng` support existing shared checker and
test infrastructure.

Declare an unreleased checkout in the application's `MODULE.bazel`:

```starlark
bazel_dep(name = "sting")
local_path_override(module_name = "sting", path = "../sting")
```

Bazel applies module overrides only from the root application. A fresh root must also configure the
compiler dependencies and overrides in Sting's `MODULE.bazel`, including Proton, the formatter rules,
J2CL, rules_java and rules_cc. Copy the referenced patches into the application root and adjust their
labels there; Bazel requires override patches to belong to the root module. Copy `maven_install.json`
when using Sting's locked browser compiler dependency configuration. These root requirements also apply
to Java-only consumers because Bazel resolves the complete module graph.

Keep an existing Arez or React4j application's shared compiler configuration. The checked configuration
is maintained in [Sting's MODULE.bazel](https://github.com/sting-ioc/sting/blob/master/MODULE.bazel),
rather than duplicated here. `tools/check_bazel_consumer.py` demonstrates a complete standalone root:
it copies the compiler setup, Maven lock and patches, then adds Sting as an external dependency.

Use Bazel 9.3.0 and configure the consumer's `.bazelrc` with Sting's Java toolchains; dependency
repositories' `.bazelrc` files are not inherited:

```text
common --java_language_version=21
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
    plugins = ["@sting//:processor"],
    deps = ["@sting//:core"],
)
```

Add `@sting//:server` for transaction integrations. If application source imports JSpecify or Jakarta
Transactions types directly, declare those dependencies in the application build as well. Sting's internal
dependency labels are not public APIs.

For J2CL, load `j2cl_library` from `@j2cl//build_defs:rules.bzl`, use the same processor plugin, and replace
`@sting//:core` with `@sting//:core-j2cl` in `deps`. Select a J2CL toolchain compatible with the root build.
Sting pins its compiler in `MODULE.bazel`; module overrides belong to the application root, so applications
sharing a compiler with Arez or React4j must retain that root's J2CL configuration. The external consumer
check in `tools/check_bazel_consumer.py` exercises both the Java and J2CL products.

The source targets `//core`, `//processor`, `//processor:sting_processor_plugin`, and `//server` remain
available for development inside Sting with visibility restricted to their internal readers.

## Configure your IDE

It is expected that most Sting applications are developed from within an IDE. The configuration of the IDE
can be done by importing the `pom.xml` into the IDE but further customizations may need to be done by
the user.

Now that the build environment is setup, you can move on to [getting started](getting_started.md) creating
the application code.
