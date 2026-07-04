load("@rules_java//java:defs.bzl", _java_binary = "java_binary", _java_library = "java_library", _java_plugin = "java_plugin", _java_test = "java_test")

_JAVA_RELEASE = "17"

_FORMATTER_JDK_EXPORTS = [
    "--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
    "--add-exports=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
    "--add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
    "--add-exports=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED",
    "--add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
    "--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED",
]

FORMATTER_JVM_FLAGS = _FORMATTER_JDK_EXPORTS

_ERROR_PRONE_JAVACOPTS = [
    "-XepExcludedPaths:(.*/" + "external/.*|.*/_javac/.*/.*_sources/.*)",
    "-Xep:AlmostJavadoc:ERROR",
    "-Xep:AlreadyChecked:ERROR",
    "-Xep:AmbiguousMethodReference:ERROR",
    "-Xep:AnnotateFormatMethod:ERROR",
    "-Xep:ArrayAsKeyOfSetOrMap:ERROR",
    "-Xep:AssertEqualsArgumentOrderChecker:ERROR",
    "-Xep:AssertThrowsMultipleStatements:ERROR",
    "-Xep:AttemptedNegativeZero:ERROR",
    "-Xep:BadComparable:ERROR",
    "-Xep:BadImport:ERROR",
    "-Xep:BadInstanceof:ERROR",
    "-Xep:BareDotMetacharacter:ERROR",
    "-Xep:BigDecimalEquals:ERROR",
    "-Xep:BigDecimalLiteralDouble:ERROR",
    "-Xep:BoxedPrimitiveConstructor:ERROR",
    "-Xep:ClassCanBeStatic:ERROR",
    "-Xep:ClassName:ERROR",
    "-Xep:DefaultLocale:ERROR",
    "-Xep:DeprecatedVariable:ERROR",
    "-Xep:DuplicateBranches:ERROR",
    "-Xep:EmptyBlockTag:ERROR",
    "-Xep:EmptyCatch:ERROR",
    "-Xep:EmptyIf:ERROR",
    "-Xep:EmptyTopLevelDeclaration:ERROR",
    "-Xep:EqualsBrokenForNull:ERROR",
    "-Xep:EqualsMissingNullable:ERROR",
    "-Xep:FieldCanBeLocal:ERROR",
    "-Xep:FieldCanBeStatic:ERROR",
    "-Xep:Finalize:ERROR",
    "-Xep:ForEachIterable:ERROR",
    "-Xep:InconsistentHashCode:ERROR",
    "-Xep:LongLiteralLowerCaseSuffix:ERROR",
    "-Xep:MissingBraces:ERROR",
    "-Xep:MissingDefault:ERROR",
    "-Xep:MissingRuntimeRetention:ERROR",
    "-Xep:MixedArrayDimensions:ERROR",
    "-Xep:MultiVariableDeclaration:ERROR",
    "-Xep:MultipleTopLevelClasses:ERROR",
    "-Xep:NonOverridingEquals:ERROR",
    "-Xep:NotJavadoc:ERROR",
    "-Xep:NullOptional:ERROR",
    "-Xep:NullablePrimitive:ERROR",
    "-Xep:NullablePrimitiveArray:ERROR",
    "-Xep:NullableTypeParameter:ERROR",
    "-Xep:NullableWildcard:ERROR",
    "-Xep:PackageLocation:ERROR",
    "-Xep:ParameterMissingNullable:ERROR",
    "-Xep:ParameterName:ERROR",
    "-Xep:PublicApiNamedStreamShouldReturnStream:ERROR",
    "-Xep:RedundantOverride:ERROR",
    "-Xep:RedundantThrows:ERROR",
    "-Xep:RemoveUnusedImports:ERROR",
    "-Xep:ReturnAtTheEndOfVoidFunction:ERROR",
    "-Xep:ReturnFromVoid:ERROR",
    "-Xep:ReturnMissingNullable:ERROR",
    "-Xep:ReturnsNullCollection:ERROR",
    "-Xep:SelfAlwaysReturnsThis:ERROR",
    "-Xep:SunApi:ERROR",
    "-Xep:SystemExitOutsideMain:ERROR",
    "-Xep:ToStringReturnsNull:ERROR",
    "-Xep:UnnecessarilyVisible:ERROR",
    "-Xep:UnnecessaryAnonymousClass:ERROR",
    "-Xep:UnnecessaryBoxedAssignment:ERROR",
    "-Xep:UnnecessaryMethodReference:ERROR",
    "-Xep:UnnecessaryOptionalGet:ERROR",
    "-Xep:UnsynchronizedOverridesSynchronized:ERROR",
    "-Xep:UnusedLabel:ERROR",
    "-Xep:UnusedTypeParameter:ERROR",
    "-Xep:UnusedVariable:ERROR",
    "-Xep:UseCorrectAssertInTests:ERROR",
    "-Xep:UsingJsr305CheckReturnValue:ERROR",
    "-Xep:VoidMissingNullable:ERROR",
    "-Xep:FieldCanBeFinal:ERROR",
    "-Xep:FieldMissingNullable:ERROR",
    "-Xep:PrivateConstructorForUtilityClass:ERROR",
    "-Xep:UnnecessaryDefaultInEnumSwitch:ERROR",
    "-Xep:UnnecessarilyFullyQualified:ERROR",
]

_JAVA_JAVACOPTS = [
    "--release",
    _JAVA_RELEASE,
    "-Werror",
    "-Xlint:all,-processing,-serial,-this-escape",
    "-Xmaxerrs",
    "10000",
    "-Xmaxwarns",
    "10000",
] + _ERROR_PRONE_JAVACOPTS

_JAVA_TEST_JVM_FLAGS = [
    "-ea",
]

def java_library(name, srcs = [], javacopts = [], deps = [], plugins = [], **kwargs):
    _java_library(
        name = name,
        srcs = srcs,
        deps = deps,
        javacopts = _JAVA_JAVACOPTS + javacopts,
        plugins = plugins,
        **kwargs
    )

def java_binary(name, srcs = [], javacopts = [], deps = [], plugins = [], **kwargs):
    _java_binary(
        name = name,
        srcs = srcs,
        deps = deps,
        javacopts = _JAVA_JAVACOPTS + javacopts,
        plugins = plugins,
        **kwargs
    )

def java_plugin(name, srcs = [], javacopts = [], deps = [], **kwargs):
    _java_plugin(
        name = name,
        srcs = srcs,
        deps = deps,
        javacopts = _JAVA_JAVACOPTS + javacopts,
        **kwargs
    )

def java_test(name, srcs = [], javacopts = [], deps = [], plugins = [], jvm_flags = [], **kwargs):
    _java_test(
        name = name,
        srcs = srcs,
        deps = deps,
        javacopts = _JAVA_JAVACOPTS + javacopts,
        plugins = plugins,
        jvm_flags = _JAVA_TEST_JVM_FLAGS + jvm_flags,
        **kwargs
    )

def java_testng(name, srcs, test_classes = [], test_class = None, deps = [], runtime_deps = [], jvm_flags = [], javacopts = [], **kwargs):
    if test_class and test_classes:
        fail("Specify only one of test_class or test_classes")
    if test_class:
        test_classes = [test_class]
    if not test_classes:
        fail("Specify test_class or test_classes")

    filtered_kwargs = dict(kwargs)
    for arg in [
        "main_class",
        "use_testrunner",
        "args",
    ]:
        if arg in filtered_kwargs.keys():
            filtered_kwargs.pop(arg)

    java_test(
        name = name,
        srcs = srcs,
        use_testrunner = False,
        main_class = "org.testng.TestNG",
        args = [
            "-testclass",
            ",".join(test_classes),
        ],
        deps = deps + [
            "//third_party/java:testng",
        ],
        javacopts = javacopts,
        jvm_flags = jvm_flags,
        runtime_deps = runtime_deps,
        **filtered_kwargs
    )
