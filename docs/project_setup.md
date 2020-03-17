---
title: Project Setup
---

A Sting project can be setup using any build system that supports configuration of annotation
processors. The following instructions describe how to set up the project using the
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
      <version>0.08</version>
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
          <source>1.8</source>
          <target>1.8</target>
          <useIncrementalCompilation>false</useIncrementalCompilation>
          <annotationProcessorPaths>
            <path>
              <groupId>org.realityforge.sting</groupId>
              <artifactId>sting-processor</artifactId>
              <version>0.08</version>
            </path>
          </annotationProcessorPaths>
        </configuration>
      </plugin>
      ...
    </plugins>
  </build>
</project>
```

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
