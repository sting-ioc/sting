load("//third_party/java:rules.bzl", "FORMATTER_JVM_FLAGS", "java_testng")

visibility(["//processor/src/test/java/sting/processor"])

MAIN = "sting.processor.StingProcessorTest"
FIXTURES = "//processor/src/test/fixtures"

def feature(feature_name, trees = ["input", "expected", "expectedFormatted"]):
    return ["%s/%s/com/example/%s:all" % (FIXTURES, tree, feature_name) for tree in trees]

def processor_test(name, data, methods = [], test_class = MAIN, size = "medium", check_nullness = True):
    java_testng(
        name = name,
        srcs = [],
        data = data,
        jvm_flags = ["-Dsting.fixture_dir=processor/src/test/fixtures"] + FORMATTER_JVM_FLAGS + [
            "--add-exports=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED",
            "--add-exports=jdk.compiler/com.sun.tools.javac.jvm=ALL-UNNAMED",
            "--add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
            "--add-opens=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED",
            "--add-exports=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED",
            "--add-exports=jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED",
            "--add-exports=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED",
        ] + ([] if check_nullness else ["-Dsting.plain_javac=true"]),
        test_class = test_class,
        test_methods = [test_class + "." + method for method in methods],
        size = size,
        deps = [":test_classes"],
    )
