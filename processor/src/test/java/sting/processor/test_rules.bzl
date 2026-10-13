load("//third_party/java:rules.bzl", "FORMATTER_JVM_FLAGS", "java_testng")

MAIN = "sting.processor.StingProcessorTest"
FIXTURES = "//processor/src/test/fixtures"

def feature(feature_name, trees = ["input", "expected", "expectedFormatted"]):
    return ["%s/%s/com/example/%s:all" % (FIXTURES, tree, feature_name) for tree in trees]

def processor_test(name, data, methods = [], test_class = MAIN, size = "medium"):
    java_testng(
        name = name,
        srcs = [],
        data = data,
        jvm_flags = ["-Dsting.fixture_dir=processor/src/test/fixtures"] + FORMATTER_JVM_FLAGS,
        test_class = test_class,
        test_methods = [test_class + "." + method for method in methods],
        size = size,
        deps = [":test_classes"],
    )
