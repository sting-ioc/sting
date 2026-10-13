#!/usr/bin/env python3
"""Verify the Maven artifacts' public shape and processor shading."""

import sys
import xml.etree.ElementTree as ET
import zipfile
from pathlib import Path


ARTIFACTS = {
    "sting-core": ("sting/Injectable.class", "sting/Injectable.java"),
    "sting-processor": ("sting/processor/StingProcessor.class", "sting/processor/StingProcessor.java"),
    "sting-server": ("sting/server/Transactional.class", "sting/server/Transactional.java"),
}


def entries(path: Path) -> set[str]:
    with zipfile.ZipFile(path) as jar:
        return set(jar.namelist())


def main() -> None:
    root = Path(__file__).resolve().parents[2] / "bazel-bin" / "tools" / "release"
    version = sys.argv[1] if len(sys.argv) > 1 else "0.0.0-SNAPSHOT"
    for artifact, (class_name, source_name) in ARTIFACTS.items():
        jar = entries(root / f"{artifact}.jar")
        sources = entries(root / f"{artifact}-sources.jar")
        javadoc = entries(root / f"{artifact}-javadoc.jar")
        if class_name not in jar or source_name not in sources or "index.html" not in javadoc:
            raise SystemExit(f"Incomplete {artifact} artifact set")
        if "META-INF/LICENSE" not in jar:
            raise SystemExit(f"Missing {artifact} license")
        pom = ET.parse(root / f"{artifact}.pom").getroot()
        namespace = {"m": "http://maven.apache.org/POM/4.0.0"}
        for field, expected in (("artifactId", artifact), ("version", version)):
            if pom.findtext(f"m:{field}", namespaces=namespace) != expected:
                raise SystemExit(f"Wrong {field} in {artifact} POM")
        if artifact == "sting-processor" and list(pom.find("m:dependencies", namespace)):
            raise SystemExit("Processor POM must not declare runtime dependencies")

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
    service = "META-INF/services/javax.annotation.processing.Processor"
    if service not in processor:
        raise SystemExit("Missing processor service registration")
    with zipfile.ZipFile(root / "sting-processor.jar") as jar:
        if "sting.processor.StingProcessor" not in jar.read(service).decode():
            raise SystemExit("Wrong processor service registration")
    print("Checked 12 Maven artifacts and processor shading")


if __name__ == "__main__":
    main()
