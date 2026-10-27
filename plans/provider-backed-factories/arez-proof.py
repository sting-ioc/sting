#!/usr/bin/env python3
"""Temporary delivery evidence: compile and run actual Arez-generated products."""

import hashlib
import pathlib
import subprocess
import tempfile
import urllib.request

ROOT = pathlib.Path(__file__).resolve().parents[2]
BAZEL = ["bazel"]
ARTIFACTS = [
    ("org.realityforge.arez", "arez-core", "0.256"),
    ("org.realityforge.arez", "arez-processor", "0.256"),
    ("org.realityforge.braincheck", "braincheck-core", "1.35.0"),
    ("org.realityforge.grim", "grim-annotations", "0.09"),
    ("org.realityforge.javax.annotation", "javax.annotation", "1.1.1"),
    ("com.google.jsinterop", "jsinterop-annotations", "2.0.0"),
    ("org.realityforge.org.jetbrains.annotations", "org.jetbrains.annotations", "1.7.0"),
    ("org.jspecify", "jspecify", "1.0.0"),
]
SOURCES = {
    "Backend.java": """
package proof;
interface Backend {}
@sting.Injectable @sting.Typed(Backend.class)
final class BackendImpl implements Backend {}
""",
    "Model.java": """
package proof;
@org.jspecify.annotations.NullMarked
@arez.annotations.ArezComponent(
    sting = arez.annotations.Feature.DISABLE,
    defaultReadOutsideTransaction = arez.annotations.Feature.ENABLE)
public abstract class Model {
    private final Backend backend;
    private final int count;
    Model(Backend backend, int count) { this.backend = backend; this.count = count; }
    public Backend backend() { return backend; }
    public int count() { return count; }
    @arez.annotations.Observable(initializer = arez.annotations.Feature.ENABLE)
    public abstract @org.jspecify.annotations.Nullable String getLabel();
    public abstract void setLabel(@org.jspecify.annotations.Nullable String label);
}
""",
    "InterfaceModel.java": """
package proof;
@arez.annotations.ArezComponent(
    sting = arez.annotations.Feature.DISABLE,
    defaultReadOutsideTransaction = arez.annotations.Feature.ENABLE)
public interface InterfaceModel {
    @arez.annotations.Observable(initializer = arez.annotations.Feature.ENABLE)
    int getCount();
    void setCount(int count);
}
""",
    "ModelFactory.java": """
package proof;
@sting.Factory
public interface ModelFactory {
    Model create(int count, @org.jspecify.annotations.Nullable String label);
    InterfaceModel createInterface(int count);
}
""",
    "Main.java": """
package proof;
@sting.Injector(fragmentOnly = false, includes = {ModelFactory.class, BackendImpl.class})
interface MyInjector { ModelFactory factory(); }
public final class Main {
    public static void main(String[] args) {
        final var factory = new Sting_MyInjector().factory();
        final var first = factory.create(23, null);
        final var second = factory.create(42, "second");
        if (first == second || first.count() != 23 || second.count() != 42
            || first.backend() == null || first.backend() != second.backend()
            || first.getLabel() != null || !"second".equals(second.getLabel())
            || first.getClass().isAnnotationPresent(sting.Injectable.class)
            || !first.getClass().getSimpleName().equals("Arez_Model")) {
            throw new AssertionError("Incorrect Arez model factory");
        }
        final var a = factory.createInterface(7);
        final var b = factory.createInterface(9);
        if (a == b || a.getCount() != 7 || b.getCount() != 9
            || a.getClass().isAnnotationPresent(sting.Injectable.class)
            || !a.getClass().getSimpleName().equals("Arez_InterfaceModel")) {
            throw new AssertionError("Incorrect Arez interface factory");
        }
        System.out.println("PASS: actual Arez class/interface implementations, shared injected backend, "
            + "distinct products, caller values, nullable observable initializer, no product Injectable");
    }
}
""",
}


def run(args, **kwargs):
    return subprocess.run(args, cwd=ROOT, check=True, text=True, **kwargs)


run(BAZEL + ["build", "//core", "//processor"])
execution_root = pathlib.Path(run(BAZEL + ["info", "execution_root"], capture_output=True).stdout.strip())
java_bin = execution_root / "external/rules_java++toolchains+remotejdk21_linux/bin"
assert (java_bin / "javac").is_file()
outputs = run(BAZEL + ["cquery", "set(//core //processor)", "--output=starlark",
              '--starlark:expr="\\n".join([f.path for f in providers(target)["@@rules_java+//java/private:java_info.bzl%JavaInfo"].transitive_runtime_jars.to_list()])'],
              capture_output=True).stdout.splitlines()
sting_jars = [str(execution_root / p) for p in outputs if p.endswith(".jar")]
assert sting_jars

with tempfile.TemporaryDirectory(prefix="sting-arez-proof-") as temporary:
    directory = pathlib.Path(temporary)
    jars = []
    for group, artifact, version in ARTIFACTS:
        url = f"https://repo.maven.apache.org/maven2/{group.replace('.', '/')}/{artifact}/{version}/{artifact}-{version}.jar"
        data = urllib.request.urlopen(url).read()
        jar = directory / f"{artifact}-{version}.jar"
        jar.write_bytes(data)
        jars.append(str(jar))
        print(f"SHA256 {jar.name} {hashlib.sha256(data).hexdigest()}", flush=True)
    sources = directory / "src"
    sources.mkdir()
    for name, content in SOURCES.items():
        (sources / name).write_text(content)
    classpath = ":".join(sting_jars + jars)
    for processors in ["arez.processor.ArezProcessor,sting.processor.StingProcessor",
                       "sting.processor.StingProcessor,arez.processor.ArezProcessor"]:
        output = directory / processors.split(',')[0]
        generated = output / "generated"
        classes = output / "classes"
        generated.mkdir(parents=True)
        classes.mkdir()
        exports = [f"-J--add-exports=jdk.compiler/com.sun.tools.javac.{package}=ALL-UNNAMED"
                   for package in ["api", "code", "file", "parser", "tree", "util"]]
        run([str(java_bin / "javac"), *exports, "-cp", classpath, "-sourcepath", "", "-processorpath", classpath,
             "-processor", processors, "-parameters", "-d", str(classes), "-s", str(generated),
             *[str(p) for p in sources.glob('*.java')]])
        factory = (generated / "proof/Sting_ModelFactory.java").read_text()
        assert "new Arez_Model(" in factory and "new Arez_InterfaceModel(" in factory
        for model in ["Arez_Model.java", "Arez_InterfaceModel.java"]:
            assert "@Injectable" not in (generated / "proof" / model).read_text()
        print(f"Processor order: {processors}", flush=True)
        run([str(java_bin / "java"), "-ea", "-cp", str(classes) + ":" + classpath, "proof.Main"])
