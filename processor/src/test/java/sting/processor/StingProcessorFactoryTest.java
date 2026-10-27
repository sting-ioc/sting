package sting.processor;

import static org.testng.Assert.*;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import org.realityforge.proton.qa.Compilation;
import org.realityforge.proton.qa.CompileTestUtil;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public final class StingProcessorFactoryTest extends AbstractStingProcessorTest {
    private static final String PACKAGE = "com.example.factory.providers";
    private static final String IMPLEMENTATION = """
        @NullMarked final class ComponentImpl extends Component {
            private final Service service;
            private final int count;
            private final @Nullable String label;

            ComponentImpl(Service service, int count, @Nullable String label) {
                this.service = service;
                this.count = count;
                this.label = label;
            }

            public Service service() { return service; }
            public int count() { return count; }
            public @Nullable String label() { return label; }
        }
        """;

    @DataProvider
    public Object[][] declarationKinds() {
        return new Object[][] {{"abstract class"}, {"interface"}, {"class"}};
    }

    @Test(dataProvider = "declarationKinds")
    public void constructsMappedImplementation(final String kind) throws Exception {
        final List<JavaFileObject> sources = componentSources(kind);
        sources.add(source("ComponentImpl", implementation(kind)));
        final Compilation compilation = compile(sources);
        assertCompilationSuccessful(compilation);
        assertGeneratedConstruction(compilation);
        runHarness(compilation);
    }

    @Test
    public void preservesCapturedDependencyQualifier() throws Exception {
        final List<JavaFileObject> sources = componentSources("abstract class");
        sources.removeIf(s -> s.getName().endsWith("/Service.java"));
        sources.add(source("Service", """
            @NullMarked interface Service {}
            @NullMarked @Injectable @Named("backend") @Typed(Service.class)
            final class ServiceImpl implements Service {}
            """));
        sources.add(source(
                "ComponentImpl",
                IMPLEMENTATION
                        .replace("final class ComponentImpl", "@Injectable final class ComponentImpl")
                        .replace(
                                "ComponentImpl(Service service,",
                                "ComponentImpl(@Named(\"backend\") Service service,")));
        final Compilation compilation = compile(sources);
        assertCompilationSuccessful(compilation);
        runHarness(compilation);
    }

    @DataProvider
    public Object[][] invalidImplementations() {
        return new Object[][] {
            {"interface ComponentImpl {}", "create a concrete class"},
            {"abstract class ComponentImpl extends Component {}", "create a concrete class"},
            {"class ComponentImpl {}", "is not assignable to"},
            {"class ComponentImpl<T> extends Component {}", "create a class with type parameters"},
            {
                "class ComponentImpl extends Component { private ComponentImpl(int count) {} }",
                "constructor accessible from the factory interface"
            },
            {
                "class ComponentImpl extends Component { ComponentImpl() {} ComponentImpl(int count) {} }",
                "single accessible constructor"
            },
            {
                "class ComponentImpl extends Component { ComponentImpl(String count) {} }",
                "parameters whose name and type match"
            }
        };
    }

    @Test(dataProvider = "invalidImplementations")
    public void rejectsInvalidImplementation(final String implementation, final String diagnostic) {
        final Compilation compilation = compile(List.of(
                framework("ComponentImpl"),
                source("Component", "@Framework abstract class Component {}"),
                source("ComponentImpl", implementation),
                source("ComponentFactory", "@Factory interface ComponentFactory { Component create(int count); }")));
        assertFalse(compilation.success());
        assertDiagnosticContaining(compilation, diagnostic);
    }

    @Test
    public void rejectsNonStaticImplementation() {
        final Compilation compilation = compile(List.of(
                framework("Holder.Implementation"),
                source("Component", "@Framework abstract class Component {}"),
                source("Holder", "class Holder { class Implementation extends Component {} }"),
                source("ComponentFactory", "@Factory interface ComponentFactory { Component create(); }")));
        assertFalse(compilation.success());
        assertDiagnosticContaining(compilation, "non-static nested class");
    }

    @Test
    public void rejectsMultipleMappings() {
        final Compilation compilation = compile(List.of(
                framework("ComponentImpl"),
                source("OtherFramework", "@StingProvider(\"OtherImpl\") @interface OtherFramework {}"),
                source("Component", "@Framework @OtherFramework abstract class Component {}"),
                source("ComponentFactory", "@Factory interface ComponentFactory { Component create(); }")));
        assertFalse(compilation.success());
        assertDiagnosticContaining(compilation, "multiple @StingProvider annotations");
    }

    @Test
    public void rejectsMalformedMapping() {
        final Compilation compilation = compile(List.of(
                source("Framework", """
                    @interface StingProvider { int value(); }
                    @StingProvider(1) @interface Framework {}
                    """),
                source("Component", "@Framework abstract class Component {}"),
                source("ComponentFactory", "@Factory interface ComponentFactory { Component create(); }")));
        assertFalse(compilation.success());
        assertDiagnosticContaining(compilation, "invalid @StingProvider annotation");
    }

    @Test(dataProvider = "unmappedDeclarations")
    public void rejectsUnmappedDeclaration(final String declaration, final String diagnostic) {
        final Compilation compilation = compile(List.of(
                source("Component", declaration + " Component {}"),
                source("ComponentFactory", "@Factory interface ComponentFactory { Component create(); }")));
        assertFalse(compilation.success());
        assertDiagnosticContaining(compilation, diagnostic);
    }

    @DataProvider
    public Object[][] unmappedDeclarations() {
        return new Object[][] {{"abstract class", "abstract class"}, {"interface", "class type"}};
    }

    @Test
    public void rejectsRawGenericDeclarationEvenWithNonGenericImplementation() {
        final Compilation compilation = compile(List.of(
                framework("ComponentImpl"),
                source("Component", "@Framework interface Component<T> {}"),
                source("ComponentImpl", "class ComponentImpl implements Component<String> {}"),
                source("ComponentFactory", "@Factory interface ComponentFactory { Component create(); }")));
        assertFalse(compilation.success());
        assertDiagnosticContaining(compilation, "return a class with type parameters");
    }

    @DataProvider
    public Object[][] generatorOrders() {
        return new Object[][] {{true, false}, {false, false}, {true, true}, {false, true}};
    }

    @Test(dataProvider = "generatorOrders")
    public void waitsForGeneratedImplementationAndConstructorTypes(
            final boolean generatorFirst, final boolean delayedDependency) throws Exception {
        final Processor generator = implementationGenerator(delayedDependency);
        final List<Processor> processors =
                generatorFirst ? List.of(generator, processor()) : List.of(processor(), generator);
        final Compilation compilation = CompileTestUtil.compile(
                javaCompiler(), componentSources("abstract class"), getOptions(), processors, List.of());
        assertCompilationSuccessful(compilation);
        assertGeneratedConstruction(compilation);
        runHarness(compilation);
        if (delayedDependency) {
            compilation.assertJavaClassPresent(PACKAGE + ".GeneratedDependency");
        }
    }

    @Test
    public void reportsMissingImplementationAtEndOfProcessing() {
        final Compilation compilation = compile(List.of(
                framework("MissingImplementation"),
                source("Component", "@Framework abstract class Component {}"),
                source("ComponentFactory", "@Factory interface ComponentFactory { Component create(); }")));
        assertFalse(compilation.success());
        assertDiagnosticContaining(compilation, "@Factory method create returns " + PACKAGE + ".Component");
        assertDiagnosticContaining(compilation, "expects implementation " + PACKAGE + ".MissingImplementation");
        assertDiagnosticContaining(compilation, "no such class exists");
    }

    @Test
    public void reportsMissingImplementationWhenDeferralDisabled() {
        final var options = new ArrayList<>(getOptions());
        options.add("-Asting.defer.unresolved=false");
        final Compilation compilation = CompileTestUtil.compile(
                javaCompiler(),
                List.of(
                        framework("MissingImplementation"),
                        source("Component", "@Framework abstract class Component {}"),
                        source("ComponentFactory", "@Factory interface ComponentFactory { Component create(); }")),
                options,
                processors(),
                List.of());
        assertFalse(compilation.success());
        assertDiagnosticContaining(compilation, "expects implementation " + PACKAGE + ".MissingImplementation");
    }

    @Test
    public void reportsUnresolvedImplementationTypesAtEndOfProcessing() {
        final Compilation compilation = compile(List.of(
                framework("ComponentImpl"),
                source("Component", "@Framework abstract class Component {}"),
                source("ComponentImpl", "class ComponentImpl extends Component { ComponentImpl(Missing value) {} }"),
                source("ComponentFactory", "@Factory interface ComponentFactory { Component create(); }")));
        assertFalse(compilation.success());
        assertDiagnosticContaining(compilation, "expects implementation " + PACKAGE + ".ComponentImpl");
        assertDiagnosticContaining(compilation, "its types could not be resolved");
    }

    @Test
    public void resolvesSeparatelyCompiledImplementation() throws Exception {
        final List<JavaFileObject> allSources = componentSources("interface");
        final List<JavaFileObject> consumerSources = allSources.stream()
                .filter(s -> s.getName().endsWith("/ComponentFactory.java")
                        || s.getName().endsWith("/Harness.java"))
                .toList();
        final var componentSources = new ArrayList<>(allSources);
        componentSources.removeAll(consumerSources);
        componentSources.add(source("ComponentImpl", implementation("interface")));
        final var options = new ArrayList<>(getOptions());
        options.add("-parameters");
        final Compilation componentCompilation =
                CompileTestUtil.compile(javaCompiler(), componentSources, options, processors(), List.of());
        assertCompilationSuccessful(componentCompilation);

        final Compilation factoryCompilation = compile(
                consumerSources,
                buildClasspath(componentCompilation.classOutput().toFile()));
        assertCompilationSuccessful(factoryCompilation);
        assertGeneratedConstruction(factoryCompilation);
        try (final var loader = new URLClassLoader(
                new URL[] {
                    factoryCompilation.classOutput().toUri().toURL(),
                    componentCompilation.classOutput().toUri().toURL()
                },
                getClass().getClassLoader())) {
            loader.loadClass(PACKAGE + ".Harness").getMethod("verify").invoke(null);
        }
    }

    private Processor implementationGenerator(final boolean delayedDependency) {
        return new AbstractProcessor() {
            private int round;

            @Override
            public Set<String> getSupportedAnnotationTypes() {
                return Set.of("*");
            }

            @Override
            public SourceVersion getSupportedSourceVersion() {
                return SourceVersion.RELEASE_21;
            }

            @Override
            public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment env) {
                if (!env.processingOver()) {
                    if (0 == round) {
                        final String implementation = delayedDependency
                                ? IMPLEMENTATION
                                        .replace(
                                                "ComponentImpl(Service service,",
                                                "ComponentImpl(Service service, GeneratedDependency dependency,")
                                        .replace(
                                                "this.service = service;",
                                                "this.service = dependency.service(service);")
                                : IMPLEMENTATION;
                        emit("ComponentImpl", implementation);
                    } else if (1 == round && delayedDependency) {
                        emit(
                                "GeneratedDependency",
                                "@NullMarked @Injectable final class GeneratedDependency { Service service(Service"
                                        + " service) { return service; } }");
                    }
                    round++;
                }
                return false;
            }

            private void emit(final String name, final String content) {
                try (final var writer = processingEnv
                        .getFiler()
                        .createSourceFile(PACKAGE + "." + name)
                        .openWriter()) {
                    writer.append(source(name, content).getCharContent(true));
                } catch (final IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        };
    }

    private List<JavaFileObject> componentSources(final String kind) {
        final String declaration = "interface".equals(kind)
                ? """
                @NullMarked @Framework public interface Component {
                    Service service();
                    int count();
                    @Nullable String label();
                }
                """
                : "@NullMarked @Framework public " + kind + " Component {\n"
                        + "  public Service service() { throw new UnsupportedOperationException(); }\n"
                        + "  public int count() { return -1; }\n"
                        + "  public @Nullable String label() { return null; }\n"
                        + "}\n";
        return new ArrayList<>(List.of(
                framework("[SimpleName]Impl"),
                source("Component", declaration),
                source("Service", """
                    @NullMarked interface Service {}
                    @NullMarked @Injectable @Typed(Service.class) final class ServiceImpl implements Service {}
                    """),
                source("ComponentFactory", """
                    @NullMarked @Factory public interface ComponentFactory {
                        Component create(int count, @Nullable String label);
                    }
                    """),
                source("Harness", """
                    @NullMarked @sting.Injector(fragmentOnly = false, includes = {ComponentFactory.class, ServiceImpl.class})
                    interface MyInjector { ComponentFactory factory(); }
                    @NullMarked public class Harness {
                        public static void verify() {
                            final var injector = new Sting_MyInjector();
                            final var factory = injector.factory();
                            final var first = factory.create(23, null);
                            final var second = factory.create(42, "second");
                            if (first == second || first.count() != 23 || second.count() != 42
                                || first.label() != null || !"second".equals(second.label())
                                || first.service() != second.service()
                                || !first.getClass().getSimpleName().equals("ComponentImpl")) {
                                throw new AssertionError("Incorrect factory construction");
                            }
                        }
                    }
                    """)));
    }

    private String implementation(final String kind) {
        return "interface".equals(kind)
                ? IMPLEMENTATION.replace("extends Component", "implements Component")
                : IMPLEMENTATION;
    }

    private JavaFileObject framework(final String pattern) {
        return source("Framework", """
            @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
            @interface StingProvider { String value(); }
            @StingProvider("%s")
            @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
            @interface Framework {}
            """.formatted(pattern));
    }

    private JavaFileObject source(final String name, final String content) {
        final String qualifiedName = PACKAGE + "." + name;
        final String text = "package " + PACKAGE
                + ";\n"
                + "import sting.*;\n"
                + "import org.jspecify.annotations.Nullable;\n"
                + "import org.jspecify.annotations.NullMarked;\n"
                + content;
        return new SimpleJavaFileObject(
                URI.create("string:///" + qualifiedName.replace('.', '/') + ".java"), JavaFileObject.Kind.SOURCE) {
            @Override
            public CharSequence getCharContent(final boolean ignoreEncodingErrors) {
                return text;
            }
        };
    }

    private void assertGeneratedConstruction(final Compilation compilation) throws Exception {
        final String generated = Files.readString(
                compilation.sourceOutput().resolve(PACKAGE.replace('.', '/') + "/Sting_ComponentFactory.java"));
        assertTrue(generated.contains("Component create("), generated);
        assertTrue(generated.contains("new ComponentImpl("), generated);
        assertFalse(generated.contains("new Component("), generated);
    }

    private void runHarness(final Compilation compilation) throws Exception {
        try (final var loader = new URLClassLoader(
                new URL[] {compilation.classOutput().toUri().toURL()},
                getClass().getClassLoader())) {
            loader.loadClass(PACKAGE + ".Harness").getMethod("verify").invoke(null);
        }
    }

    private void assertDiagnosticContaining(final Compilation compilation, final String text) {
        final String diagnostics = compilation.diagnostics().toString();
        assertTrue(diagnostics.contains(text), diagnostics);
    }
}
