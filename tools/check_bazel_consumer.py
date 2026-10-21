#!/usr/bin/env python3
"""Exercise Sting's public Bazel contract from a separate root module."""

from contextlib import ExitStack
import json
from pathlib import Path
import shutil
import subprocess
import tempfile
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
PUBLIC = {
    "//:core", "//:core-j2cl", "//:server", "//:processor", "//:processor_runtime",
    "//third_party:error_prone_core", "//third_party:nullaway",
    "//third_party:nullaway_plugin", "//third_party:testng",
}


def bazel(workspace, *args, capture=False):
    return subprocess.run(
        ["bazel", *args], cwd=workspace, text=True,
        stdout=subprocess.PIPE if capture else None,
        stderr=subprocess.PIPE if capture else None, check=not capture,
    )


def check_inventory():
    result = bazel(ROOT, "query", "--output=xml", 'siblings(//...)', capture=True)
    if result.returncode:
        raise RuntimeError(result.stderr)
    public = set()
    for target in ET.fromstring(result.stdout):
        visibility = target.find("list[@name='visibility']")
        labels = target.findall("visibility-label") if visibility is None else visibility
        if any(label.get("value", label.get("name")) == "//visibility:public" for label in labels):
            if target.tag != "rule" or target.get("class") != "alias":
                raise RuntimeError(f"Public label must alias its owning package: {target.get('name')}")
            actual = target.find("label[@name='actual']").get("value")
            if actual.split(":")[0] == target.get("name").split(":")[0]:
                raise RuntimeError(f"Public alias must point to its owning package: {target.get('name')}")
            public.add(target.get("name"))
    if public != PUBLIC:
        raise RuntimeError(f"Public surface changed: extra={sorted(public - PUBLIC)}, missing={sorted(PUBLIC - public)}")


def main():
    check_inventory()
    with tempfile.TemporaryDirectory(prefix="sting-consumer-") as directory, ExitStack() as cleanup:
        workspace = Path(directory)
        cleanup.callback(lambda: bazel(workspace, "shutdown", capture=True))
        for name in [".bazelrc", ".bazelversion", "maven_install.json"]:
            shutil.copy2(ROOT / name, workspace / name)
        shutil.copytree(ROOT / "third_party/bazel/patches", workspace / "third_party/bazel/patches")
        module = (ROOT / "MODULE.bazel").read_text().split("# --- depgen-generated repository rules start ---")[0]
        module = module.replace('name = "sting",', 'name = "sting_consumer",', 1)
        module += '\nbazel_dep(name = "sting")\nlocal_path_override(module_name = "sting", path = ' + json.dumps(str(ROOT)) + ')\n'
        (workspace / "MODULE.bazel").write_text(module)
        (workspace / "Consumer.java").write_text('''package sting.consumer;

import jsinterop.annotations.JsMethod;
import org.jspecify.annotations.NullMarked;
import sting.Injectable;
import sting.Injector;
import sting.interceptors.Invocation;

@NullMarked
public final class Consumer {
  @Injectable
  public static final class Service {
    Service() {}
    int value() { return 42; }
  }

  @Injector
  public interface Graph {
    Service service();
  }

  @JsMethod
  public static void verify() throws Throwable {
    final Graph graph = new Consumer_Sting_Graph();
    final Service first = graph.service();
    final Service second = graph.service();
    if (first.value() != 42 || first != second) {
      throw new AssertionError("Generated injector did not cache its service");
    }
    final Invocation invocation = new Invocation(arguments -> arguments[0], new Object[] {"original"});
    if (!"original".equals(invocation.proceed()) || !"replacement".equals(invocation.proceed(new Object[] {"replacement"}))) {
      throw new AssertionError("Interceptor invocation lost its arguments");
    }
  }
}
''')
        (workspace / "ConsumerTest.java").write_text('''package sting.consumer;

import org.jspecify.annotations.NullMarked;
import sting.processor.StingProcessor;
import sting.server.Transactional;

@NullMarked
public final class ConsumerTest {
  public static void main(String[] args) throws Throwable {
    Consumer.verify();
    if (new StingProcessor().getSupportedAnnotationTypes().isEmpty() || !Transactional.class.isAnnotation()) {
      throw new AssertionError("Published processor or server is unavailable");
    }
  }
}
''')
        (workspace / "entry.js").write_text("goog.module('sting.consumer.entry');\nconst Consumer = goog.require('sting.consumer.Consumer');\nConsumer.verify();\n")
        build = '''load("@rules_java//java:defs.bzl", "java_test")
load("@j2cl//build_defs:rules.bzl", "j2cl_library", "j2cl_import")
load("@rules_closure//closure:defs.bzl", "closure_js_library", "closure_js_binary")
java_test(
    name = "consumer_test",
    srcs = ["Consumer.java", "ConsumerTest.java"],
    main_class = "sting.consumer.ConsumerTest",
    use_testrunner = False,
    plugins = ["@sting//:processor"],
    deps = ["@sting//:core", "@sting//:server", "@sting//:processor_runtime", "@j2cl//:jsinterop-annotations", "@maven//:org_jspecify_jspecify"],
)
j2cl_import(name = "jspecify", jar = "@maven//:org_jspecify_jspecify")
j2cl_library(
    name = "browser",
    srcs = ["Consumer.java"],
    experimental_enable_jspecify_support_do_not_enable_without_jspecify_static_checking_or_you_might_cause_an_outage = True,
    plugins = ["@sting//:processor"],
    deps = ["@sting//:core-j2cl", "@j2cl//:jsinterop-annotations-j2cl", ":jspecify"],
)
closure_js_library(name = "entry", srcs = ["entry.js"], deps = [":browser"])
closure_js_binary(name = "application", deps = [":entry"], compilation_level = "ADVANCED", entry_points = ["goog:sting.consumer.entry"])
filegroup(name = "tooling", srcs = ["@sting//third_party:error_prone_core", "@sting//third_party:nullaway", "@sting//third_party:nullaway_plugin", "@sting//third_party:testng"])
'''
        (workspace / "BUILD.bazel").write_text(build)
        bazel(workspace, "test", "//:consumer_test")
        bazel(workspace, "build", "//:application", "//:tooling")
        result = bazel(workspace, "cquery", "//:application", "--output=files", capture=True)
        if result.returncode:
            raise RuntimeError(result.stderr)
        javascript = [workspace / line for line in result.stdout.splitlines() if line.endswith(".js")]
        if len(javascript) != 1:
            raise RuntimeError(f"Expected one executable JavaScript file, got {javascript}")
        subprocess.run(["node", str(javascript[0])], cwd=workspace, check=True)
        denied = [
            "//core", "//processor", "//processor:sting_processor_plugin", "//server",
            "//core/src/main/java/sting:Injector.java", "//tools/release:core_sources",
            "//tools/release:processor_jar", "//processor/src/test/fixtures:all",
            "//third_party/java:jspecify", "//third_party/java:jakarta_transaction_api",
            "//core:published", "//server:published", "//processor:published_plugin",
            "//processor:published_runtime", "//core:_core_j2cl",
            "//core:_core_j2cl-j2wasm", "//core:lib_core_j2cl.jar",
        ]
        for label in denied:
            (workspace / "BUILD.bazel").write_text(f'filegroup(name = "probe", srcs = ["@sting{label}"])\n')
            result = bazel(workspace, "build", "//:probe", "--nobuild", capture=True)
            if not result.returncode or "not visible" not in result.stderr:
                raise RuntimeError(f"Expected visibility denial for {label}:\n{result.stderr}")
        for label, symbol in [
            ("//third_party/java:rules.bzl", "java_library"),
            ("//tools/release:release.bzl", "release_jar"),
            ("//tools/site:site.bzl", "sting_site"),
            ("//processor/src/test/java/sting/processor:test_rules.bzl", "processor_test"),
        ]:
            (workspace / "BUILD.bazel").write_text(f'load("@sting{label}", "{symbol}")\nfilegroup(name = "probe")\n')
            result = bazel(workspace, "build", "//:probe", "--nobuild", capture=True)
            if not result.returncode or "visibility" not in result.stderr.lower():
                raise RuntimeError(f"Expected load visibility denial for {label}:\n{result.stderr}")
        # A source-archive J2CL build must require the checked JVM class outputs.
        invalid_sting = workspace / "invalid-sting"
        invalid_sting.mkdir()
        for name in ["BUILD.bazel", "MODULE.bazel", "MODULE.bazel.lock", "maven_install.json"]:
            shutil.copy2(ROOT / name, invalid_sting / name)
        for name in ["core", "third_party", "tools/release"]:
            shutil.copytree(ROOT / name, invalid_sting / name)
        shutil.copy2(ROOT / "LICENSE", invalid_sting / "LICENSE")
        invocation = invalid_sting / "core/src/main/java/sting/interceptors/Invocation.java"
        invocation.write_text(invocation.read_text().replace(
            "    private final Invoker _invoker;",
            "    public static String nullnessProbe() { return null; }\n\n    private final Invoker _invoker;",
            1,
        ))
        (workspace / "BUILD.bazel").write_text('filegroup(name = "unused")\n')
        result = bazel(workspace, "build", "@sting//:core-j2cl", f"--override_module=sting={invalid_sting}", capture=True)
        if not result.returncode or "[NullAway]" not in result.stderr or "nullnessProbe" not in result.stderr:
            raise RuntimeError("J2CL bypassed the native nullness checks:\n" + result.stderr)
        print("Public surface, Java/J2CL runtime, visibility denials and native nullness enforcement passed.")


if __name__ == "__main__":
    main()
