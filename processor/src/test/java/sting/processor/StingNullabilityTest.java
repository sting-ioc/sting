package sting.processor;

import static org.testng.Assert.*;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import org.realityforge.proton.qa.Compilation;
import org.realityforge.proton.qa.CompileTestUtil;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public final class StingNullabilityTest extends AbstractStingProcessorTest {
    @Test
    public void generatedNullnessViolationsFailChecking() throws IOException {
        final var synthesizer = new AbstractProcessor() {
            private boolean _generated;

            @Override
            public Set<String> getSupportedAnnotationTypes() {
                return Set.of("*");
            }

            @Override
            public SourceVersion getSupportedSourceVersion() {
                return SourceVersion.RELEASE_17;
            }

            @Override
            public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment round) {
                if (!_generated) {
                    _generated = true;
                    try (final var writer = processingEnv
                            .getFiler()
                            .createSourceFile("samples.InvalidGenerated")
                            .openWriter()) {
                        writer.write("""
                            package samples;
                            @javax.annotation.processing.Generated("NullAwayControl")
                            @org.jspecify.annotations.NullMarked
                            final class InvalidGenerated {
                              static int length(@org.jspecify.annotations.Nullable String text) { return text.length(); }
                              static java.util.List<String> values(
                                  java.util.List<@org.jspecify.annotations.Nullable String> values) { return values; }
                            }
                            """);
                    } catch (final IOException e) {
                        throw new AssertionError(e);
                    }
                }
                return false;
            }
        };
        final Compilation compilation = CompileTestUtil.compile(
                javaCompiler(),
                List.of(source("samples.Control", "package samples; final class Control {}")),
                getOptions(),
                List.of(synthesizer),
                List.of());
        try {
            assertCompilationUnsuccessful(compilation);
            assertEquals(
                    compilation.diagnostics().stream()
                            .filter(d -> d.getKind() == Diagnostic.Kind.ERROR
                                    && d.toString().contains("[NullAway]")
                                    && d.toString().contains("InvalidGenerated.java"))
                            .count(),
                    2L,
                    describeFailureDiagnostics(compilation));
            compilation.assertSourceOutputFilenamePresent("samples/InvalidGenerated.java");
        } finally {
            for (final var directory : List.of(compilation.sourceOutput(), compilation.classOutput())) {
                try (final var paths = Files.walk(directory)) {
                    for (final var path :
                            paths.sorted(Comparator.reverseOrder()).toList()) {
                        Files.delete(path);
                    }
                }
            }
        }
    }

    @Test
    public void nullableRequestMatchesRequiredProvider() {
        final Compilation compilation = compile(List.of(source("samples.Request", """
            package samples;
            import org.jspecify.annotations.*;
            import sting.*;
            @NullMarked
            @Injector(fragmentOnly = false)
            interface Request {
              @Nullable Service service();
              @Injectable class Service {}
            }
            """)));
        assertCompilationSuccessful(compilation);
    }

    @Test
    public void unmarkedRequestsRemainRequired() {
        final Compilation compilation = compile(List.of(source("samples.Unmarked", """
            package samples;
            import sting.*;
            @Injector(inputs = @Injector.Input(type = String.class))
            interface Unmarked { String text(); }
            """)));
        assertCompilationSuccessful(compilation);
    }

    @Test
    public void compiledNullableProviderPreservesOptionality() {
        final Compilation library = compile(List.of(source("samples.Library", """
            package samples;
            import org.jspecify.annotations.*;
            import sting.*;
            @NullMarked @Fragment
            public interface Library {
              default @Nullable String text() { return null; }
            }
            """)));
        assertCompilationSuccessful(library);
        final Compilation nullable = compile(
                List.of(source("samples.NullableRequest", """
                    package samples;
                    import org.jspecify.annotations.*;
                    import sting.*;
                    @NullMarked @Injector(includes = Library.class)
                    interface NullableRequest { @Nullable String text(); }
                    """)),
                buildClasspath(library.classOutput().toFile()));
        assertCompilationSuccessful(nullable);
        final Compilation required = compile(
                List.of(source("samples.RequiredRequest", """
                    package samples;
                    import sting.*;
                    @Injector(includes = Library.class)
                    interface RequiredRequest { String text(); }
                    """)),
                buildClasspath(library.classOutput().toFile()));
        assertCompilationUnsuccessful(required);
        assertErrorDiagnostic(required, "optional provider method or optional injector input");
    }

    @DataProvider(name = "interceptorPhases")
    public Object[][] interceptorPhases() {
        return new Object[][] {
            {"@Before public void before() {}"},
            {
                "@Around public @Nullable Object around(@Proceed Invocation invocation) throws Throwable {"
                        + " return invocation.proceed(); }"
            }
        };
    }

    @Test(dataProvider = "interceptorPhases")
    public void inheritedInterceptorSignaturesPreserveNestedAndArrayNullness(final String lifecycle)
            throws IOException {
        final Compilation compilation =
                compile(List.of(source("samples.Shapes", """
                    package samples;
                    import java.lang.annotation.*;
                    import java.util.*;
                    import org.jspecify.annotations.*;
                    import sting.*;
                    import sting.interceptors.*;
                    @NullMarked
                    public final class Shapes {
                      private Shapes() {}
                      interface Parent {
                        List<@Nullable String> run(@Nullable String @Nullable [] values);
                      }
                      @Trace interface Service extends Parent {}
                      @Injectable @Typed(Service.class)
                      static class Model implements Service {
                        public List<@Nullable String> run(@Nullable String @Nullable [] values) {
                          return Collections.emptyList();
                        }
                      }
                      @Injectable public static class TraceInterceptor {
                        @Before public void before() {}
                      }
                      @InterceptorBinding(implementedBy = "samples.Shapes.TraceInterceptor", priority = 1)
                      @Retention(RetentionPolicy.CLASS) @Target(ElementType.TYPE)
                      @interface Trace {}
                      @Injector(includes = Model.class, fragmentOnly = false)
                      interface Request { Service service(); }
                    }
                    """.replace("@Before public void before() {}", lifecycle))));
        assertCompilationSuccessful(compilation);
        final String proxy = compilation.sourceOutputFilenames().stream()
                .filter(name -> name.endsWith("InterceptorProxy.java"))
                .findFirst()
                .orElseThrow();
        final String code = Files.readString(compilation.sourceOutput().resolve(proxy));
        assertTrue(code.contains("List<@Nullable String>"), code);
        assertTrue(code.contains("@Nullable String @Nullable [] values"), code);
    }

    @DataProvider(name = "legacyDeclarations")
    public Object[][] legacyDeclarations() {
        return new Object[][] {
            {
                "Nullable",
                "METHOD",
                "@sting.Fragment interface Broken { @javax.annotation.Nullable default String value() { return null; }"
                        + " }"
            },
            {
                "Nonnull",
                "PARAMETER",
                "@sting.Injectable class Broken { Broken(@javax.annotation.Nonnull String value) {} }"
            }
        };
    }

    @Test(dataProvider = "legacyDeclarations")
    public void rejectLegacyDeclarations(final String annotation, final String target, final String declaration) {
        final Compilation compilation = compile(List.of(
                source(
                        "javax.annotation." + annotation,
                        "package javax.annotation;"
                                + " @java.lang.annotation.Target(java.lang.annotation.ElementType." + target + ")"
                                + " public @interface " + annotation + " {}"),
                source("samples.Broken", "package samples; " + declaration)));
        assertCompilationUnsuccessful(compilation);
        assertErrorDiagnostic(compilation, "Legacy nullability annotation javax.annotation." + annotation);
    }

    @Test
    public void rejectNullableTypeArgumentsInDiRequests() {
        final Compilation compilation = compile(List.of(source("samples.Unsupported", """
            package samples;
            import org.jspecify.annotations.Nullable;
            import java.util.function.Supplier;
            import sting.Injector;
            @Injector interface Unsupported { Supplier<@Nullable String> value(); }
            """)));
        assertCompilationUnsuccessful(compilation);
        assertErrorDiagnostic(compilation, "Nullable type arguments are not supported in DI requests");
    }

    static JavaFileObject source(final String name, final String code) {
        return new SimpleJavaFileObject(
                URI.create("string:///" + name.replace('.', '/') + ".java"), JavaFileObject.Kind.SOURCE) {
            @Override
            public CharSequence getCharContent(final boolean ignoreEncodingErrors) {
                return code;
            }
        };
    }
}
