---
title: Project Setup
---

A Sting project can be setup using any build system that supports configuration of annotation
processors. Library and generated contracts use JSpecify; see the
[nullness migration guide](dependency_kinds.md#nullness-and-migration) for annotations and supported DI forms.

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

Use JDK 22+, patched JDK 17.0.19+, or patched JDK 21.0.8+ when reading JSpecify contracts from compiled
classes. The symbol flag in the example makes type-use annotations available to Sting on the patched older
compilers. The Java output remains at version 17. `sting-core` publishes a dependency on
`org.jspecify:jspecify:1.0.0`, which supplies the annotations on the compilation classpath. The shaded
processor contains its own canonical JSpecify runtime classes for the isolated processorpath.

To check application and generated bodies, configure Error Prone and NullAway with JSpecify mode and marked
scopes as described in the [development process](development_process.md#nullness-checking-and-local-proton).
Unmarked application implementations remain an optimistic boundary.

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

## Configure your IDE

It is expected that most Sting applications are developed from within an IDE. The configuration of the IDE
can be done by importing the `pom.xml` into the IDE but further customizations may need to be done by
the user.

## Configure a GWT Application

If you are using Sting within a GWT application you will also need to inherit the GWT module via:

```xml
<module>
  ...
  <inherits name='sting.Sting'/>
  ...
</module>
```

Now that the build environment is setup, you can move on to [getting started](getting_started.md) creating
the application code.
