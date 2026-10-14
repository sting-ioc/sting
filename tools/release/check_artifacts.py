#!/usr/bin/env python3
"""Verify the Maven artifacts' public shape and processor shading."""

import os
import re
import subprocess
import sys
import tempfile
import textwrap
import xml.etree.ElementTree as ET
import zipfile
from pathlib import Path


ARTIFACTS = {
    "sting-core": ("sting/Injectable.class", "sting/Injectable.java"),
    "sting-processor": ("sting/processor/StingProcessor.class", "sting/processor/StingProcessor.java"),
    "sting-server": ("sting/server/Transactional.class", "sting/server/Transactional.java"),
}

LEGACY_DESCRIPTOR = re.compile(
    rb"L(?:javax/annotation/(?:Nonnull|Nullable|CheckForNull)|org/jetbrains/annotations/(?:NotNull|Nullable));"
)


def check_owned_nullability(workspace: Path) -> None:
    legacy = re.compile(
        r"javax\.annotation\.(?:Nonnull|Nullable|CheckForNull)|org\.jetbrains\.annotations\.(?:NotNull|Nullable)|@Nonnull\b"
    )
    allowed = {"processor/src/main/java/sting/processor/StingProcessor.java",
               "processor/src/test/java/sting/processor/StingNullabilityTest.java"}
    roots = ["core", "processor", "server", "integration-tests", "server-integration-tests", "doc-examples", "docs", "tools/site"]
    files = [workspace / "README.md", workspace / "AGENTS.md"]
    for root in roots:
        files.extend(path for path in (workspace / root).rglob("*") if path.suffix in (".java", ".md"))
    for path in files:
        text = path.read_text()
        if legacy.search(text) and (str(path.relative_to(workspace)) not in allowed
                                    or re.search(r"(?m)^\s*import\s+(?:" + legacy.pattern + ")", text)):
            raise SystemExit(f"Legacy nullability reference in owned source/documentation: {path}")
    print("Checked owned sources, fixtures, goldens and current documentation for legacy nullability")


def check_nullness_metadata(path: Path) -> None:
    with zipfile.ZipFile(path) as jar:
        for name in jar.namelist():
            # Formatter classes are third-party code bundled by Proton.
            if name.endswith(".class") and name.startswith("sting/") and "/vendor/proton/vendor/" not in name:
                if LEGACY_DESCRIPTOR.search(jar.read(name)):
                    raise SystemExit(f"Legacy nullability annotation descriptor in {path.name}: {name}")
            if name.endswith(".class") and name.startswith("sting/") and "/vendor/" not in name:
                check_java_17_bytecode(jar.read(name), f"{path.name}: {name}")
        if b"Lorg/jspecify/annotations/NullMarked;" not in jar.read(
            "sting/processor/package-info.class" if path.name == "sting-processor.jar" else
            "sting/server/package-info.class" if path.name == "sting-server.jar" else "sting/package-info.class"
        ):
            raise SystemExit(f"Missing NullMarked package contract in {path.name}")


def check_java_17_bytecode(data: bytes, name: str) -> None:
    if data[:4] != b"\xca\xfe\xba\xbe" or int.from_bytes(data[6:8], "big") != 61:
        raise SystemExit(f"Expected Java 17 bytecode: {name}")


def run(command: list[str], cwd: Path) -> str:
    result = subprocess.run(command, cwd=cwd, text=True, capture_output=True)
    if result.returncode:
        raise SystemExit(f"Command failed: {' '.join(command)}\n{result.stdout}{result.stderr}")
    return result.stdout.strip()


def check_packaged_consumer(workspace: Path, artifacts: Path) -> None:
    execution_root = Path(run(["bazel", "info", "execution_root"], workspace))
    java_home = execution_root / run(
        ["bazel", "cquery", "config(@rules_java//toolchains:current_java_runtime, target)", "--output=starlark",
         '--starlark:expr=providers(target)["ToolchainInfo"].java_runtime.java_home'], workspace
    )
    jspecify = execution_root / run(["bazel", "cquery", "config(@jspecify//file, target)", "--output=files"], workspace)
    compiler = str(java_home / "bin" / "javac")
    with tempfile.TemporaryDirectory(prefix="sting-packaged-consumer-") as directory:
        work = Path(directory)
        classes = work / "classes"
        classes.mkdir()
        generated = work / "generated"
        generated.mkdir()
        parent = work / "Parent.java"
        parent.write_text(textwrap.dedent("""\
            package packaged;
            @org.jspecify.annotations.NullMarked
            public interface Parent {
              java.util.List<@org.jspecify.annotations.Nullable String> run(
                  @org.jspecify.annotations.Nullable String @org.jspecify.annotations.Nullable [] values);
            }
            """))
        classpath = os.pathsep.join(map(str, [artifacts / "sting-core.jar", jspecify, classes]))
        common = [compiler, "--release", "17", "-Werror", "-Xlint:all,-processing", "-XDaddTypeAnnotationsToSymbol=true", "-classpath", classpath,
                  "-d", str(classes)]
        run(common + ["-proc:none", str(parent)], workspace)
        consumer = work / "Consumer.java"
        consumer.write_text(textwrap.dedent("""\
            package packaged;
            import java.lang.annotation.*;
            import java.util.*;
            import org.jspecify.annotations.*;
            import sting.*;
            import sting.interceptors.*;
            @NullMarked
            public final class Consumer {
              private Consumer() {}
              @Trace interface Service extends Parent {}
              @Injectable @Typed(Service.class)
              static class Model implements Service {
                public List<@Nullable String> run(@Nullable String @Nullable [] values) {
                  return Collections.singletonList(null);
                }
              }
              @Injectable public static class TraceInterceptor {
                @Around public @Nullable Object around(@Proceed Invocation invocation) throws Throwable {
                  return invocation.proceed();
                }
              }
              @InterceptorBinding(implementedBy = "packaged.Consumer.TraceInterceptor", priority = 1)
              @Retention(RetentionPolicy.CLASS) @Target(ElementType.TYPE)
              @interface Trace {}
              @Injector(includes = Model.class, fragmentOnly = false)
              interface Request { Service service(); }
              public static void main(String[] arguments) {
                List<@Nullable String> result = new Consumer_Sting_Request().service().run(null);
                if (result.size() != 1 || result.get(0) != null) { throw new AssertionError(result); }
              }
            }
            """))
        exports = [f"-J--add-exports=jdk.compiler/com.sun.tools.javac.{package}=ALL-UNNAMED"
                   for package in ("api", "code", "file", "parser", "tree", "util")]
        run(common + exports + ["-processorpath", str(artifacts / "sting-processor.jar"),
                               "-processor", "sting.processor.StingProcessor",
                               "-Asting.format_generated_source=true", "-s", str(generated), str(consumer)], workspace)
        proxies = list(generated.rglob("*InterceptorProxy.java"))
        if len(proxies) != 1:
            raise SystemExit(f"Expected one packaged interceptor proxy, found {len(proxies)}")
        source = proxies[0].read_text()
        if "List<@Nullable String>" not in source or "@Nullable String @Nullable []" not in source:
            raise SystemExit("Packaged processor lost compiled inherited type-use nullness:\n" + source)
        for path in classes.rglob("*.class"):
            check_java_17_bytecode(path.read_bytes(), str(path.relative_to(classes)))
        run([str(java_home / "bin" / "java"), "-ea", "-classpath", classpath, "packaged.Consumer"], workspace)
    print("Compiled Java 17 consumer bytecode and ran it with an isolated shaded processorpath and compiled JSpecify contracts")


def entries(path: Path) -> set[str]:
    with zipfile.ZipFile(path) as jar:
        for entry in jar.infolist():
            if entry.date_time != (1980, 1, 1, 0, 0, 0):
                raise SystemExit(f"Non-normalized timestamp in {path.name}: {entry.filename}")
            if entry.filename.endswith(".html") and re.search(
                rb"<!-- Generated by javadoc [^\r\n]* on ", jar.read(entry)
            ):
                raise SystemExit(f"Javadoc generation timestamp in {path.name}: {entry.filename}")
        return set(jar.namelist())


def main() -> None:
    workspace = Path(__file__).resolve().parents[2]
    check_owned_nullability(workspace)
    root = workspace / "bazel-bin" / "tools" / "release"
    version = sys.argv[1] if len(sys.argv) > 1 else "0.0.0-SNAPSHOT"
    for artifact, (class_name, source_name) in ARTIFACTS.items():
        jar = entries(root / f"{artifact}.jar")
        sources = entries(root / f"{artifact}-sources.jar")
        javadoc = entries(root / f"{artifact}-javadoc.jar")
        if class_name not in jar or source_name not in sources or "index.html" not in javadoc:
            raise SystemExit(f"Incomplete {artifact} artifact set")
        if "META-INF/LICENSE" not in jar:
            raise SystemExit(f"Missing {artifact} license")
        check_nullness_metadata(root / f"{artifact}.jar")
        pom = ET.parse(root / f"{artifact}.pom").getroot()
        namespace = {"m": "http://maven.apache.org/POM/4.0.0"}
        for field, expected in (("artifactId", artifact), ("version", version)):
            if pom.findtext(f"m:{field}", namespaces=namespace) != expected:
                raise SystemExit(f"Wrong {field} in {artifact} POM")
        if artifact == "sting-processor" and list(pom.find("m:dependencies", namespace)):
            raise SystemExit("Processor POM must not declare runtime dependencies")
        if artifact == "sting-core":
            dependencies = [tuple(dependency.findtext(f"m:{field}", namespaces=namespace)
                                  for field in ("groupId", "artifactId", "version"))
                            for dependency in pom.find("m:dependencies", namespace)]
            if dependencies != [("org.jspecify", "jspecify", "1.0.0")]:
                raise SystemExit(f"Wrong core nullability dependencies: {dependencies}")

    processor = entries(root / "sting-processor.jar")
    if "sting/processor/vendor/javapoet/JavaFile.class" not in processor:
        raise SystemExit("Missing relocated Javapoet")
    if not any(name.startswith("sting/processor/vendor/proton/") for name in processor):
        raise SystemExit("Missing relocated Proton")
    if any(name.startswith(("com/palantir/javapoet/", "org/realityforge/proton/")) for name in processor):
        raise SystemExit("Unrelocated processor dependency")
    with zipfile.ZipFile(root / "sting-processor.jar") as jar:
        if any(b"javax/json" in jar.read(name) for name in processor if name.endswith(".class")):
            raise SystemExit("Processor jar still references javax.json")
        for annotation in ("NullMarked", "NullUnmarked", "Nullable", "NonNull"):
            if f"org/jspecify/annotations/{annotation}.class" not in processor:
                raise SystemExit(f"Missing canonical processor runtime annotation: {annotation}")
        if b"getTypeName" not in jar.read("sting/processor/vendor/proton/GeneratorUtil.class"):
            raise SystemExit("Processor jar does not contain the Proton type-use converter")
        if int.from_bytes(jar.read("sting/processor/vendor/proton/GeneratorUtil.class")[6:8], "big") != 65:
            raise SystemExit("Expected the Java 21 Proton implementation in the processor")
    with zipfile.ZipFile(root / "sting-core.jar") as jar:
        if b"Lorg/jspecify/annotations/Nullable;" not in jar.read("sting/interceptors/Invocation.class"):
            raise SystemExit("Missing JSpecify Invocation nullness metadata")
    service = "META-INF/services/javax.annotation.processing.Processor"
    if service not in processor:
        raise SystemExit("Missing processor service registration")
    with zipfile.ZipFile(root / "sting-processor.jar") as jar:
        if "sting.processor.StingProcessor" not in jar.read(service).decode():
            raise SystemExit("Wrong processor service registration")
    print("Checked 12 Maven artifacts and processor shading")
    check_packaged_consumer(workspace, root.resolve())


if __name__ == "__main__":
    main()
